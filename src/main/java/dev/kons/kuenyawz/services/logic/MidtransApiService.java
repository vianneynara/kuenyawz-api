package dev.kons.kuenyawz.services.logic;

import dev.kons.kuenyawz.dtos.midtrans.TransactionRequest;
import dev.kons.kuenyawz.dtos.midtrans.TransactionResponse;

public interface MidtransApiService {
	/**
	 * Creates a transaction in which will return a token and a redirect URL.
	 * <br>
	 * {@code POST https://app.sandbox.midtrans.com/snap/v1/transactions}
	 *
	 * @param request {@link TransactionRequest} transaction request
	 * @return {@link TransactionResponse} transaction response
	 */
	TransactionResponse createTransaction(TransactionRequest request);

	/**
	 * Fetches a transaction status by its order (purchase) id.
	 * <br>
	 * {@code GET https://api.sandbox.midtrans.com/v2/{order_id}/status}
	 *
	 * @param orderId {@link String} order id
	 * @return {@link TransactionResponse} transaction response
	 */
	TransactionResponse fetchTransactionStatus(String orderId);

	/**
	 * Cancels/voids a transaction by its order (purchase) id.
	 * <br>
	 * {@code POST https://api.sandbox.midtrans.com/v2/{order_id}/cancel}
	 *
	 * @param orderId {@link String} order id
	 * @return {@link TransactionResponse} transaction response
	 */
	TransactionResponse cancelTransaction(String orderId);
}