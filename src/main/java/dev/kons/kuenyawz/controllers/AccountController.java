package dev.kons.kuenyawz.controllers;

import dev.kons.kuenyawz.configurations.ApplicationProperties;
import dev.kons.kuenyawz.dtos.account.*;
import dev.kons.kuenyawz.entities.Account;
import dev.kons.kuenyawz.exceptions.UnauthorizedException;
import dev.kons.kuenyawz.mapper.AccountMapper;
import dev.kons.kuenyawz.services.entity.AccountService;
import dev.kons.kuenyawz.services.logic.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
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
	private final ApplicationProperties properties;

	@Operation(summary = "(Master) Get all accounts",
		description = "Retrieves a list of all accounts with secure information",
		security = @SecurityRequirement(name = "cookieAuth")
	)
	@ApiResponse(responseCode = "200", description = "Successfully retrieved all accounts",
		content = @Content(
			mediaType = MediaType.APPLICATION_JSON_VALUE,
			schema = @Schema(implementation = ListOfAccountDto.class)
		)
	)
	@SecurityRequirements({
		@SecurityRequirement(name = "cookieAuth", scopes = {"ADMIN"}),
		@SecurityRequirement(name = "xApiKey")
	})
	@GetMapping
	public ResponseEntity<Object> getAllAccounts() {
		ensureRequesterAuthorized();

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
	@SecurityRequirements({
		@SecurityRequirement(name = "cookieAuth", scopes = {"ADMIN"}),
		@SecurityRequirement(name = "xApiKey")
	})
	@PostMapping
	public ResponseEntity<Object> createAccount(
		@Valid @RequestBody AccountRegistrationDto accountRegistrationDto
	) {
		ensureRequesterAuthorized();

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
	@SecurityRequirements({
		@SecurityRequirement(name = "cookieAuth", scopes = {"ADMIN"}),
		@SecurityRequirement(name = "xApiKey")
	})
	@GetMapping("{accountId}")
	public ResponseEntity<Object> getAccount(
		@PathVariable Long accountId
	) {
		ensureRequesterAuthorized();

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
	@SecurityRequirements({
		@SecurityRequirement(name = "cookieAuth", scopes = {"ADMIN", "USER"}),
		@SecurityRequirement(name = "xApiKey")
	})
	@PutMapping("{accountId}")
	public ResponseEntity<Object> updateAccount(
		@PathVariable Long accountId,
		@Valid @RequestBody AccountPutDto accountPutDto
	) {
		ensureRequesterAuthorized();

		Account account = accountService.updateAccount(accountId, accountPutDto);
		return ResponseEntity.ok(accountMapper.fromEntity(account));
	}

	@Operation(summary = "Delete an account", description = "Deletes an account with the provided account ID")
	@ApiResponse(responseCode = "204", description = "Successfully deleted account")
	@SecurityRequirements({
		@SecurityRequirement(name = "cookieAuth", scopes = {"ADMIN", "USER"}),
		@SecurityRequirement(name = "xApiKey")
	})
	@DeleteMapping("{accountId}")
	public ResponseEntity<Object> deleteAccount(
		@PathVariable Long accountId
	) {
		ensureRequesterAuthorized();

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
	@SecurityRequirements({
		@SecurityRequirement(name = "cookieAuth", scopes = {"ADMIN", "USER"}),
		@SecurityRequirement(name = "xApiKey")
	})
	@PatchMapping("{accountId}/account")
	public ResponseEntity<Object> patchAccount(
		@PathVariable Long accountId,
		@Valid @RequestBody AccountPatchDto accountPatchDto
	) {
		ensureRequesterAuthorized();

		Account account = accountService.patchAccount(accountId, accountPatchDto);
		return ResponseEntity.ok(accountMapper.fromEntity(account));
	}

	@Operation(summary = "Patch an account's password", description = "Patches with the provided request body")
	@ApiResponse(responseCode = "204", description = "Successfully patched account's password")
	@SecurityRequirements({
		@SecurityRequirement(name = "cookieAuth", scopes = {"ADMIN", "USER"}),
		@SecurityRequirement(name = "xApiKey")
	})
	@PatchMapping("{accountId}/password")
	public ResponseEntity<Object> updatePassword(
		@PathVariable Long accountId,
		@Valid @RequestBody PasswordUpdateDto passwordUpdateDto
	) {
		ensureRequesterAuthorized();

		accountService.updatePassword(accountId, passwordUpdateDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "Patch an account's privilege", description = "Patches with the provided request body")
	@ApiResponse(responseCode = "204", description = "Successfully patched account's privilege")
	@SecurityRequirements({
		@SecurityRequirement(name = "cookieAuth", scopes = {"ADMIN"}),
		@SecurityRequirement(name = "xApiKey")
	})
	@PatchMapping("{accountId}/privilege")
	public ResponseEntity<Object> updatePrivilege(
		@PathVariable Long accountId,
		@Valid @RequestBody PrivilegeUpdateDto privilegeUpdateDto
	) {
		ensureRequesterAuthorized();

		accountService.updatePrivilege(accountId, privilegeUpdateDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	/**
	 * Helped method to ensure the requester is authorized to perform the action.
	 * This is specific for this controller so that direct call to Account endpoints are authenticated properly.
	 */
	private void ensureRequesterAuthorized() {
		if (!(AuthService.isAuthenticatedMaster(properties) || AuthService.isAuthenticatedAdmin())) {
			throw new UnauthorizedException("This action requires master or admin privileges");
		}
	}
}
