package dev.kons.kuenyawz.advice;

import dev.kons.kuenyawz.advice.responses.ErrorResponse;
import dev.kons.kuenyawz.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Map;

@Slf4j
@ControllerAdvice
@Order(2)
public class CustomExceptionsHandler {

	@ExceptionHandler(AccountNotFoundException.class)
	public ResponseEntity<Object> handleAccountNotFoundException(AccountNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.of(ex.getMessage()));
	}

	@ExceptionHandler(AccountExistsException.class)
	public ResponseEntity<Object> handleAccountExistsException(AccountExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.of(ex.getMessage()));
	}

	@ExceptionHandler(InvalidPasswordException.class)
	public ResponseEntity<Object> handleInvalidPasswordException(InvalidPasswordException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.of(ex.getMessage()));
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<Object> handleIncorrectCredentialsException(InvalidCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse.of(ex.getMessage()));
	}

	@ExceptionHandler(PasswordMismatchException.class)
	public ResponseEntity<Object> handlePasswordMismatchException(PasswordMismatchException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.of(ex.getMessage()));
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse.of(ex.getMessage()));
	}

	@ExceptionHandler(InvalidRequestBodyValue.class)
	public ResponseEntity<Object> handleInvalidRequestBodyValue(InvalidRequestBodyValue ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.of(ex.getMessage()));
	}

	@ExceptionHandler(IllegalOperationException.class)
	public ResponseEntity<Object> handleIllegalOperationException(IllegalOperationException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.of(ex.getMessage()));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.of(ex.getMessage()));
	}

	@ExceptionHandler(ResourceExistsException.class)
	public ResponseEntity<Object> handleResourceExistsException(ResourceExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.of(ex.getMessage()));
	}

	@ExceptionHandler(ResourceUploadException.class)
	public ResponseEntity<Object> handleResourceUploadException(ResourceUploadException ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.of(ex.getMessage()));
	}

	@ExceptionHandler(SpecificationException.class)
	public ResponseEntity<Object> handleSpecificationException(SpecificationException ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.of(ex.getMessage()));
	}

	@ExceptionHandler(MidtransTransactionException.class)
	public ResponseEntity<Object> handleMidtransTransactionException(MidtransTransactionException ex) {
		List<Map<String, Object>> body = List.of(
			Map.of("message", ex.getMessage()),
			Map.of("errors", ex.getErrorResponse().getErrorMessages())
		);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
	}
}
