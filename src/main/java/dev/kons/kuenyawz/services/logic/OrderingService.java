package dev.kons.kuenyawz.services.logic;

import dev.kons.kuenyawz.dtos.purchase.PurchaseDto;
import dev.kons.kuenyawz.dtos.purchase.PurchasePostDto;
import dev.kons.kuenyawz.dtos.purchase.TransactionDto;
import dev.kons.kuenyawz.entities.Purchase;
import dev.kons.kuenyawz.services.entity.PurchaseService;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface OrderingService {
	/**
	 * Initiates order processing flow, which includes:
	 * <ol>
	 *     <li>Creating a new purchase, save it and get the purchase id</li>
	 *     <li>Use the purchase id to create a new Midtrans invoice</li>
	 *     <li>Track the invoice status to make Transaction and correlate it with the Purchase and Account</li>
	 *     <li>Update the purchase status based on the transaction status</li>
	 * </ol>
	 *
	 * @param PurchasePostDto
	 * @return
	 */
	PurchaseDto processOrder(PurchasePostDto PurchasePostDto);

	/**
	 * Cancels an order by its purchase id. This also cancels all transactions related to the purchase.
	 *
	 * @param purchaseId {@link Long}
	 * @return {@link PurchaseDto}
	 */
	PurchaseDto cancelOrder(Long purchaseId);

	/**
	 * Confirms an order by its purchase id. Confirming an order can only be done if the
	 * transaction status is already set to "CAPTURE" or "SETTLEMENT.
	 *
	 * @param purchaseId {@link Long}
	 * @return {@link PurchaseDto}
	 */
	PurchaseDto confirmOrder(Long purchaseId);

	/**
	 * Refunds an order by its purchase id. Calls the purchase service to refund the order.
	 *
	 * @param purchaseId {@link Long}
	 * @return {@link PurchaseDto}
	 */
	PurchaseDto refundOrder(Long purchaseId);

	/**
	 * Changes the status of an order by its purchase id. Calls the purchase service to change the status.
	 *
	 * @param purchaseId {@link Long}
	 * @param status {@link Purchase.PurchaseStatus}
	 * @return {@link PurchaseDto}
	 */
	PurchaseDto changeOrderStatus(Long purchaseId, String status);

	/**
	 * Upgrades the status of an order by its purchase id to its next status.
	 * Calls the purchase service to upgrade the status.
	 *
	 * @param purchaseId {@link Long}
	 * @return {@link PurchaseDto}
	 */
	PurchaseDto upgradeOrderStatus(Long purchaseId);

	/**
	 * Gets a map of available next statuses for an order by its purchase id.
	 *
	 * @param purchaseId {@link Long}
	 * @return {@link Map} key-value pair of status and description
	 */
	Map<String, String> availableStatuses(Long purchaseId);

	/**
	 * Finds all purchases with pagination. Will return based on account session type.
	 *
	 * @param criteria {@link PurchaseService.PurchaseSearchCriteria}
	 * @return {@link Page<PurchaseDto>}
	 */
	Page<PurchaseDto> findAll(PurchaseService.PurchaseSearchCriteria criteria);

	/**
	 * Fetches a purchase by its id.
	 *
	 * @param purchaseId {@link Long}
	 * @return {@link PurchaseDto}
	 */
	PurchaseDto findPurchase(Long purchaseId);

	/**
	 * Fetches the latest transaction of a purchase.
	 *
	 * @param purchaseId {@link Long}
	 * @return {@link TransactionDto}
	 */
	TransactionDto findTransactionOfPurchase(Long purchaseId);
}
