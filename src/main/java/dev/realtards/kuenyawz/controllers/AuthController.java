package dev.realtards.kuenyawz.controllers;

import dev.realtards.kuenyawz.dtos.account.AccountRegistrationDto;
import dev.realtards.kuenyawz.dtos.account.AccountSecureDto;
import dev.realtards.kuenyawz.dtos.auth.*;
import dev.realtards.kuenyawz.services.AuthService;
import dev.realtards.kuenyawz.services.OTPService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
	private final OTPService otpService;

	@Operation(summary = "Register a new user")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "User registered successfully",
			content = @Content(
				mediaType = MediaType.APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = AuthResponseDto.class)
			)),
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
		@ApiResponse(responseCode = "200", description = "Login successful",
			content = @Content(
				mediaType = MediaType.APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = AuthResponseDto.class)
			)),
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
		@ApiResponse(responseCode = "200", description = "Token refreshed successfully",
			content = @Content(
				mediaType = MediaType.APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = AuthResponseDto.class)
			))
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
		@ApiResponse(responseCode = "200", description = "User information retrieved successfully",
			content = @Content(
				mediaType = MediaType.APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = AccountSecureDto.class)
			)),
		@ApiResponse(responseCode = "404", description = "User not found")
	})
	@SecurityRequirement(name = "bearerAuth", scopes = {"USER", "ADMIN"})
	@GetMapping("/me")
	public ResponseEntity<Object> me() {
		AccountSecureDto accountSecureDto = authService.getCurrentUserInfo();
		return ResponseEntity.status(HttpStatus.OK).body(accountSecureDto);
	}

	@Operation(summary = "Request an OTP to be sent to the user's phone number")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OTP sent successfully")
	})
	@PostMapping("/otp/request")
	public ResponseEntity<?> requestOtp(
		@Valid @RequestBody OtpRequestDto otpRequestDto
	) {
		otpService.sendOTP(otpRequestDto);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Verify the OTP sent to the user's phone number")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OTP verified successfully")
	})
	@PostMapping("/otp/verify")
	public ResponseEntity<?> verifyOtp(
		@Valid @RequestBody OtpVerifyDto otpVerifyDto
	) {
		otpService.verifyOTP(otpVerifyDto);
		return ResponseEntity.ok().build();
	}
}
