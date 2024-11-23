package dev.realtards.kuenyawz.advice;

import dev.realtards.kuenyawz.advice.responses.ErrorResponse;
import dev.realtards.kuenyawz.advice.responses.ListedErrors;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.TransactionalException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class helps to handle error thrown by Spring Boot beans.
 */
@Slf4j
@ControllerAdvice
@Order(3)
public class GlobalExceptionsHandler {
	/**
	 * Unhandled exception handler.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGenericException(Exception ex) {
		log.error("Unhandled exception", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.of("An error occurred internally"));
	}

	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<Object> handleMultipartException(MultipartException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.of(ex.getMessage()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.of(ex.getMessage()));
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Object> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ErrorResponse.of(ex.getMessage()));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<Object> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.of(ex.getMessage()));
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.of("Resource might not exist"));
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.of(ex.getMessage()));
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
			.body(ListedErrors.of("Validation failed", errors));
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
			.body(ListedErrors.of("Validation failed", errors));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
		MethodArgumentTypeMismatchException ex) {
		String message = String.format("Failed to convert value '%s' to type %s",
			ex.getValue(), Objects.requireNonNull(ex.getRequiredType()).getSimpleName());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.of(message));
	}
}
