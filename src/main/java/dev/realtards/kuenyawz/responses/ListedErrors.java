package dev.realtards.kuenyawz.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Map;

@Schema(description = "Response containing error message and list of errors")
@Getter
public class ListedErrors<T extends Map<String, String>> {
	@Schema(description = "Error message")
	private final String message;
	@Schema(description = "Map of errors, mapped by field name")
	private final T errors;

	public ListedErrors(String message, T errors) {
		this.message = message;
		this.errors = errors;
	}
}
