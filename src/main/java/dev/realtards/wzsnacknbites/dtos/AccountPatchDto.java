package dev.realtards.wzsnacknbites.dtos;

import dev.realtards.wzsnacknbites.models.Account;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountPatchDto {

	@Size(min = 2, max = 128, message = "Full name must be between 2 and 128 characters")
	private String fullName;

	@Email(message = "Email format is invalid")
	private String email;

    @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Invalid phone number format")
    private String phone;

    private Account.Privilege privilege;

	public AccountPatchDto(Account account) {
		this.fullName = account.getFullName();
		this.email = account.getEmail();
		this.phone = account.getPhone();
		this.privilege = account.getPrivilege();
	}

	public static AccountPatchDto fromEntity(Account account) {
		return new AccountPatchDto(account);
	}
}
