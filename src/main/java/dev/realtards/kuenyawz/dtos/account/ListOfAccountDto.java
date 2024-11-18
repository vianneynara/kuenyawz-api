package dev.realtards.kuenyawz.dtos.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Schema(description = "Response containing list of accounts")
@Getter
public record ListOfAccountDto(
	@Schema(description = "List of account secure information") List<AccountSecureDto> accounts) {
	public ListOfAccountDto(List<AccountSecureDto> accounts) {
		this.accounts = accounts;
	}
}
