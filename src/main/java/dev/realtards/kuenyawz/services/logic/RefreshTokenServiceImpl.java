package dev.realtards.kuenyawz.services.logic;

import dev.realtards.kuenyawz.configurations.ApplicationProperties;
import dev.realtards.kuenyawz.entities.Account;
import dev.realtards.kuenyawz.entities.RefreshToken;
import dev.realtards.kuenyawz.exceptions.ResourceNotFoundException;
import dev.realtards.kuenyawz.exceptions.UnauthorizedException;
import dev.realtards.kuenyawz.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

	private final ApplicationProperties properties;
	private final RefreshTokenRepository refreshTokenRepository;

	@Override
	public RefreshToken createRefreshToken(UserDetails userDetails) {
		Account account = (Account) userDetails;

		String token = generateSecureToken();

		RefreshToken refreshToken = RefreshToken.builder()
			.token(token)
			.accountId(account.getAccountId())
			.username(account.getUsername())
			.expiresAt(LocalDateTime.now().plusDays(properties.getSecurity().getJwtRefreshDays() * 24))
			.isRevoked(false)
			.build();

		refreshToken = refreshTokenRepository.save(refreshToken);
		return refreshToken;
	}

	@Override
	public RefreshToken verifyRefreshToken(String token) {
		RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
			.orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

		if (refreshToken.isRevoked()) {
			throw new UnauthorizedException("Refresh token has been revoked");
		}

		if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
			refreshToken.setRevoked(true);
			refreshTokenRepository.save(refreshToken);
			throw new UnauthorizedException("Refresh token has expired");
		}

		return refreshToken;
	}

	@Override
	public void revokeRefreshToken(String token) {
		RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
			.orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

		refreshToken.setRevoked(true);
		refreshTokenRepository.save(refreshToken);
	}

	@Override
	public void revokeAllRefreshTokensOf(Long accountId) {
		List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByAccountId(accountId);
		refreshTokens.forEach(refreshToken -> refreshToken.setRevoked(true));
		refreshTokenRepository.saveAll(refreshTokens);
	}

	@Override
	public String generateSecureToken() {
		byte[] randomBytes = new byte[64];
		new SecureRandom().nextBytes(randomBytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
	}
}
