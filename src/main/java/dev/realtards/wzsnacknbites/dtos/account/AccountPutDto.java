package dev.realtards.wzsnacknbites.dtos.account;

import dev.realtards.wzsnacknbites.models.Account;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class AccountPutDto {

	@NotBlank(message = "Full name is required")
	@Size(min = 2, max = 128, message = "Full name must be between 2 and 128 characters")
	private String fullName;

	@NotBlank(message = "Email is required")
	@Email(message = "Email format is invalid")
	private String email;

    @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Invalid phone number format")
    private String phone;

    @NotBlank(message = "Privilege is required")
    private Account.Privilege privilege;

	public AccountPutDto(Account account) {
		this.fullName = account.getFullName();
		this.email = account.getEmail();
		this.phone = account.getPhone();
		this.privilege = account.getPrivilege();
	}

	public static AccountPutDto fromEntity(Account account) {
		return new AccountPutDto(account);
	}
}
