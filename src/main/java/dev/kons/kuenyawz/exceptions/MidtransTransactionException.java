package dev.kons.kuenyawz.exceptions;

import dev.kons.kuenyawz.dtos.midtrans.TransactionResponse;
import lombok.Getter;

@Getter
public class MidtransTransactionException extends RuntimeException {

	public static final String DEFAULT_MESSAGE = "There was an error during Midtrans transaction";

	private TransactionResponse errorResponse;

	public MidtransTransactionException() {
		super(DEFAULT_MESSAGE);
	}

	public MidtransTransactionException(String message) {
		super(message);
	}

	public MidtransTransactionException(String message, TransactionResponse errorResponse) {
		super(message != null ? message : DEFAULT_MESSAGE);
		this.errorResponse = errorResponse;
	}
}