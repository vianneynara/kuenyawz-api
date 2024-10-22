package dev.realtards.wzsnacknbites.services;

import dev.realtards.wzsnacknbites.dtos.AccountRegistrationDto;
import dev.realtards.wzsnacknbites.models.Account;

import java.util.List;

public interface AccountService {

	List<Account> getAllAccounts();

	Account createAccount(AccountRegistrationDto accountRegistrationDto);

	Account updatePrivilege(Account account, Account.Privilege privilege);
}
