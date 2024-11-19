package dev.realtards.kuenyawz.dtos.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Authentication response")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponseDto {

	@Schema(description = "JWT access token with short expiration time",
		example = "iOiJIUzI6IkpXVCJ9.eyJzwiaMDAwLCJleHAiOjE2MjAwMDAwMDB9.1Jf")
	@NotNull
	private String accessToken;

	@Schema(description = "Refresh token for obtaining new access tokens",
		example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJeJf")
	@NotNull
	private String refreshToken;

	@Schema(description = "Token type, always 'Bearer'", example = "Bearer", defaultValue = "Bearer")
	@NotNull
	@Builder.Default
	private String tokenType = "Bearer";

	@Schema(description = "Token issue time in milliseconds timestamp", example = "1620000000")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long iat;

	@Schema(description = "Token expiration time in milliseconds timestamp", example = "1620360000")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long exp;
}