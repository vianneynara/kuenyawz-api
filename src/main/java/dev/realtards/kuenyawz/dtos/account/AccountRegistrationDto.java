package dev.realtards.kuenyawz.dtos.account;

import dev.realtards.kuenyawz.utils.stringtrimmer.CleanString;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Account registration request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRegistrationDto {

	@Schema(description = "User's full name", example = "Emilia", minLength = 2, maxLength = 128)
	@NotBlank(message = "Full name is required")
	@Size(min = 2, max = 128, message = "Full name must be between 2 and 128 characters")
	@CleanString
	private String fullName;

    @Schema(description = "User's valid phone number", example = "81234567890", pattern = "^[1-9][0-9]{7,14}$")
    @Pattern(regexp = "^[1-9][0-9]{7,14}$", message = "Invalid phone number format")
	@NotBlank(message = "Phone is required")
	@CleanString
	private String phone;

	@Schema(description = "User's password", example = "emiliaBestGirl", minLength = 4)
	@NotBlank(message = "Password is required")
	@Size(min = 4, message = "Password must be at least 4 characters")
	private String password;
}
