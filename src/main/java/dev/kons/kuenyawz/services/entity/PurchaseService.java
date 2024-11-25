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

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PurchaseService {
	/**
	 * Finds all purchases with pagination with admin view.
	 *
	 * @param criteria {@link PurchaseSearchCriteria} search criteria
	 * @return Paginated list of {@link PurchaseDto}
	 */
	Page<PurchaseDto> findAll(PurchaseSearchCriteria criteria);

	/**
	 * Finds all purchases with pagination with user view.
	 *
	 * @param accountId {@link Long} account id
	 * @param criteria  {@link PurchaseSearchCriteria} search criteria
	 * @return Paginated list of {@link PurchaseDto}
	 */
	Page<PurchaseDto> findAll(Long accountId, PurchaseSearchCriteria criteria);

	/**
	 * Finds a purchase by its purchase id.
	 *
	 * @param purchaseId {@link Long}
	 * @return {@link PurchaseDto}
	 */
	PurchaseDto findById(Long purchaseId);

	/**
	 * Finds a purchase by its transaction id.
	 *
	 * @param transactionId {@link Long}
	 * @return {@link PurchaseDto}
	 */
	PurchaseDto findByTransactionId(Long transactionId);

	/**
	 * Finds a purchase by its Xendit's invoice id.
	 *
	 * @param invoiceId {@link String}
	 * @return {@link PurchaseDto}
	 */
	PurchaseDto findByInvoiceId(String invoiceId);

	/**
	 * Creates a new purchase.
	 *
	 * @param purchasePostDto {@link PurchasePostDto}
	 * @return {@link PurchaseDto}
	 */
	PurchaseDto create(PurchasePostDto purchasePostDto);

	/**
	 * Patches a purchase.
	 *
	 * @param purchaseId       {@link Long}
	 * @param purchasePatchDto {@link PurchasePatchDto}
	 * @return {@link PurchaseDto}
	 */
	PurchaseDto patch(Long purchaseId, PurchasePatchDto purchasePatchDto);

	// Other business logic

	/**
	 * Cancels a purchase. Has two modes, direct cancellation can be done by admin, while user
	 * can only request for cancellation if conditions are met.
	 *
	 * @param purchaseId {@link Long}
	 * @return {@link PurchaseDto}
	 */
	PurchaseDto cancel(Long purchaseId);

	/**
	 * Confirms a purchase after the purchase has been paid (down-payment/full-payment).
	 * This is done by admin.
	 *
	 * @param purchaseId {@link Long}
	 * @return {@link PurchaseDto}
	 */
	PurchaseDto confirm(Long purchaseId);

	/**
	 * Changes a fee of the purchase. This is done by admin. Can only affect non full-paid purchases.
	 * Fee will be included in full payments.
	 *
	 * @param purchaseId {@link Long}
	 * @param fee        {@link BigDecimal} delivery fee
	 * @return {@link PurchaseDto}
	 */
	PurchaseDto changeFee(Long purchaseId, BigDecimal fee);

	/**
	 * Changes the status of a purchase. This is done by admin.
	 *
	 * @param purchaseId {@link Long}
	 * @param status     {@link Purchase.PurchaseStatus}
	 * @return {@link PurchaseDto}
	 */
	PurchaseDto changeStatus(Long purchaseId, Purchase.PurchaseStatus status);

	@Getter
	@Setter
	@Builder
	class PurchaseSearchCriteria {

		private Boolean isAscending;
		private Purchase.PurchaseStatus status;
		private PaymentType paymentType;
		private LocalDate from;
		private LocalDate to;
		private Integer page;
		private Integer pageSize;

		static PurchaseSearchCriteria of(Boolean isAscending, Purchase.PurchaseStatus status, PaymentType paymentType, LocalDate from, LocalDate to, Integer page, Integer pageSize) {
			return PurchaseSearchCriteria.builder()
				.isAscending(isAscending)
				.status(status)
				.paymentType(paymentType)
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
	}
}
