package dev.realtards.kuenyawz.services.logic;

import dev.realtards.kuenyawz.configurations.ApplicationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Read <a href="https://javadoc.io/doc/io.jsonwebtoken/jjwt-api/latest/index.html">jjwt documentation</a>)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JWTServiceImpl implements JWTService {

	private final ApplicationProperties applicationProperties;
	private static final long CLOCK_SKEW_SECONDS = 60;

	@Override
	public String generateAccessToken(UserDetails userDetails) {
		return generateAccessToken(new HashMap<>(), userDetails);
	}

	@Override
	public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
		return buildToken(
			extraClaims,
			userDetails,
			applicationProperties.getSecurity().getJwtTokenExpSeconds()
		);
	}

	@Override
	public String buildToken(
		Map<String, Object> extraClaims,
		UserDetails userDetails,
		long expiration
	) {
		Instant now = Instant.now();
		return Jwts.builder()
			.claims(extraClaims)
			.subject(userDetails.getUsername())
			.issuedAt(Date.from(now))
			.expiration(Date.from(now.plusSeconds(expiration)))
			.claim("tokenType", TokenType.ACCESS.name())
			.signWith(getSignInKey())
			.compact();
	}

	@Override
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}

	@Override
	public boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	@Override
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	@Override
	public Claims extractAllClaims(String token) {
		return Jwts.parser()
			.verifyWith(getSignInKey())
			.clockSkewSeconds(CLOCK_SKEW_SECONDS)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	@Override
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	@Override
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	@Override
	public SecretKey getSignInKey() {
		byte[] bytes = Base64.getDecoder()
			.decode(applicationProperties.getSecurity().getJwtSecret().getBytes(StandardCharsets.UTF_8));
		return new SecretKeySpec(bytes, "HmacSHA256");
	}

	@Override
	public long getExpirationTime() {
		return applicationProperties.getSecurity().getJwtTokenExpSeconds();
	}

	@Override
	public long getIssuedAt(String token) {
		return extractClaim(token, claims -> claims.getIssuedAt().getTime());
	}

	@Override
	public long getExpiration(String token) {
		return extractClaim(token, claims -> claims.getExpiration().getTime());
	}

	@Override
	public boolean isAccessToken(String token) {
		return extractClaim(token, claims -> claims.get("tokenType", String.class))
			.equals(TokenType.ACCESS.name());
	}

	public enum TokenType {
		ACCESS, REFRESH
	}
}