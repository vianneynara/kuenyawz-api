package dev.realtards.wzsnacknbites.controllers;

import dev.realtards.wzsnacknbites.dtos.account.*;
import dev.realtards.wzsnacknbites.exceptions.AccountExistsException;
import dev.realtards.wzsnacknbites.models.Account;
import dev.realtards.wzsnacknbites.responses.AccountsResponse;
import dev.realtards.wzsnacknbites.services.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Validated
public class AccountController extends BaseController {

	private final AccountService accountService;

	@GetMapping("/all")
	public ResponseEntity<Object> getAllAccounts() {
		List<AccountSecureDto> accounts = accountService.getAllAccounts()
			.stream()
			.map(AccountSecureDto::new)
			.toList();

		return ResponseEntity.ok(new AccountsResponse(accounts));
	}

	// CRUD operations

	@PostMapping
	public ResponseEntity<Object> createAccount(
		@Valid @RequestBody AccountRegistrationDto accountRegistrationDto
	) {
		try {
			Account account = accountService.createAccount(accountRegistrationDto);
			return ResponseEntity
				.ok().
				body(Map.of("account", new AccountSecureDto(account)));
		} catch (AccountExistsException e) {
			return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity
				.badRequest()
				.body(e.getMessage());
		}
	}

	@GetMapping("{accountId}")
	public ResponseEntity<Object> getAccount(
		@PathVariable Long accountId
	) {
		Account account = accountService.getAccount(accountId);
		return ResponseEntity.ok(new AccountSecureDto(account));
	}

	@PutMapping("{accountId}")
	public ResponseEntity<Object> updateAccount(
		@PathVariable Long accountId,
		@Valid @RequestBody AccountPutDto accountPutDto
	) {
		Account account = accountService.updateAccount(accountId, accountPutDto);
		return ResponseEntity.ok(new AccountSecureDto(account));
	}

	@DeleteMapping("{accountId}")
	public ResponseEntity<Object> deleteAccount(
		@PathVariable Long accountId
	) {
		accountService.deleteAccount(accountId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PatchMapping("{accountId}/account")
	public ResponseEntity<Object> patchAccount(
		@PathVariable Long accountId,
		@Valid @RequestBody AccountPatchDto accountPatchDto
	) {
		Account account = accountService.patchAccount(accountId, accountPatchDto);
		return ResponseEntity.ok(new AccountSecureDto(account));
	}

	@PatchMapping("{accountId}/password")
	public ResponseEntity<Object> updatePassword(
		@PathVariable Long accountId,
		@Valid @RequestBody PasswordUpdateDto passwordUpdateDto
	) {
		accountService.updatePassword(accountId, passwordUpdateDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PatchMapping("{accountId}/privilege")
	public ResponseEntity<Object> updatePrivilege(
		@PathVariable Long accountId,
		@Valid @RequestBody PrivilegeUpdateDto privilegeUpdateDto
	) {
		accountService.updatePrivilege(accountId, privilegeUpdateDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
