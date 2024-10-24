package dev.realtards.wzsnacknbites.exceptions;

public class InvalidCredentialsException extends RuntimeException {

	public InvalidCredentialsException() {
		super("Incorrect credentials.");
	}

	public InvalidCredentialsException(String message) {
		super(message);
	}
}
