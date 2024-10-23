package dev.realtards.wzsnacknbites.services;

import dev.realtards.wzsnacknbites.dtos.AccountRegistrationDto;
import dev.realtards.wzsnacknbites.exceptions.AccountExistsException;
import dev.realtards.wzsnacknbites.exceptions.AccountNotFoundException;
import dev.realtards.wzsnacknbites.models.Account;
import dev.realtards.wzsnacknbites.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
			throw new AccountExistsException();
		}

		Account account = Account.builder()
			.fullName(accountRegistrationDto.getFullName())
			.email(accountRegistrationDto.getEmail().toLowerCase())
			.password(accountRegistrationDto.getPassword())
			.privilege(Account.Privilege.USER)
			.build();

		account = accountRepository.save(account);
		log.info("[ACCOUNT] CREATED: {}", account);

		return account;
	}

	@Override
	public Account getAccount(long accountId) {
		Optional<Account> result = accountRepository.findById(accountId);
		if (result.isEmpty()) {
			throw new AccountNotFoundException();
		}
		log.info("[ACCOUNT] RETRIEVED: {}", result.get());

		return result.get();
	}

	@Override
	public Account updateAccount(Account account) {
		Account existingAccount = accountRepository.findById(account.getAccountId())
			.orElseThrow(AccountNotFoundException::new);

		existingAccount.setFullName(account.getFullName());
		existingAccount.setEmail(account.getEmail());
		existingAccount.setPassword(account.getPassword());
		existingAccount.setPrivilege(account.getPrivilege());
		existingAccount.setPhone(account.getPhone());

		existingAccount = accountRepository.save(existingAccount);
		log.info("[ACCOUNT] UPDATED: {}", existingAccount);

		return existingAccount;
	}

	@Override
	public void deleteAccount(long accountId) {
		accountRepository.deleteById(accountId);
		log.info("[ACCOUNT] DELETED: {}", accountId);
	}

	@Override
	public Account updatePrivilege(Account account, Account.Privilege privilege) {
		Account existingAccount = accountRepository.findById(account.getAccountId())
			.orElseThrow(AccountNotFoundException::new);

		existingAccount.setPrivilege(privilege);
		existingAccount = accountRepository.save(existingAccount);
		log.info("[ACCOUNT] PRIVILEGE UPDATED: {}", existingAccount);

		return existingAccount;
	}
}
