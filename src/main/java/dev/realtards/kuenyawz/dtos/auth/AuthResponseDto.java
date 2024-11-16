package dev.realtards.kuenyawz.dtos.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Authentication response")
@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class AuthResponseDto {

	@Schema(description = "Access token")
	private String accessToken;

	@Schema(description = "Refresh token")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String refreshToken;

	@Schema(description = "Token type")
	private String tokenType = "Bearer";

	@Schema(description = "Account identifier")
	private String accountId;

	@Schema(description = "Email address")
	private String email;

	@Schema(description = "Full name")
	private String fullName;
}
