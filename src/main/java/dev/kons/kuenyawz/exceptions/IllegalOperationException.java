package dev.kons.kuenyawz.exceptions;

public class IllegalOperationException extends RuntimeException {

	public IllegalOperationException() {
		super("Illegal operation detected");
	}

	public IllegalOperationException(String message) {
		super(message);
	}
}
