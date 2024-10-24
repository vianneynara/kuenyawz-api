package dev.realtards.wzsnacknbites.exceptions;

public class AccountExistsException extends RuntimeException {

	public AccountExistsException() {
		super("Account already exists.");
	}

	public AccountExistsException(String message) {
		super(message);
	}
}
