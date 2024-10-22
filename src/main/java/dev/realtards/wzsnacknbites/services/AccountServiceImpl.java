package dev.realtards.wzsnacknbites.services;

import dev.realtards.wzsnacknbites.dtos.AccountRegistrationDto;
import dev.realtards.wzsnacknbites.exceptions.EmailExistsException;
import dev.realtards.wzsnacknbites.models.Account;
import dev.realtards.wzsnacknbites.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;

	@Override
	public List<Account> getAllAccounts() {
		return accountRepository.findAll();
	}

	@Override
	public Account createAccount(AccountRegistrationDto accountRegistrationDto) {
		if (accountRepository.existsByEmail(accountRegistrationDto.getEmail())) {
			throw new EmailExistsException();
		}

		Account account = Account.builder()
			.fullName(accountRegistrationDto.getFullName())
			.email(accountRegistrationDto.getEmail().toLowerCase())
			.password(accountRegistrationDto.getPassword())
			.privilege(Account.Privilege.USER)
			.build();

		account = accountRepository.save(account);
		log.info("[CREATED]: {}", account);

		return account;
	}

	@Override
	public Account updatePrivilege(Account account, Account.Privilege privilege) {
		account.setPrivilege(privilege);

		account = accountRepository.save(account);
		log.info("[PRIVILEGE UPDATED]: {}", account);

		return account;
	}
}
