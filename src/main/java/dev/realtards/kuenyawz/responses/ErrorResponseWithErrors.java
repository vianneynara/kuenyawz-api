package dev.realtards.kuenyawz.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Schema(description = "Response containing error message and list of errors")
@Getter
public class ErrorResponseWithErrors {
	@Schema(description = "Error message")
	private final String message;
	@Schema(description = "List of errors")
	private final List<String> errors;

	public ErrorResponseWithErrors(String message, List<String> errors) {
		this.message = message;
		this.errors = errors;
	}
}
