package dev.kons.kuenyawz.services.entity;

import dev.kons.kuenyawz.constants.PaymentType;
import dev.kons.kuenyawz.dtos.purchase.PurchaseDto;
import dev.kons.kuenyawz.dtos.purchase.PurchasePatchDto;
import dev.kons.kuenyawz.dtos.purchase.PurchasePostDto;
import dev.kons.kuenyawz.entities.Purchase;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PurchaseService {
	/**
	 * Finds all purchases with pagination with admin view.
	 *
	 * @param criteria {@link PurchaseSearchCriteria} search criteria
	 * @return Paginated list of {@link PurchaseDto}
	 */
	@Transactional(readOnly = true)
	Page<PurchaseDto> findAll(PurchaseSearchCriteria criteria);

	/**
	 * Finds all purchases with pagination with user view.
	 *
	 * @param accountId {@link Long} account id
	 * @param criteria  {@link PurchaseSearchCriteria} search criteria
	 * @return Paginated list of {@link PurchaseDto}
	 */
	@Transactional(readOnly = true)
	Page<PurchaseDto> findAll(Long accountId, PurchaseSearchCriteria criteria);

	/**
	 * Finds a purchase by its purchase id.
	 *
	 * @param purchaseId {@link Long}
	 * @return {@link PurchaseDto}
	 */
	@Transactional(readOnly = true)
	PurchaseDto findById(Long purchaseId);

	/**
	 * Finds a purchase by its transaction id.
	 *
	 * @param transactionId {@link Long}
	 * @return {@link PurchaseDto}
	 */
	@Transactional(readOnly = true)
	PurchaseDto findByTransactionId(Long transactionId);

	/**
	 * Finds a purchase by its invoice id.
	 *
	 * @param invoiceId {@link String}
	 * @return {@link PurchaseDto}
	 */
	@Transactional(readOnly = true)
	PurchaseDto findByInvoiceId(String invoiceId);

	/**
	 * Creates a new purchase.
	 *
	 * @param purchasePostDto {@link PurchasePostDto}
	 * @return {@link PurchaseDto}
	 */
	@Transactional
	PurchaseDto create(PurchasePostDto purchasePostDto);

	/**
	 * Patches a purchase.
	 *
	 * @param purchaseId       {@link Long}
	 * @param purchasePatchDto {@link PurchasePatchDto}
	 * @return {@link PurchaseDto}
	 */
	@Transactional
	PurchaseDto patch(Long purchaseId, PurchasePatchDto purchasePatchDto);

	// Other business logic

	/**
	 * Cancels a purchase. Has two modes, direct cancellation can be done by admin, while user
	 * can only request for cancellation if conditions are met.
	 *
	 * @param purchaseId {@link Long}
	 * @return {@link PurchaseDto}
	 */
	@Transactional
	PurchaseDto cancel(Long purchaseId);

	/**
	 * Confirms a purchase after the purchase has been paid (down-payment/full-payment).
	 * This is done by admin.
	 *
	 * @param purchaseId {@link Long}
	 * @return {@link PurchaseDto}
	 */
	@Transactional
	PurchaseDto confirm(Long purchaseId);

	/**
	 * Changes a fee of the purchase. This is done by admin. Can only affect non full-paid purchases.
	 * Fee will be included in full payments.
	 *
	 * @param purchaseId {@link Long}
	 * @param fee        {@link BigDecimal} delivery fee
	 * @return {@link PurchaseDto}
	 */
	@Transactional
	PurchaseDto changeFee(Long purchaseId, BigDecimal fee);

	/**
	 * Changes the status of a purchase. This is done by admin.
	 *
	 * @param purchaseId {@link Long}
	 * @param status     {@link Purchase.PurchaseStatus}
	 * @return {@link PurchaseDto}
	 */
	@Transactional
	PurchaseDto changeStatus(Long purchaseId, Purchase.PurchaseStatus status);

	/**
	 * Converts a purchase entity to a purchase dto.
	 *
	 * @param purchase {@link Purchase}
	 * @return {@link PurchaseDto}
	 */
	PurchaseDto convertToDto(Purchase purchase);

	@Getter
	@Setter
	@Builder
	class PurchaseSearchCriteria {

		private Boolean isAscending;
		private Purchase.PurchaseStatus status;
		private PaymentType paymentType;
		private LocalDate from;
		private LocalDate to;
		private Long accountId;
		private String orderBy;
		private Integer page;
		private Integer pageSize;

		static PurchaseSearchCriteria of(Boolean isAscending, Purchase.PurchaseStatus status, PaymentType paymentType, LocalDate from, LocalDate to, Long accountId, String sortBy, Integer page, Integer pageSize) {
			isAscending = (isAscending != null && isAscending);
			return PurchaseSearchCriteria.builder()
				.isAscending(isAscending)
				.status(status)
				.paymentType(paymentType)
				.from(from)
				.to(to)
				.accountId(accountId)
				.orderBy(sortBy)
				.page(page)
				.pageSize(pageSize)
				.build();
		}

		public Integer getPage() {
			return (page == null || page < 0) ? 0 : page;
		}

		public Integer getPageSize() {
			return (pageSize == null || pageSize < 1 || pageSize > 1000) ? 10 : pageSize;
		}

		public Pageable getPageable() {
			return PageRequest.of(
				getPage(),
				getPageSize()
			);
		}

		public Pageable getPageable(Sort sorter) {
			if (sorter == null) {
				sorter = Sort.by((isAscending != null && isAscending)
					? Sort.Order.asc(orderBy != null ? orderBy : "purchaseId")
					: Sort.Order.desc(orderBy != null ? orderBy : "purchaseId")
				);
			}

			return PageRequest.of(
				getPage(),
				getPageSize(),
				sorter
			);
		}
	}
}
