package dev.realtards.kuenyawz.dtos.account;

import dev.realtards.kuenyawz.entities.Account;
import dev.realtards.kuenyawz.utils.stringtrimmer.CleanString;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Schema(description = "Complete account update request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountPutDto {

    @Schema(description = "User's full name", example = "Emilia Narwandaru", minLength = 2, maxLength = 128)
	@NotBlank(message = "Full name is required")
	@Size(min = 2, max = 128, message = "Full name must be between 2 and 128 characters")
	@CleanString
	private String fullName;

    @Schema(description = "User's phone number", example = "+92345678909", pattern = "^\\+?[1-9][0-9]{7,14}$")
    @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Invalid phone number format")
	@CleanString
    private String phone;

    @Schema(description = "User's email address", example = "emilia.narwandaru@example.com")
	@Email(message = "Email format is invalid")
	@CleanString
	private String email;

	public AccountPutDto(Account account) {
		this.fullName = account.getFullName();
		this.email = account.getEmail();
		this.phone = account.getPhone();
	}

	public static AccountPutDto fromEntity(Account account) {
		return new AccountPutDto(account);
	}
}
