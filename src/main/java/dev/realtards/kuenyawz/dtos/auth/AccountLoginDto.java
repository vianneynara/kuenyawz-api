package dev.realtards.kuenyawz.dtos.auth;

import dev.realtards.kuenyawz.utils.stringtrimmer.CleanString;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Account login request")
@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class AccountLoginDto {

	@Schema(description = "User's email address", example = "emilia@example.com")
	@NotBlank(message = "Email is required")
	@Email(message = "Email format is invalid")
	@CleanString
	private String email;

	@Schema(description = "User's password", example = "emiliaBestGirl", minLength = 4)
	@NotBlank(message = "Password is required")
	@Size(min = 4, message = "Password must be at least 4 characters")
	private String password;
}
