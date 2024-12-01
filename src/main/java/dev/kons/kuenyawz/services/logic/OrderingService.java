package dev.kons.kuenyawz.services.logic;

import dev.kons.kuenyawz.dtos.purchase.PurchaseDto;
import dev.kons.kuenyawz.dtos.purchase.PurchasePostDto;

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
}
