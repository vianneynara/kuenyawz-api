package dev.kons.kuenyawz.services.entity;

import dev.kons.kuenyawz.dtos.purchase.TransactionDto;
import dev.kons.kuenyawz.dtos.purchase.TransactionPatchDto;
import dev.kons.kuenyawz.entities.Account;
import dev.kons.kuenyawz.entities.Purchase;
import dev.kons.kuenyawz.entities.Transaction;
import dev.kons.kuenyawz.mapper.TransactionMapper;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

	private final TransactionRepository transactionRepository;
	private final SnowFlakeIdGenerator snowFlakeIdGenerator;
	private final MidtransApiService midtransApiService;
	private final TransactionMapper transactionMapper;

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
		return transactionRepository.findByPurchase_PurchaseId(purchaseId)
			.stream()
			.map(this::convertToDto)
			.toList();
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
		List<Transaction> transactions = transactionRepository.findByPurchase_PurchaseId(purchaseId);
		transactions.forEach(this::cancelOne);
	}

	public void cancelOne(@NotNull Transaction transaction) {
		if (transaction.getStatus() == Transaction.TransactionStatus.CANCEL) {
//			throw new IllegalOperationException("Transaction %d has already been cancelled");
			log.warn("Transaction {} has already been cancelled", transaction.getTransactionId());
			return;
		}
		transaction.setStatus(Transaction.TransactionStatus.CANCEL);
		transactionRepository.save(transaction);
	}

	public void cancelOne(@NotNull Long transactionId) {
		Transaction transaction = transactionRepository.findById(transactionId)
			.orElseThrow(() -> new EntityNotFoundException("Transaction with id " + transactionId + " not found"));
		cancelOne(transaction);
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
}
