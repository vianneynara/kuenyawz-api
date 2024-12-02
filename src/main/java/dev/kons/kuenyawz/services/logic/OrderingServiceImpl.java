package dev.kons.kuenyawz.services.logic;

import dev.kons.kuenyawz.configurations.ApplicationProperties;
import dev.kons.kuenyawz.dtos.midtrans.TransactionRequest;
import dev.kons.kuenyawz.dtos.midtrans.TransactionResponse;
import dev.kons.kuenyawz.dtos.purchase.PurchaseDto;
import dev.kons.kuenyawz.dtos.purchase.PurchasePostDto;
import dev.kons.kuenyawz.entities.Account;
import dev.kons.kuenyawz.entities.ClosedDate;
import dev.kons.kuenyawz.entities.Purchase;
import dev.kons.kuenyawz.entities.Transaction;
import dev.kons.kuenyawz.exceptions.IllegalOperationException;
import dev.kons.kuenyawz.mapper.PurchaseMapper;
import dev.kons.kuenyawz.repositories.PurchaseRepository;
import dev.kons.kuenyawz.repositories.TransactionRepository;
import dev.kons.kuenyawz.repositories.TransactionSpec;
import dev.kons.kuenyawz.services.entity.CartItemService;
import dev.kons.kuenyawz.services.entity.ClosedDateService;
import dev.kons.kuenyawz.services.entity.PurchaseService;
import dev.kons.kuenyawz.services.entity.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderingServiceImpl implements OrderingService {

	private final PurchaseService purchaseService;
	private final TransactionService transactionService;
	private final MidtransApiService midtransApiService;
	private final TransactionRepository transactionRepository;
	private final PurchaseRepository purchaseRepository;
	private final PurchaseMapper purchaseMapper;
	private final ApplicationProperties properties;
	private final ClosedDateService closedDateService;
	private final WhatsappApiService whatsappApiService;
	private final CartItemService cartItemService;

	@Override
	public PurchaseDto processOrder(PurchasePostDto PurchasePostDto) {
		// Initialize required entities
		Account account = AuthService.getAuthenticatedAccount();

		// Checks for ongoing transaction
		Specification<Transaction> spec = TransactionSpec.withAccountId(account.getAccountId());
		Pageable pageable = Pageable.unpaged();
		Page<Transaction> transactions = transactionRepository.findAll(spec, pageable);
		transactions.stream()
			.filter(t ->
				t.getStatus() == Transaction.TransactionStatus.CREATED
					|| t.getStatus() == Transaction.TransactionStatus.PENDING)
			.findAny()
			.ifPresent(t -> {
				throw new IllegalOperationException("There's already an ongoing transaction");
			});

		// Checks for date overlap
		LocalDate eventDate = LocalDate.parse(PurchasePostDto.getEventDate());
		LocalDate prepDate2 = eventDate.minusDays(1);
		LocalDate prepDate1 = eventDate.minusDays(2);

		if (LocalDate.now().isAfter(prepDate1.minusDays(1))) {
			throw new IllegalOperationException("Cannot create within 2 days before event date");
		}

		if (!closedDateService.getAllBetween(prepDate1, eventDate).isEmpty()) {
			throw new IllegalOperationException(String.format("Cannot create purchase on a closed date: %s ~ %s",
				prepDate1, eventDate));
		}

		Purchase purchase = purchaseService.create(PurchasePostDto);

		// Build transaction (not saved yet)
		Transaction transaction = transactionService.build(purchase, account);

		List<TransactionRequest.ItemDetail> items = TransactionRequest.ItemDetail.of(purchase.getPurchaseItems());
		if (purchase.getDeliveryFee() != null) {
			items.add(TransactionRequest.ItemDetail.builder()
				.id("delivery_fee")
				.name("Delivery Fee")
				.price(purchase.getDeliveryFee())
				.quantity(1)
				.build());
		}
		items.add(TransactionRequest.ItemDetail.builder()
			.id("service_fee")
			.name("Service Fee")
			.price(BigDecimal.valueOf(properties.vendor().getPaymentFee()))
			.quantity(1)
			.build());

		// Create the request body
		TransactionRequest request = TransactionRequest.builder()
			.transactionDetails(TransactionRequest.TransactionDetails.of(purchase, transaction.getTransactionId(), properties))
			.itemDetails(items)
			.customerDetails(TransactionRequest.CustomerDetails.of(purchase, account))
			.expiry(TransactionRequest.Expiry.defaultExpiry())
			.build();

		// Send the request to payment gateway
		TransactionResponse response = midtransApiService.createTransaction(request);

		// Save the transaction
		transaction.setPaymentUrl(response.getRedirectUrl());
		transaction.setReferenceId(response.getTransactionId());
		Transaction savedTransaction = transactionRepository.save(transaction);

		// Update purchase DTO with the transaction
		PurchaseDto purchaseDto = purchaseMapper.toDto(purchase);
		purchaseDto.setTransactions(List.of(transactionService.convertToDto(savedTransaction, account, purchase)));

		closedDateService.save(Set.of(
			ClosedDate.builder().date(prepDate1).closureType(ClosedDate.ClosureType.PREP).build(),
			ClosedDate.builder().date(prepDate2).closureType(ClosedDate.ClosureType.PREP).build(),
			ClosedDate.builder().date(eventDate).closureType(ClosedDate.ClosureType.RESERVED).build()
		));

		cartItemService.deleteCartItemsOfAccount(account.getAccountId());

		try {
			final String message = String.format("Pesanan dengan kode pemesanan %s sudah dibuat. Harap menyelesaikan pembayaran anda untuk mengkonfirmasi jadwal %s",
				purchase.getPurchaseId(), response.getRedirectUrl());
			whatsappApiService.send(account.getPhone(), message, "62");
		} catch (Exception e) {
			log.error("Failed to send order creation notification to {}, error: ", account.getPhone(), e);
		}

		return purchaseDto;
	}

	@Override
	public PurchaseDto cancelOrder(Long purchaseId) {
		Purchase purchase = purchaseService.getById(purchaseId);

		if (purchase.getStatus() == Purchase.PurchaseStatus.CANCELLED) {
			throw new IllegalOperationException("Purchase is already cancelled");
		} else if (purchase.getStatus() == Purchase.PurchaseStatus.DELIVERED) {
			throw new IllegalOperationException("Cannot cancel delivered purchase");
		}

		transactionService.validateOwnership(purchaseId, AuthService.getAuthenticatedAccount().getAccountId());

		LocalDate currentDate = LocalDate.now();
		if (currentDate.isAfter(purchase.getEventDate())) {
			throw new IllegalOperationException("Cannot cancel purchase after event date");
		} else if (currentDate.isAfter(purchase.getEventDate().minusDays(2)) && !AuthService.isAuthenticatedAdmin()) {
			throw new IllegalOperationException("Cannot cancel purchase during preparation period");
		}

		transactionService.cancelAllOf(purchaseId);

		purchase.setStatus(Purchase.PurchaseStatus.CANCELLED);
		Purchase savedPurchase = purchaseRepository.save(purchase);

		closedDateService.deleteBetween(
			purchase.getEventDate().minusDays(2),
			purchase.getEventDate()
		);

		return purchaseMapper.toDto(savedPurchase);
	}
}
