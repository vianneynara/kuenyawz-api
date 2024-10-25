package dev.realtards.kuenyawz.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "Response containing error message")
@Getter
public class ErrorResponse {
	@Schema(description = "Error message")
	private final String message;

	public ErrorResponse(String message) {
		this.message = message;
	}

}
