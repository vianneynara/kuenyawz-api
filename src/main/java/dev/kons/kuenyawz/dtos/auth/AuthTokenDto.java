package dev.kons.kuenyawz.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Authentication token request")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthTokenDto {

	@Schema(description = "Token that has already been issued", example = "eiawDSFOJo8oeJIOjoi...")
	@NotNull(message = "Token is required")
	@NotBlank(message = "Token is required")
	private String token;
}
