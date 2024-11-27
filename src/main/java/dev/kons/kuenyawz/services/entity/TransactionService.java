package dev.kons.kuenyawz.services.entity;

import dev.kons.kuenyawz.constants.PaymentType;
import dev.kons.kuenyawz.dtos.purchase.TransactionDto;
import dev.kons.kuenyawz.dtos.purchase.TransactionPatchDto;
import dev.kons.kuenyawz.entities.Account;
import dev.kons.kuenyawz.entities.Purchase;
import dev.kons.kuenyawz.entities.Transaction;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public interface TransactionService {
	/**
	 * Finds all transactions with pagination.
	 *
	 * @param criteria {@link TransactionSearchCriteria} search criteria
	 * @return Paginated list of {@link TransactionDto}
	 */
	@Transactional(readOnly = true)
	Page<TransactionDto> findAll(TransactionSearchCriteria criteria);

	/**
	 * Finds all transactions of an account with pagination.
	 *
	 * @param accountId {@link Long} account id
	 * @param criteria {@link TransactionSearchCriteria} search criteria
	 * @return Paginated list of {@link TransactionDto}
	 */
	@Transactional(readOnly = true)
	Page<TransactionDto> findAll(Long accountId, TransactionSearchCriteria criteria);

	/**
	 * Finds a transaction by its id.
	 *
	 * @param transactionId {@link Long}
	 * @return {@link TransactionDto}
	 */
	@Transactional(readOnly = true)
	TransactionDto findById(Long transactionId);

	/**
	 * Gets a transaction by its id.
	 *
	 * @param transactionId {@link Long}
	 * @return {@link Transaction} entity
	 */
	@Transactional(readOnly = true)
	Transaction getById(Long transactionId);

	/**
	 * Finds a transaction by its Midtrans's invoice id.
	 *
	 * @param invoiceId {@link String}
	 * @return {@link TransactionDto}
	 */
	@Transactional(readOnly = true)
	TransactionDto findByInvoiceId(String invoiceId);

	/**
	 * Gets a transaction by its Midtrans's invoice id.
	 *
	 * @param invoiceId {@link String}
	 * @return {@link Transaction} entity
	 */
	@Transactional(readOnly = true)
	Transaction getByInvoiceId(String invoiceId);

	/**
	 * Finds all transactions of a purchase with pagination.
	 *
	 * @param purchaseId {@link Long} purchase id
	 * @return Paginated list of {@link TransactionDto}
	 */
	@Transactional(readOnly = true)
	Page<TransactionDto> findByPurchaseId(Long purchaseId);

	/**
	 * Creates a new transaction. This operation must be done inside a transaction.
	 *
	 * @param purchase
	 * @param account
	 * @return {@link TransactionDto}
	 */
	@Transactional
	Transaction create(Purchase purchase, Account account);

	/**
	 * Patches a transaction. This operation must be done inside a transaction.
	 * Used by payment gateway webhooks.
	 *
	 * @param transactionId {@link Long}
	 * @param transactionPatchDto {@link TransactionPatchDto}
	 * @return {@link TransactionDto}
	 */
	@Transactional
	TransactionDto patch(Long transactionId, TransactionPatchDto transactionPatchDto);

	/**
	 * Converts a transaction entity to DTO.
	 *
	 * @param transaction {@link Transaction}
	 * @return {@link TransactionDto}
	 */
	TransactionDto convertToDto(Transaction transaction);

	/**
	 * Calls the payment gateway to cancel all transactions of a purchase.
	 *
	 * @param purchaseId {@link Long} purchase id
	 */
	@Transactional
	void cancelAllOf(Long purchaseId);

	@Getter
	@Setter
	@Builder
	class TransactionSearchCriteria {

		private Boolean isAscending;
		private Transaction.TransactionStatus status;
		private PaymentType paymentType;
		private Long purchaseId;
		private LocalDate from;
		private LocalDate to;
		private Integer page;
		private Integer pageSize;

		static TransactionSearchCriteria of(Boolean isAscending, Transaction.TransactionStatus status, PaymentType paymentType, Long purchaseId, LocalDate from, LocalDate to, Integer page, Integer pageSize) {
			return TransactionSearchCriteria.builder()
				.isAscending(isAscending)
				.status(status)
				.paymentType(paymentType)
				.purchaseId(purchaseId)
				.from(from)
				.to(to)
				.page(page)
				.pageSize(pageSize)
				.build();
		}

		private Integer getPage() {
			return (page == null || page < 0) ? 0 : page;
		}

		private Integer getPageSize() {
			return (pageSize == null || pageSize < 1 || pageSize > 1000) ? 10 : pageSize;
		}

		public Pageable getPageable() {
			return PageRequest.of(
				getPage(),
				getPageSize(),
				isAscending ? Sort.Direction.ASC : Sort.Direction.DESC
			);
		}
	}
}
