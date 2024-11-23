package dev.kons.kuenyawz.exceptions;

public class UnauthorizedException extends RuntimeException {

	private static final String defaultMessage = "Unauthorized";

	public UnauthorizedException() {
		super(defaultMessage);
	}

	public UnauthorizedException(String message) {
		super(message);
	}
}
