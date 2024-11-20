package dev.realtards.kuenyawz.dtos.account;

import dev.realtards.kuenyawz.entities.Account;
import dev.realtards.kuenyawz.utils.stringtrimmer.CleanString;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Schema(description = "Partial account update request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountPatchDto {

    @Schema(description = "User's full name", example = "Emilia", minLength = 2, maxLength = 128)
	@Size(min = 2, max = 128, message = "Full name must be between 2 and 128 characters")
	@CleanString
	private String fullName;

    @Schema(description = "User's valid phone number", example = "81234567890", pattern = "^[1-9][0-9]{7,14}$")
    @Pattern(regexp = "^[1-9][0-9]{7,14}$", message = "Invalid phone number format")
	@CleanString
    private String phone;

    @Schema(description = "User's email address", example = "emilia@example.com")
	@Email(message = "Email format is invalid")
	@CleanString
	private String email;

	public AccountPatchDto(Account account) {
		this.fullName = account.getFullName();
		this.phone = account.getPhone();
		this.email = account.getEmail();
	}

	public static AccountPatchDto fromEntity(Account account) {
		return new AccountPatchDto(account);
	}
}
