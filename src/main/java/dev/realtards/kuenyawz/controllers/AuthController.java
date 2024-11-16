package dev.realtards.kuenyawz.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

//	private final AuthService authService;

	@Operation(summary = "Register a new user")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "User registered successfully"),
		@ApiResponse(responseCode = "409", description = "User exists")
	})
	@PostMapping("/register")
	public ResponseEntity<Object> register() {
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Login as an existing user")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Login successful"),
		@ApiResponse(responseCode = "401", description = "Invalid credentials")
	})
	@PostMapping("/login")
	public ResponseEntity<Object> login() {
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Revoke the current access token")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Token revoked successfully")
	})
	@PostMapping("/revoke")
	public ResponseEntity<Object> revoke() {
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Refresh the current access token")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Token refreshed successfully")
	})
	@PostMapping("/refresh")
	public ResponseEntity<Object> refresh() {
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Get the current user's information")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "User information retrieved successfully"),
		@ApiResponse(responseCode = "404", description = "User not found")
	})
	@PostMapping("/me")
	public ResponseEntity<Object> me() {
		return ResponseEntity.ok().build();
	}
}
