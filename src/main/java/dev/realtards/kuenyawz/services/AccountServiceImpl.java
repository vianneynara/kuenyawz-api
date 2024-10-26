package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.dtos.account.*;
import dev.realtards.kuenyawz.entities.Account;
import dev.realtards.kuenyawz.exceptions.AccountExistsException;
import dev.realtards.kuenyawz.exceptions.AccountNotFoundException;
import dev.realtards.kuenyawz.exceptions.InvalidPasswordException;
import dev.realtards.kuenyawz.exceptions.PasswordMismatchException;
import dev.realtards.kuenyawz.mapper.AccountMapper;
import dev.realtards.kuenyawz.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;
	private final AccountMapper accountMapper;

	@Override
	public List<AccountSecureDto> getAllAccounts() {
		return accountRepository.findAll()
			.stream()
			.map(accountMapper::fromEntity)
			.toList();
	}

	@Override
	public Account createAccount(AccountRegistrationDto accountRegistrationDto) {
		if (accountRepository.existsByEmail(accountRegistrationDto.getEmail())) {
			throw new AccountExistsException();
		}

		Account account = Account.builder()
			.fullName(accountRegistrationDto.getFullName())
			.email(accountRegistrationDto.getEmail().toLowerCase())
			.password(passwordEncoder.encode(accountRegistrationDto.getPassword()))
			.privilege(Account.Privilege.USER)
			.build();

		account = accountRepository.save(account);
		log.info("CREATED: {}", account);

		return account;
	}

	@Override
	public Account getAccount(long accountId) {
		Account account = accountRepository.findById(accountId)
			.orElseThrow(AccountNotFoundException::new);
		log.info("RETRIEVED by ID: {}", account);
		return account;
	}

	@Override
	public Account getAccount(String email) {
		Account account = accountRepository.findByEmail(email)
			.orElseThrow(AccountNotFoundException::new);
		log.info("RETRIEVED by EMAIL: {}", account);
		return account;
	}

	@Override
	public Account updateAccount(Long accountId, AccountPutDto account) {
		Account existingAccount = accountRepository.findById(accountId)
			.orElseThrow(AccountNotFoundException::new);

		existingAccount.setFullName(account.getFullName());
		existingAccount.setEmail(account.getEmail());
		existingAccount.setPhone(account.getPhone());

		existingAccount = accountRepository.save(existingAccount);
		log.info("UPDATED: {}", existingAccount);

		return existingAccount;
	}

	@Override
	public void deleteAccount(long accountId) {
		accountRepository.deleteById(accountId);
		log.info("DELETED: {}", accountId);
	}

	@Override
	public Account patchAccount(Long accountId, AccountPatchDto accountPatchDto) {
		final Account existingAccount = accountRepository.findById(accountId)
			.orElseThrow(AccountNotFoundException::new);

		Optional.ofNullable(accountPatchDto.getFullName())
			.ifPresent(existingAccount::setFullName);
		Optional.ofNullable(accountPatchDto.getEmail())
			.ifPresent(email -> {
				if (accountRepository.existsByEmail(email)) {
					throw new AccountExistsException();
				}
				existingAccount.setEmail(email);
			});
		Optional.ofNullable(accountPatchDto.getPhone())
			.ifPresent(existingAccount::setPhone);

		Account savedAccount = accountRepository.save(existingAccount);
		log.info("PATCHED: {}", existingAccount);

		return savedAccount;
	}

	@Override
	public Account updatePassword(Long accountId, PasswordUpdateDto passwordUpdateDto) {
		Account existingAccount = accountRepository.findById(accountId)
			.orElseThrow(AccountNotFoundException::new);

		// checks whether the current password matches
		log.debug("DTO Current password: {}", passwordUpdateDto.getCurrentPassword());
		log.debug("ACC Existing password: {}", existingAccount.getPassword());
		if (!passwordEncoder.matches(passwordUpdateDto.getCurrentPassword(), existingAccount.getPassword())) {
			throw new InvalidPasswordException();
		}

		// checks whether the new and confirm password matches
		if (!passwordUpdateDto.getNewPassword().equals(passwordUpdateDto.getConfirmPassword())) {
			throw new PasswordMismatchException("New password and confirm password mismatch");
		}

		String encodedPassword = passwordEncoder.encode(passwordUpdateDto.getNewPassword());
		existingAccount.setPassword(encodedPassword);

		accountRepository.save(existingAccount);
		log.info("PASSWORD UPDATED: {}", existingAccount);

		return existingAccount;
	}

	@Override
	public Account updatePrivilege(Long accountId, PrivilegeUpdateDto privilegeUpdateDto) {
		Account existingAccount = accountRepository.findById(accountId)
			.orElseThrow(AccountNotFoundException::new);

		existingAccount.setPrivilege(privilegeUpdateDto.getPrivilege());
		existingAccount = accountRepository.save(existingAccount);
		log.info("PRIVILEGE UPDATED: {}", existingAccount);

		return existingAccount;
	}
}
