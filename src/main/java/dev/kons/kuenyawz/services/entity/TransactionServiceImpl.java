package dev.kons.kuenyawz.services.entity;

import dev.kons.kuenyawz.configurations.ApplicationProperties;
import dev.kons.kuenyawz.dtos.midtrans.MidtransResponse;
import dev.kons.kuenyawz.dtos.purchase.TransactionDto;
import dev.kons.kuenyawz.dtos.purchase.TransactionPatchDto;
import dev.kons.kuenyawz.entities.Account;
import dev.kons.kuenyawz.entities.Purchase;
import dev.kons.kuenyawz.entities.Transaction;
import dev.kons.kuenyawz.exceptions.IllegalOperationException;
import dev.kons.kuenyawz.exceptions.UnauthorizedException;
import dev.kons.kuenyawz.repositories.PurchaseRepository;
import dev.kons.kuenyawz.repositories.TransactionRepository;
import dev.kons.kuenyawz.repositories.TransactionSpec;
import dev.kons.kuenyawz.services.logic.AuthService;
import dev.kons.kuenyawz.services.logic.MidtransApiService;
import dev.kons.kuenyawz.services.logic.WhatsappApiService;
import dev.kons.kuenyawz.utils.idgenerator.SnowFlakeIdGenerator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

	private final TransactionRepository transactionRepository;
	private final SnowFlakeIdGenerator snowFlakeIdGenerator;
	private final MidtransApiService midtransApiService;
	private final PurchaseRepository purchaseRepository;
	private final WhatsappApiService whatsappApiService;
	private final ApplicationProperties properties;

	@Override
	public Page<TransactionDto> findAll(TransactionSearchCriteria criteria) {
		AuthService.validateIsAdmin();
		return findAllHelper(criteria);
	}

	@Override
	public Page<TransactionDto> findAll(Long accountId, TransactionSearchCriteria criteria) {
		AuthService.validateMatchesId(accountId);
		criteria.setAccountId(accountId);
		return findAllHelper(criteria);
	}

	private Page<TransactionDto> findAllHelper(TransactionSearchCriteria criteria) {
		Specification<Transaction> spec = TransactionSpec.withAccountId(criteria.getAccountId())
			.and(TransactionSpec.withStatuses(criteria.getStatuses()))
			.and(TransactionSpec.withPaymentType(criteria.getPaymentType()))
			.and(TransactionSpec.withDateAfter(criteria.getFrom()))
			.and(TransactionSpec.withDateBefore(criteria.getTo()));

		if (criteria.getFrom() != null && criteria.getTo() != null) {
			spec = spec.and(TransactionSpec.withDateBetween(criteria.getFrom(), criteria.getTo()));
		} else if (criteria.getFrom() != null) {
			spec = spec.and(TransactionSpec.withDateAfter(criteria.getFrom()));
		} else if (criteria.getTo() != null) {
			spec = spec.and(TransactionSpec.withDateBefore(criteria.getTo()));
		}

		Sort sorter = Sort.by(criteria.getIsAscending() != null && criteria.getIsAscending()
			? Sort.Order.asc("createdAt")
			: Sort.Order.desc("createdAt")
		);
		Pageable pageable = criteria.getPageable(sorter);
		return transactionRepository.findAll(spec, pageable).map(this::convertToDto);
	}

	@Override
	public TransactionDto findById(Long transactionId) {
		return transactionRepository.findById(transactionId)
			.map(this::convertToDto)
			.orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
	}

	@Override
	public Transaction getById(Long transactionId) {
		return transactionRepository.findById(transactionId)
			.orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
	}

	@Override
	public TransactionDto findByInvoiceId(String referenceId) {
		return transactionRepository.findByReferenceId(referenceId)
			.map(this::convertToDto)
			.orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
	}

	@Override
	public Transaction getByInvoiceId(String referenceId) {
		return transactionRepository.findByReferenceId(referenceId)
			.orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
	}

	@Override
	public List<TransactionDto> findByPurchaseId(Long purchaseId) {
		Pageable pageable = Pageable.unpaged(
			Sort.by(Sort.Order.desc("createdAt"))
		);
		return transactionRepository.findByPurchase_PurchaseId(purchaseId, pageable)
			.stream()
			.map(this::convertToDto)
			.toList();
	}

	@Override
	public Transaction getLatestOfPurchaseId(Long purchaseId) {
		Transaction transaction = transactionRepository.findFirstByPurchase_PurchaseIdOrderByCreatedAtDesc(purchaseId)
			.orElseThrow(() -> new EntityNotFoundException("Transaction not found for purchase " + purchaseId));
		return transaction;
	}

	@Override
	public TransactionDto fetchTransaction(Long transactionId) {
		Transaction transaction = transactionRepository.findById(transactionId)
			.orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
		MidtransResponse res = midtransApiService.fetchTransactionStatus(String.valueOf(transactionId));

		return fetchTransactionHelper(transaction, res);
	}

	@Override
	public TransactionDto fetchTransaction(Transaction transaction) {
		MidtransResponse res = midtransApiService.fetchTransactionStatus(String.valueOf(transaction.getTransactionId()));

		return fetchTransactionHelper(transaction, res);
	}

	private TransactionDto fetchTransactionHelper(Transaction transaction, MidtransResponse res) {
		Transaction.TransactionStatus status = Transaction.TransactionStatus.fromString(res.getTransactionStatus());
		Purchase purchase = transaction.getPurchase();

		// Check for fraud status of the transaction, this is for card payments
		if (res.getFraudStatus() != null && !res.getFraudStatus().equalsIgnoreCase("accept")) {
			transaction.setStatus(Transaction.TransactionStatus.CANCEL);
			purchase.setStatus(Purchase.PurchaseStatus.CANCELLED);
			transactionRepository.save(transaction);
			purchaseRepository.save(purchase);
			return convertToDto(transaction);
		}

		transaction.setStatus(status);

		// Update purchase status based on transaction status
		if (purchase.getStatus() == Purchase.PurchaseStatus.PENDING) {
			if (status == Transaction.TransactionStatus.CAPTURE || status == Transaction.TransactionStatus.SETTLEMENT) {
				String message = String.format("Ada pesanan baru dengan kode *%s*, segera cek aplikasi! %n%n%s",
					purchase.getPurchaseId(), properties.frontend().getBaseUrl()
				);
				whatsappApiService.send(properties.vendor().getPhone(), message, "62");
				purchase.setStatus(Purchase.PurchaseStatus.CONFIRMING);
			} else if (status == Transaction.TransactionStatus.CANCEL || status == Transaction.TransactionStatus.EXPIRE) {
				purchase.setStatus(Purchase.PurchaseStatus.CANCELLED);
			}
			purchaseRepository.save(purchase);
		}

		transactionRepository.save(transaction);
		return convertToDto(transaction);
	}

	@Override
	public Transaction build(Purchase purchase, Account account) {
		AuthService.validateMatchesId(account.getAccountId());

		Long transactionId = snowFlakeIdGenerator.generateId();
		BigDecimal paymentFee = BigDecimal.valueOf(properties.vendor().getPaymentFee());

		return Transaction.builder()
			.transactionId(transactionId)
			.referenceId(null)
			.amount(purchase.getTotalPrice().add(purchase.getDeliveryFee()).add(paymentFee))
			.paymentUrl(null)
			.status(Transaction.TransactionStatus.CREATED)
			.paymentType(purchase.getPaymentType())
			.expiresAt(LocalDateTime.now().plusDays(1))
			.finalizedAt(null)
			.createdAt(LocalDateTime.now())
			.purchase(purchase)
			.account(account)
			.build();
	}

	@Override
	public TransactionDto create(Transaction transaction) {
		Transaction savedTransaction = transactionRepository.save(transaction);
		return convertToDto(savedTransaction);
	}

	@Override
	public TransactionDto patch(Long transactionId, TransactionPatchDto transactionPatchDto) {
		Transaction transaction = transactionRepository.findById(transactionId)
			.orElseThrow(() -> new EntityNotFoundException("Transaction with id " + transactionId + " not found"));
		transaction.setStatus(transactionPatchDto.getStatus());
		Transaction savedTransaction = transactionRepository.save(transaction);
		return convertToDto(savedTransaction);
	}

	public TransactionDto patch(Long transactionId, Transaction.TransactionStatus status) {
		Transaction transaction = transactionRepository.findById(transactionId)
			.orElseThrow(() -> new EntityNotFoundException("Transaction with id " + transactionId + " not found"));
		transaction.setStatus(status);
		Transaction savedTransaction = transactionRepository.save(transaction);
		return convertToDto(savedTransaction);
	}

	@Override
	public void cancelAllOf(Long purchaseId) {
		Pageable pageable = Pageable.unpaged(
			Sort.by(Sort.Order.desc("createdAt"))
		);
		Page<Transaction> transactions = transactionRepository.findByPurchase_PurchaseId(purchaseId, pageable);
		transactions.forEach(t -> {
			log.warn("Cancelling transaction id {} of purchase {}", t.getTransactionId(), purchaseId);
			cancelOneProcess(t);
		});
	}

	@Override
	public void cancelOne(@NotNull Long transactionId) {
		Transaction transaction = transactionRepository.findById(transactionId)
			.orElseThrow(() -> new EntityNotFoundException("Transaction with id " + transactionId + " not found"));

		cancelOneProcess(transaction);
	}

	@Override
	public void cancelOne(@NotNull Transaction transaction) {
		cancelOneProcess(transaction);
	}

	private void cancelOneProcess(Transaction transaction) {
		Account account = AuthService.getAuthenticatedAccount();
		if (!(AuthService.isAuthenticatedAdmin() || Objects.equals(transaction.getAccount().getAccountId(), account.getAccountId()))) {
			throw new UnauthorizedException("You are not authorized to cancel this transaction");
		}

		if (transaction.getStatus() == Transaction.TransactionStatus.CREATED) {
			log.warn("Transaction {} has not been continued yet, cancelling", transaction.getTransactionId());
		}

		if (transaction.getStatus() == Transaction.TransactionStatus.CANCEL) {
			log.warn("Transaction {} has already been cancelled", transaction.getTransactionId());
			return;
		}

		// Call Midtrans API to cancel the transaction
		MidtransResponse response = midtransApiService.cancelTransaction(String.valueOf(transaction.getTransactionId()));
		if (Objects.equals(response.getStatusCode(), "404")) {
			log.info("Transaction {} not found in Midtrans, cancelling locally", transaction.getTransactionId());
		} else if (Objects.equals(response.getStatusCode(), "412")) {
			throw new IllegalOperationException("Modification is not allowed on the transaction");
		}

		transaction.setStatus(Transaction.TransactionStatus.CANCEL);
		transactionRepository.save(transaction);
	}

	@Override
	public TransactionDto convertToDto(Transaction transaction) {
		return TransactionDto.fromEntity(transaction);
	}

	@Override
	public TransactionDto convertToDto(Transaction transaction, Account account, Purchase purchase) {
		TransactionDto dto = TransactionDto.fromEntity(transaction);
		dto.setAccountId(account.getAccountId());
		dto.setPurchaseId(purchase.getPurchaseId());

		return dto;
	}

	@Override
	public void validateOwnership(Long purchaseId, Long accountId) {
		List<Transaction> transaction = transactionRepository.findByPurchase_PurchaseIdAndAccount_AccountId(purchaseId, accountId);
		if (transaction.isEmpty()) {
			throw new UnauthorizedException("You are not authorized to access this order");
		}
	}

	@Override
	public boolean isOwner(Long purchaseId, Long accountId) {
		List<Transaction> transaction = transactionRepository.findByPurchase_PurchaseIdAndAccount_AccountId(purchaseId, accountId);
		return !transaction.isEmpty();
	}
}
