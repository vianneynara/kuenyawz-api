package dev.realtards.kuenyawz.exceptions;

public class InvalidRequestBodyValue extends RuntimeException {

	public InvalidRequestBodyValue() {
		super("Invalid request body value");
	}

	public InvalidRequestBodyValue(String message) {
		super(message);
	}
}
