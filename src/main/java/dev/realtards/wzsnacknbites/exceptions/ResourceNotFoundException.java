package dev.realtards.wzsnacknbites.exceptions;

public class ResourceNotFoundException extends RuntimeException {

	private static final String defaultMessage = "Resource Not Found";

	public ResourceNotFoundException() {
		super(defaultMessage);
	}

	public ResourceNotFoundException(String message) {
		super(message);
	}
}
