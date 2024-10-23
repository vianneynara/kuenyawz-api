package dev.realtards.wzsnacknbites.responses;

import dev.realtards.wzsnacknbites.dtos.account.AccountSecureDto;

import java.util.List;

public record AccountsResponse(
	List<AccountSecureDto> accounts
) {
}
