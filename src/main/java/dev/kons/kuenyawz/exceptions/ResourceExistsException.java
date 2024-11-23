package dev.kons.kuenyawz.exceptions;

public class ResourceExistsException extends RuntimeException {

	public ResourceExistsException() {
		super("Resource already exists");
	}

	public ResourceExistsException(String message) {
		super(message);
	}
}
