package dev.realtards.kuenyawz.advice;

import dev.realtards.kuenyawz.advice.responses.ErrorResponse;
import dev.realtards.kuenyawz.exceptions.InvalidRefreshTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.UnsupportedKeyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * This class helps to handle error thrown by JWT stuffs.
 */
@Slf4j
@ControllerAdvice
@Order(1)
public class JwtExceptionsHandler {

	@ExceptionHandler(UnsupportedKeyException.class)
	public ResponseEntity<Object> handleUnsupportedKeyException(UnsupportedKeyException ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.of("The key is too weak"));
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.of(ex.getMessage()));
	}

	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<Object> handleExpiredJwtException(ExpiredJwtException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse.of(ex.getMessage()));
	}

	@ExceptionHandler(MalformedJwtException.class)
	public ResponseEntity<Object> handleMalformedJwtException(MalformedJwtException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse.of(ex.getMessage()));
	}

	@ExceptionHandler(SignatureException.class)
	public ResponseEntity<Object> handleSignatureException(SignatureException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse.of("Invalid signature"));
	}

	@ExceptionHandler(InvalidRefreshTokenException.class)
	public ResponseEntity<Object> handleInvalidRefreshTokenException(InvalidRefreshTokenException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.of(ex.getMessage()));
	}
}
