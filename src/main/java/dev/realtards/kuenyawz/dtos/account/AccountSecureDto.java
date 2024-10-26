package dev.realtards.kuenyawz.dtos.account;

import dev.realtards.kuenyawz.entities.Account;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Schema(description = "Secure account information returned by the API")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountSecureDto {

    @Schema(description = "Unique identifier of the account", example = "1221991247904768")
    private Long accountId;

    @Schema(description = "User's full name", example = "Emilia")
    private String fullName;

    @Schema(description = "Google OAuth ID if account is linked to Google", example = "118234546754675234123")
    private String googleId;

    @Schema(description = "User's email address", example = "emilia@example.com")
    private String email;

    @Schema(description = "Timestamp of when email was verified", example = "2024-01-01T12:00:00")
    private LocalDateTime emailVerifiedAt;

    @Schema(description = "User's phone number", example = "12345678901")
    private String phone;

    @Schema(description = "User's privilege level", example = "user")
    private Account.Privilege privilege;

	public AccountSecureDto(Account account) {
		this.accountId = account.getAccountId();
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
