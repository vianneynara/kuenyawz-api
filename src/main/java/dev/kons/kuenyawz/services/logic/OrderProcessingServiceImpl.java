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
import dev.kons.kuenyawz.services.entity.ClosedDateService;
import dev.kons.kuenyawz.services.entity.PurchaseService;
import dev.kons.kuenyawz.services.entity.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProcessingServiceImpl implements OrderProcessingService {

	private final PurchaseService purchaseService;
	private final TransactionService transactionService;
	private final MidtransApiService midtransApiService;
	private final TransactionRepository transactionRepository;
	private final PurchaseRepository purchaseRepository;
	private final PurchaseMapper purchaseMapper;
	private final ApplicationProperties properties;
	private final ClosedDateService closedDateService;
	private final WhatsappApiService whatsappApiService;

	@Override
	public PurchaseDto processOrder(PurchasePostDto PurchasePostDto) {
		// Initialize required entities
		Account account = AuthService.getAuthenticatedAccount();

		// Checks for ongoing transaction
		transactionService.findAll(account.getAccountId(), TransactionService.TransactionSearchCriteria.builder()
			.status(Transaction.TransactionStatus.PENDING)
			.build()).stream()
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
		if (!closedDateService.getAllBetween(prepDate1, eventDate).isEmpty()) {
			throw new IllegalOperationException(String.format("Cannot create purchase on a closed date: %s ~ %s",
				prepDate1, eventDate));
		}

		Purchase purchase = purchaseService.create(PurchasePostDto);

		// Build transaction
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

		// Update purchase with the transaction
		purchase = purchaseRepository.findById(purchase.getPurchaseId())
			.orElseThrow(() -> new EntityNotFoundException("Purchase not found"));

		PurchaseDto purchaseDto = purchaseMapper.toDto(purchase);
		purchaseDto.setTransactions(List.of(transactionService.convertToDto(savedTransaction, account, purchase)));

		closedDateService.save(Set.of(
			ClosedDate.builder().date(prepDate1).closureType(ClosedDate.ClosureType.PREP).build(),
			ClosedDate.builder().date(prepDate2).closureType(ClosedDate.ClosureType.PREP).build(),
			ClosedDate.builder().date(eventDate).closureType(ClosedDate.ClosureType.RESERVED).build()
		));

		try {
			final String message = String.format("Pesanan dengan kode pemesanan %s sudah dibuat. Harap menyelesaikan pembayaran anda untuk mengkonfirmasi jadwal %s",
				purchase.getPurchaseId(), response.getRedirectUrl());
			whatsappApiService.send(account.getPhone(), message, "62");
		} catch (Exception e) {
			log.error("Failed to send order creation notification to {}, error: ", account.getPhone(), e);
		}

		return purchaseDto;
	}
}
