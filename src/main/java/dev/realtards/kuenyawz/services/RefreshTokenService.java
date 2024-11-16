package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.entities.RefreshToken;
import org.springframework.security.core.userdetails.UserDetails;

public interface RefreshTokenService {

	RefreshToken createRefreshToken(UserDetails userDetails);

	RefreshToken verifyRefreshToken(String token);

	void revokeRefreshToken(String token);

	String generateSecureToken();
}
