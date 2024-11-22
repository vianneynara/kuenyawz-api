package dev.realtards.kuenyawz.advice.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing error message")
public record ErrorResponse(
	@Schema(description = "Error message") String message
) {

	public ErrorResponse(String message) {
		this.message = message;
	}

	public static ErrorResponse of(String message) {
		return new ErrorResponse(message);
	}
}
