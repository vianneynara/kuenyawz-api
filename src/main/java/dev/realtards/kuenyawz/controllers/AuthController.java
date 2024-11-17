package dev.realtards.kuenyawz.controllers;

import dev.realtards.kuenyawz.dtos.account.AccountRegistrationDto;
import dev.realtards.kuenyawz.dtos.account.AccountSecureDto;
import dev.realtards.kuenyawz.dtos.auth.AuthRefreshTokenDto;
import dev.realtards.kuenyawz.dtos.auth.AuthRequestDto;
import dev.realtards.kuenyawz.dtos.auth.AuthResponseDto;
import dev.realtards.kuenyawz.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication Routes", description = "Authentication and authorization endpoints")
@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@Validated
public class AuthController {

	private final AuthService authService;

	@Operation(summary = "Register a new user")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "User registered successfully"),
		@ApiResponse(responseCode = "409", description = "User exists")
	})
	@PostMapping("/register")
	public ResponseEntity<Object> register(
		@Valid @RequestBody AccountRegistrationDto accountRegistrationDto
	) {
		AuthResponseDto response = authService.register(accountRegistrationDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "Login as an existing user")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Login successful"),
		@ApiResponse(responseCode = "401", description = "Invalid credentials")
	})
	@PostMapping("/login")
	public ResponseEntity<Object> login(
		@Valid @RequestBody AuthRequestDto authRequestDto
	) {
		AuthResponseDto response = authService.login(authRequestDto);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "Revoke the current refresh token")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Token revoked successfully")
	})
	@PostMapping("/revoke")
	public ResponseEntity<Object> revoke(
		@Valid @RequestBody AuthRefreshTokenDto tokenDto
	) {
		authService.revokeRefreshToken(tokenDto.getRefreshToken());
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Refresh the current access token")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Token refreshed successfully")
	})
	@PostMapping("/refresh")
	public ResponseEntity<Object> refresh(
		@Valid @RequestBody AuthRefreshTokenDto tokenDto
	) {
		AuthResponseDto response = authService.refresh(tokenDto.getRefreshToken());
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "Get the current user's information from Authorization header")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "User information retrieved successfully"),
		@ApiResponse(responseCode = "404", description = "User not found")
	})
	@GetMapping("/me")
	public ResponseEntity<Object> me() {
		AccountSecureDto accountSecureDto = authService.getCurrentUserInfo();
		return ResponseEntity.status(HttpStatus.OK).body(accountSecureDto);
	}
}
