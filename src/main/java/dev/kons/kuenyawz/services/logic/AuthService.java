package dev.kons.kuenyawz.services.logic;

import dev.kons.kuenyawz.dtos.account.AccountRegistrationDto;
import dev.kons.kuenyawz.dtos.account.AccountSecureDto;
import dev.kons.kuenyawz.dtos.auth.AuthRequestDto;
import dev.kons.kuenyawz.dtos.auth.AuthResponseDto;
import dev.kons.kuenyawz.entities.Account;
import dev.kons.kuenyawz.exceptions.UnauthorizedException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public interface AuthService {
	/**
	 * Registers a new user account.
	 *
	 * @param accountRegistrationDto Registration information
	 * @return {@link AuthResponseDto} with full tokens and account information
	 */
	AuthResponseDto register(AccountRegistrationDto accountRegistrationDto);

	/**
	 * Authenticates a user credentials and returns an access token with
	 * refresh token.
	 *
	 * @param accountLoginDto Login information
	 * @return {@link AuthResponseDto} with full tokens and account information
	 */
	AuthResponseDto login(AuthRequestDto accountLoginDto);

	/**
	 * Refreshes an active access token with refresh token.
	 *
	 * @param token The refresh token to use
	 * @return {@link AuthResponseDto} with new access token
	 */
	AuthResponseDto refresh(String token);

	/**
	 * Revokes an active refresh token.
	 *
	 * @param token The token to revoke
	 */
	void revokeRefreshToken(String token);

	/**
	 * Returns the corresponding user information of the token.
	 *
	 * @param token The token to extract user information from
	 * @return {@link AccountSecureDto} user information
	 */
	AccountSecureDto getUserInfo(String token);

	/**
	 * Returns the corresponding user information of the account id.
	 *
	 * @param accountId The token to extract user information from
	 * @return {@link AccountSecureDto} user information
	 */
	AccountSecureDto getUserInfo(Long accountId);

	/**
	 * Returns the corresponding user information an authentication.
	 *
	 * @param authentication The authentication to peek
	 * @return {@link AccountSecureDto} user information
	 */
	AccountSecureDto getUserInfo(Authentication authentication);

	/**
	 * Returns the current user information from an authenticated session.
	 *
	 * @return {@link AccountSecureDto} user information
	 */
	AccountSecureDto getCurrentUserInfo();

	/**
	 * Validate a token to check whether it has expired or not.
	 *
	 * @param token The token to validate
	 * @return {@code true} if the token is valid, {@code false} otherwise
	 */
	boolean validateToken(String token);

	// Static authentication methods

	static Account getAuthenticatedAccount() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof Account) {
			return (Account) principal;
		}
		throw new EntityNotFoundException("Account not found");
	}

	static void validateIsAdmin() {
		Account account = getAuthenticatedAccount();
		if (account.getPrivilege() != Account.Privilege.ADMIN) {
			throw new UnauthorizedException("You are not authorized to perform this action");
		}
	}

	static void validateMatchesId(Long accountId) {
		Account account = getAuthenticatedAccount();
		if (!accountId.equals(account.getAccountId())) {
			throw new UnauthorizedException("You are not allowed to access this resource");
		}
	}

	static boolean isAuthenticatedAdmin() {
		Account account = getAuthenticatedAccount();
		return account.getPrivilege() == Account.Privilege.ADMIN;
	}

	static boolean isAuthenticatedUser() {
		Account account = getAuthenticatedAccount();
		return account.getPrivilege() == Account.Privilege.USER;
	}

	static boolean authenticatedAccountEquals(Long accountId) {
		Account account = getAuthenticatedAccount();
		return account.getAccountId().equals(accountId);
	}
}
