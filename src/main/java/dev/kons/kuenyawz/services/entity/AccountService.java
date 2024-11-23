package dev.kons.kuenyawz.services.entity;

import dev.kons.kuenyawz.dtos.account.*;
import dev.kons.kuenyawz.entities.Account;
import dev.kons.kuenyawz.exceptions.AccountExistsException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AccountService {

	@Transactional(readOnly = true)
	List<AccountSecureDto> getAllAccounts();

	@Transactional
	Account createAccount(AccountRegistrationDto accountRegistrationDto);

	@Transactional(readOnly = true)
	Account getAccount(long accountId);

	@Transactional(readOnly = true)
	Account getAccount(String phone);

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

	@Transactional(readOnly = true)
	boolean passwordMatches(String password, Account account);

	@Transactional(readOnly = true)
	void validatePhoneNoDuplicate(String phone) throws AccountExistsException;
}
