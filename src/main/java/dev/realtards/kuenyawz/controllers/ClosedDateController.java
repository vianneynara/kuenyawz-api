package dev.realtards.kuenyawz.controllers;

import dev.realtards.kuenyawz.dtos.closeddate.ClosedDatePatchDto;
import dev.realtards.kuenyawz.dtos.closeddate.ClosedDatePostDto;
import dev.realtards.kuenyawz.services.entity.ClosedDateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Closure Routes", description = "Endpoints for managing custom closure dates")
@RequestMapping("closure")
@RestController
@RequiredArgsConstructor
@Validated
public class ClosedDateController {

	private final ClosedDateService closedDateService;

	@Operation(summary = "Get closed dates")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Successfully retrieved closed dates"),
		@ApiResponse(responseCode = "400", description = "Invalid request parameters")
	})
	@GetMapping
	public ResponseEntity<?> getClosedDates(
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
		@RequestParam(required = false, defaultValue = "0") int page,
		@RequestParam(required = false, defaultValue = "10") int pageSize
	) {
		if (date != null) {
			return ResponseEntity.ok(closedDateService.getByDate(date));
		} else if (from == null && to == null) {
			return ResponseEntity.ok(closedDateService.getAll(page, pageSize));
		} else if (from != null && to == null) {
			return ResponseEntity.ok(closedDateService.getAllAfter(from, page, pageSize));
		} else {
			return ResponseEntity.ok(closedDateService.getAllBetween(from, to, page, pageSize));
		}
	}

	@Operation(summary = "Get closed date by ID")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Successfully retrieved closed date"),
		@ApiResponse(responseCode = "400", description = "Invalid request parameters"),
		@ApiResponse(responseCode = "404", description = "Closed date not found")
	})
	@GetMapping("/{closedDateId}")
	public ResponseEntity<?> getClosedDate(@PathVariable Long closedDateId) {
		final var result = closedDateService.getById(closedDateId);
		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Create closed date")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Successfully created closed date"),
		@ApiResponse(responseCode = "400", description = "Invalid request parameters"),
		@ApiResponse(responseCode = "409", description = "Duplicate date")
	})
	@SecurityRequirement(name = "cookieAuth")
	@PostMapping
	public ResponseEntity<?> createClosedDate(@RequestBody List<@Valid ClosedDatePostDto> closedDatePostDtos) {
		final var result = closedDateService.create(closedDatePostDtos);
		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Update a closed date")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Successfully updated closed date"),
		@ApiResponse(responseCode = "400", description = "Invalid request parameters"),
		@ApiResponse(responseCode = "404", description = "Closed date not found"),
		@ApiResponse(responseCode = "409", description = "Duplicate date")
	})
	@SecurityRequirement(name = "cookieAuth")
	@PatchMapping("/{closedDateId}")
	public ResponseEntity<?> updateClosedDate(
		@PathVariable Long closedDateId,
		@Valid @RequestBody ClosedDatePatchDto closedDatePatchDto
	) {
		final var result = closedDateService.update(closedDateId, closedDatePatchDto);
		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Delete a closed date")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Successfully deleted closed date"),
		@ApiResponse(responseCode = "400", description = "Invalid request parameters"),
		@ApiResponse(responseCode = "404", description = "Closed date not found")
	})
	@SecurityRequirement(name = "cookieAuth")
	@DeleteMapping("/{closedDateId}")
	public ResponseEntity<?> deleteClosedDate(@PathVariable Long closedDateId) {
		closedDateService.deleteById(closedDateId);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Delete closed dates between two dates")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Successfully deleted closed dates"),
		@ApiResponse(responseCode = "400", description = "Invalid request parameters")
	})
	@DeleteMapping
	public ResponseEntity<?> deleteClosedDates(
		@NotNull @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
		@NotNull @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
	) {
		closedDateService.deleteBetween(from, to);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Check if a date is available")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Successfully checked date availability"),
		@ApiResponse(responseCode = "400", description = "Invalid request parameters")
	})
	@GetMapping("/available")
	public ResponseEntity<?> isDateAvailable(
		@NotNull @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
	) {
		return ResponseEntity.ok(closedDateService.isDateAvailable(date));
	}
}
