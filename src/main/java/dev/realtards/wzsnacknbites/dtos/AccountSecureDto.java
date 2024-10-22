package dev.realtards.wzsnacknbites.dtos;

import dev.realtards.wzsnacknbites.models.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountSecureDto {

	private Long accoutId;
	private String fullName;
	private String googleId;
	private String email;
	private LocalDateTime emailVerifiedAt;
	private String phone;
	private Account.Privilege privilege;

	public AccountSecureDto(Account account) {
		this.accoutId = account.getAccountId();
		this.fullName = account.getFullName();
		this.googleId = account.getGoogleId();
		this.email = account.getEmail();
		this.emailVerifiedAt = account.getEmailVerifiedAt();
		this.phone = account.getPhone();
		this.privilege = account.getPrivilege();
	}

	public static AccountSecureDto fromEntity(Account account) {
		return new AccountSecureDto(account);
	}
}
