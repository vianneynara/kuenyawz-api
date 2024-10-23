package dev.realtards.wzsnacknbites.dtos.account;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateDto {
	@NotBlank(message = "Current password is required")
	private String currentPassword;

	@NotBlank(message = "New password is required")
	private String newPassword;

	@NotBlank(message = "Password confirmation is required")
	private String confirmPassword;
}