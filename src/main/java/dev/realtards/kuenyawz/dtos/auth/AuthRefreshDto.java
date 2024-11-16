package dev.realtards.kuenyawz.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Authentication refresh request")
@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class AuthRefreshDto {

	@Schema(description = "Refresh token that has already been issued", example = "eiawDSFOJo8oeJIOjoi...")
	@NotBlank(message = "Refresh token is required")
	private String refreshToken;
}
