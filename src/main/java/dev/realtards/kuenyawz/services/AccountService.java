package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.dtos.account.*;
import dev.realtards.kuenyawz.models.Account;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AccountService {

	@Transactional(readOnly = true)
	List<Account> getAllAccounts();

	@Transactional
	Account createAccount(AccountRegistrationDto accountRegistrationDto);

	@Transactional(readOnly = true)
	Account getAccount(long accountId);

	@Transactional
	Account updateAccount(Long accountId, AccountPutDto accountPutDto);

	@Transactional
	void deleteAccount(long accountId);

	@Transactional
	Account patchAccount(Long accountId, AccountPatchDto accountPatchDto);

	@Transactional
	Account updatePassword(Long accountId, PasswordUpdateDto passwordUpdateDto);

	@Transactional
	Account updatePrivilege(Long accountId, PrivilegeUpdateDto privilegeUpdateDto);
}