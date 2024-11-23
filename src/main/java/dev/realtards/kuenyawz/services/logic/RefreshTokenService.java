package dev.realtards.kuenyawz.services.logic;

import dev.realtards.kuenyawz.entities.RefreshToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenService {
	/**
	 * Creates a refresh token for a user.
	 *
	 * @param userDetails user details
	 * @return {@link RefreshToken}
	 */
	@Transactional
	RefreshToken createRefreshToken(UserDetails userDetails);

	/**
	 * Verifies a refresh token by checking whether it has been revoked or expired.
	 *
	 * @param token refresh token
	 * @return {@link RefreshToken} if the token is valid
	 */
	RefreshToken verifyRefreshToken(String token);

	/**
	 * Revokes a refresh token.
	 *
	 * @param token refresh token to be revoked
	 */
	@Transactional
	void revokeRefreshToken(String token);

	/**
	 * Revokes all refresh tokens of an account.
	 *
	 * @param accountId
	 */
	@Transactional
	void revokeAllRefreshTokensOf(Long accountId);

	/**
	 * Generates a secure token using Java SecureRandom. The bytes are then encoded to
	 * Base64.
	 *
	 * @return {@link String} secure token
	 */
	String generateSecureToken();
}
