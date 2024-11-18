package dev.realtards.kuenyawz.advice.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "Response containing error message and list of errors")
public record ListedErrors<T extends Map<String, String>>(
	@Schema(description = "Error message") String message,
	@Schema(description = "Map of errors, mapped by field name") T errors
) {

	public ListedErrors(String message, T errors) {
		this.message = message;
		this.errors = errors;
	}

	public static <T extends Map<String, String>> ListedErrors<T> of(String message, T errors) {
		return new ListedErrors<>(message, errors);
	}
}
