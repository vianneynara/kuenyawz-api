package dev.realtards.wzsnacknbites.responses;

import dev.realtards.wzsnacknbites.dtos.AccountSecureDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AccountsResponse {

	private final List<AccountSecureDto> accounts;
}
