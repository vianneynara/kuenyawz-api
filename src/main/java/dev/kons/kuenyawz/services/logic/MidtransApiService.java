package dev.kons.kuenyawz.services.logic;

import dev.kons.kuenyawz.dtos.midtrans.TransactionRequest;
import dev.kons.kuenyawz.dtos.midtrans.TransactionResponse;

public interface MidtransApiService {
	/**
	 * Creates a transaction in which will return a token and a redirect URL.
	 *
	 * @param request {@link TransactionRequest} transaction request
	 * @return {@link TransactionResponse} transaction response
	 */
	TransactionResponse createTransaction(TransactionRequest request);

	/**
	 * Cancels/voids a transaction by its order (purchase) id.
	 *
	 * @param orderId {@link String} order id
	 * @return {@link TransactionResponse} transaction response
	 */
	TransactionResponse cancelTransaction(String orderId);
}
