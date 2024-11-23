package dev.kons.kuenyawz.exceptions;

public class ResourceUploadException extends RuntimeException {

	public ResourceUploadException() {
		super("Resource is empty");
	}

	public ResourceUploadException(String message) {
		super(message);
	}
}
