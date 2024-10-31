package dev.realtards.kuenyawz.advice;

import dev.realtards.kuenyawz.exceptions.*;
import dev.realtards.kuenyawz.responses.ErrorResponse;
import dev.realtards.kuenyawz.responses.ListedErrors;
import jakarta.transaction.TransactionalException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class helps to handle error thrown by Spring Boot beans.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AccountNotFoundException.class)
	public ResponseEntity<Object> handleAccountNotFoundException(AccountNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(AccountExistsException.class)
	public ResponseEntity<Object> handleAccountExistsException(AccountExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<Object> handleIncorrectCredentialsException(InvalidCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(InvalidRequestBodyValue.class)
	public ResponseEntity<Object> handleInvalidRequestBodyValue(InvalidRequestBodyValue ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(IllegalOperationException.class)
	public ResponseEntity<Object> handleIllegalOperationException(IllegalOperationException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(ResourceExistsException.class)
	public ResponseEntity<Object> handleResourceExistsException(ResourceExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(ResourceUploadException.class)
	public ResponseEntity<Object> handleResourceUploadException(ResourceUploadException ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<Object> handleMultipartException(MultipartException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
	}

	// watch

	@ExceptionHandler(InvalidPasswordException.class)
	public ResponseEntity<Object> handleInvalidPasswordException(InvalidPasswordException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(PasswordMismatchException.class)
	public ResponseEntity<Object> handlePasswordMismatchException(PasswordMismatchException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(TransactionSystemException.class)
	public ResponseEntity<Object> handleTransactionSystemException(TransactionSystemException ex) {
		Throwable cause = ex.getCause();
		if (cause instanceof TransactionalException) {
			cause = cause.getCause();
		}

		if (cause instanceof ConstraintViolationException) {
			return handleConstraintViolationException((ConstraintViolationException) cause);
		}

		if (cause instanceof InvalidCredentialsException) {
			return handleIncorrectCredentialsException((InvalidCredentialsException) cause);
		}

		return handleGenericException(ex);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
		HashMap<String, String> errors = ex.getConstraintViolations()
			.stream()
			.collect(Collectors.toMap(
				violation -> violation.getPropertyPath().toString(),
				violation -> Objects.requireNonNull(violation.getMessage()),
				(existing, replacement) -> replacement,
				HashMap::new
			));

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new ListedErrors<Map<String, String>>("Validation failed", errors));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {
		HashMap<String, String> errors = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.collect(Collectors.toMap(
				FieldError::getField,
				fieldError -> Objects.requireNonNull(fieldError.getDefaultMessage()),
				(existing, replacement) -> replacement,
				HashMap::new
			));

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new ListedErrors<Map<String, String>>("Validation failed", errors));
	}

	// TODO: Add handlers for Spring standard exceptions

	/**
	 * Unhandled exception handler.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGenericException(Exception ex) {
		log.error("Unhandled exception", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An error occurred internally"));
	}
}
