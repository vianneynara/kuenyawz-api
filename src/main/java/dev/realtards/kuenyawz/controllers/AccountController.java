package dev.realtards.kuenyawz.controllers;

import dev.realtards.kuenyawz.dtos.account.*;
import dev.realtards.kuenyawz.entities.Account;
import dev.realtards.kuenyawz.mapper.AccountMapper;
import dev.realtards.kuenyawz.dtos.account.ListOfAccountDto;
import dev.realtards.kuenyawz.services.entity.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Account Routes", description = "Endpoints for managing user accounts")
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Validated
public class AccountController extends BaseController {

	private final AccountService accountService;
	private final AccountMapper accountMapper;

	@Operation(summary = "(Master) Get all accounts",
		description = "Retrieves a list of all accounts with secure information",
		security = @SecurityRequirement(name = "bearerAuth")
	)
	@ApiResponse(responseCode = "200", description = "Successfully retrieved all accounts",
		content = @Content(
			mediaType = MediaType.APPLICATION_JSON_VALUE,
			schema = @Schema(implementation = ListOfAccountDto.class)
		)
	)
	@SecurityRequirement(name = "bearerAuth", scopes = {"ADMIN"})
	@GetMapping
	public ResponseEntity<Object> getAllAccounts() {
		List<AccountSecureDto> accounts = accountService.getAllAccounts();

		return ResponseEntity.status(HttpStatus.OK).body(new ListOfAccountDto(accounts));
	}

	// CRUD operations

	@Operation(summary = "Create an account", description = "Creates a new account with the provided request body")
	@ApiResponse(responseCode = "201", description = "Successfully created account",
		content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
			schema = @Schema(implementation = AccountSecureDto.class),
			examples = @ExampleObject(value = """
				{
				  "accountId": 1221991247904768,
				  "fullName": "Emilia",
				  "googleId": null,
				  "email": "emilia@example.com",
				  "emailVerifiedAt": null,
				  "phone": null,
				  "privilege": "user"
				}
				"""
			)
		)
	)
	@SecurityRequirement(name = "bearerAuth", scopes = {"ADMIN"})
	@PostMapping
	public ResponseEntity<Object> createAccount(
		@Valid @RequestBody AccountRegistrationDto accountRegistrationDto
	) {
		Account account = accountService.createAccount(accountRegistrationDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(accountMapper.fromEntity(account));
	}

	@Operation(summary = "Get an account", description = "Retrieves an account with the provided account ID")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved account",
		content = @Content(
			mediaType = MediaType.APPLICATION_JSON_VALUE,
			schema = @Schema(implementation = AccountSecureDto.class)
		)
	)
	@SecurityRequirement(name = "bearerAuth", scopes = {"ADMIN", "USER"})
	@GetMapping("{accountId}")
	public ResponseEntity<Object> getAccount(
		@PathVariable Long accountId
	) {
		Account account = accountService.getAccount(accountId);
		return ResponseEntity.ok(accountMapper.fromEntity(account));
	}

	@Operation(summary = "Update an account", description = "Updates an account with the provided request body")
	@ApiResponse(responseCode = "200", description = "Successfully updated account",
		content = @Content(
			mediaType = MediaType.APPLICATION_JSON_VALUE,
			schema = @Schema(implementation = AccountSecureDto.class)
		)
	)
	@SecurityRequirement(name = "bearerAuth", scopes = {"ADMIN", "USER"})
	@PutMapping("{accountId}")
	public ResponseEntity<Object> updateAccount(
		@PathVariable Long accountId,
		@Valid @RequestBody AccountPutDto accountPutDto
	) {
		Account account = accountService.updateAccount(accountId, accountPutDto);
		return ResponseEntity.ok(accountMapper.fromEntity(account));
	}

	@Operation(summary = "Delete an account", description = "Deletes an account with the provided account ID")
	@ApiResponse(responseCode = "204", description = "Successfully deleted account")
	@SecurityRequirement(name = "bearerAuth", scopes = {"ADMIN"})
	@DeleteMapping("{accountId}")
	public ResponseEntity<Object> deleteAccount(
		@PathVariable Long accountId
	) {
		accountService.deleteAccount(accountId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	// PATCH

	@Operation(summary = "Patch an account", description = "Patches with the provided request body")
	@ApiResponse(responseCode = "200", description = "Successfully patched account",
		content = @Content(
			mediaType = MediaType.APPLICATION_JSON_VALUE,
			schema = @Schema(implementation = AccountSecureDto.class)
		)
	)
	@SecurityRequirement(name = "bearerAuth", scopes = {"ADMIN", "USER"})
	@PatchMapping("{accountId}/account")
	public ResponseEntity<Object> patchAccount(
		@PathVariable Long accountId,
		@Valid @RequestBody AccountPatchDto accountPatchDto
	) {
		Account account = accountService.patchAccount(accountId, accountPatchDto);
		return ResponseEntity.ok(accountMapper.fromEntity(account));
	}

	@Operation(summary = "Patch an account's password", description = "Patches with the provided request body")
	@ApiResponse(responseCode = "204", description = "Successfully patched account's password")
	@SecurityRequirement(name = "bearerAuth", scopes = {"ADMIN", "USER"})
	@PatchMapping("{accountId}/password")
	public ResponseEntity<Object> updatePassword(
		@PathVariable Long accountId,
		@Valid @RequestBody PasswordUpdateDto passwordUpdateDto
	) {
		accountService.updatePassword(accountId, passwordUpdateDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "Patch an account's privilege", description = "Patches with the provided request body")
	@ApiResponse(responseCode = "204", description = "Successfully patched account's privilege")
	@SecurityRequirement(name = "bearerAuth", scopes = {"ADMIN"})
	@PatchMapping("{accountId}/privilege")
	public ResponseEntity<Object> updatePrivilege(
		@PathVariable Long accountId,
		@Valid @RequestBody PrivilegeUpdateDto privilegeUpdateDto
	) {
		accountService.updatePrivilege(accountId, privilegeUpdateDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
