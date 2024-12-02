package dev.kons.kuenyawz.services.entity;

import dev.kons.kuenyawz.dtos.midtrans.TransactionResponse;
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
			.and(TransactionSpec.withStatus(criteria.getStatus()))
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
	public TransactionDto fetchTransaction(Long transactionId) {
		Transaction transaction = transactionRepository.findById(transactionId)
			.orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
		TransactionResponse res = midtransApiService.fetchTransactionStatus(String.valueOf(transactionId));

		Transaction.TransactionStatus status = Transaction.TransactionStatus.fromString(res.getTransactionStatus());
		transaction.setStatus(status);

		Purchase purchase = transaction.getPurchase();
		if (purchase.getStatus() == Purchase.PurchaseStatus.PENDING
			&& (status == Transaction.TransactionStatus.CAPTURE || status == Transaction.TransactionStatus.SETTLEMENT)
		) {
			purchase.setStatus(Purchase.PurchaseStatus.CONFIRMING);
			purchaseRepository.save(purchase);
		} else if (purchase.getStatus() == Purchase.PurchaseStatus.PENDING
			&& (status == Transaction.TransactionStatus.CANCEL || status == Transaction.TransactionStatus.EXPIRE)
		) {
			purchase.setStatus(Purchase.PurchaseStatus.CANCELLED);
			purchaseRepository.save(purchase);
		}

		Transaction savedTransaction = transactionRepository.save(transaction);
		return convertToDto(savedTransaction);
	}

	@Override
	public TransactionDto fetchTransaction(Transaction transaction) {
		TransactionResponse res = midtransApiService.fetchTransactionStatus(String.valueOf(transaction.getTransactionId()));

		Transaction.TransactionStatus status = Transaction.TransactionStatus.fromString(res.getTransactionStatus());
		transaction.setStatus(status);

		Purchase purchase = transaction.getPurchase();
		if (purchase.getStatus() == Purchase.PurchaseStatus.PENDING
			&& (status == Transaction.TransactionStatus.CAPTURE || status == Transaction.TransactionStatus.SETTLEMENT)
		) {
			purchase.setStatus(Purchase.PurchaseStatus.CONFIRMING);
			purchaseRepository.save(purchase);
		} else if (purchase.getStatus() == Purchase.PurchaseStatus.PENDING
			&& (status == Transaction.TransactionStatus.CANCEL || status == Transaction.TransactionStatus.EXPIRE)
		) {
			purchase.setStatus(Purchase.PurchaseStatus.CANCELLED);
			purchaseRepository.save(purchase);
		}

		Transaction savedTransaction = transactionRepository.save(transaction);
		return convertToDto(savedTransaction);
	}

	@Override
	public Transaction build(Purchase purchase, Account account) {
		AuthService.validateMatchesId(account.getAccountId());

		Long transactionId = snowFlakeIdGenerator.generateId();

		return Transaction.builder()
			.transactionId(transactionId)
			.referenceId(null)
			.amount(purchase.getTotalPrice().add(purchase.getDeliveryFee()))
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
		TransactionResponse response = midtransApiService.cancelTransaction(String.valueOf(transaction.getTransactionId()));
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
