package dev.realtards.wzsnacknbites.dtos.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRegistrationDto {

	@NotBlank(message = "Full name is required")
	@Size(min = 2, max = 128, message = "Full name must be between 2 and 128 characters")
	private String fullName;

	@NotBlank(message = "Password is required")
	@Size(min = 4, message = "Password must be at least 4 characters")
	private String password;

	@NotBlank(message = "Email is required")
	@Email(message = "Email format is invalid")
	private String email;
}
