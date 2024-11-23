package dev.realtards.kuenyawz.services.logic;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface JWTService {

	String generateAccessToken(UserDetails userDetails);

	String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails);

	String buildToken(
		Map<String, Object> extraClaims,
		UserDetails userDetails,
		long expiration
	);

	boolean isTokenValid(String token, UserDetails userDetails);

	boolean isTokenExpired(String token);

	Date extractExpiration(String token);

	Claims extractAllClaims(String token);

	<T> T extractClaim(String token, Function<Claims, T> claimsResolver);

	String extractUsername(String token);

	SecretKey getSignInKey();

	long getExpirationTime();

	long getIssuedAt(String token);

	long getExpiration(String token);

	boolean isAccessToken(String token);
}
