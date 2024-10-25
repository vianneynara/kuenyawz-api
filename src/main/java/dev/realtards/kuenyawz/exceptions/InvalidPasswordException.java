package dev.realtards.kuenyawz.exceptions;

public class InvalidPasswordException extends RuntimeException {
	public InvalidPasswordException() {
		super("Password incorrect");
	}

	public InvalidPasswordException(String message) {
		super(message);
	}
}
