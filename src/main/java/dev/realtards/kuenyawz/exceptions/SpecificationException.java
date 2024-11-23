package dev.realtards.kuenyawz.exceptions;

public class SpecificationException extends RuntimeException {
	public SpecificationException() {
		super("A specification exception has occurred");
	}
	public SpecificationException(String message) {
		super(message);
	}
}
