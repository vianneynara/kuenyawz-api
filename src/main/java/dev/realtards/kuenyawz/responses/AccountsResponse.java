package dev.realtards.kuenyawz.responses;

import dev.realtards.kuenyawz.dtos.account.AccountSecureDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Schema(description = "Response containing list of accounts")
@Getter
public class AccountsResponse {
    @Schema(description = "List of account secure information")
	private final List<AccountSecureDto> accounts;

	public AccountsResponse(List<AccountSecureDto> accounts) {
		this.accounts = accounts;
	}
}
