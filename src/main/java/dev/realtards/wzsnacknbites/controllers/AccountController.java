package dev.realtards.wzsnacknbites.controllers;

import dev.realtards.wzsnacknbites.dtos.AccountRegistrationDto;
import dev.realtards.wzsnacknbites.dtos.AccountSecureDto;
import dev.realtards.wzsnacknbites.exceptions.EmailExistsException;
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
	public ResponseEntity<Object> getAccounts() {
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
		} catch (EmailExistsException e) {
			return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity
				.badRequest()
				.body(e.getMessage());
		}
	}
}
