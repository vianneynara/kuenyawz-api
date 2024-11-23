package dev.realtards.kuenyawz.exceptions;

public class InvalidPasswordException extends RuntimeException {
	public InvalidPasswordException() {
		super("Invalid Credentials");
	}

	public InvalidPasswordException(String message) {
		super(message);
	}
}
