package dev.kons.kuenyawz.controllers;

import dev.kons.kuenyawz.dtos.purchase.PurchaseDto;
import dev.kons.kuenyawz.dtos.purchase.PurchasePostDto;
import dev.kons.kuenyawz.entities.Account;
import dev.kons.kuenyawz.services.entity.PurchaseService;
import dev.kons.kuenyawz.services.logic.AuthService;
import dev.kons.kuenyawz.services.logic.OrderProcessingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Order Processing Routes", description = "Endpoints for processing orders")
@RequestMapping("/orders")
@RestController
@RequiredArgsConstructor
@Validated
public class OrderProcessingController {

	private final OrderProcessingService orderProcessingService;
	private final PurchaseService purchaseService;

	@Operation(summary = "Get purchases/orders")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Orders fetched successfully"),
		@ApiResponse(responseCode = "400", description = "Bad request")
	})
	@GetMapping
	public ResponseEntity<?> getOrders(
		@RequestParam(required = false) Boolean isAscending,
		@RequestParam(required = false) String status,
		@RequestParam(required = false) String paymentType,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
		@RequestParam(required = false) Long accountId,
		@RequestParam(required = false) String orderBy,
		@RequestParam(required = false) Integer page,
		@RequestParam(required = false) Integer pageSize
	) {
		PurchaseService.PurchaseSearchCriteria criteria = PurchaseService.PurchaseSearchCriteria.of(
			isAscending,
			status,
			paymentType,
			from,
			to,
			accountId,
			orderBy,
			page,
			pageSize
		);
		Page<PurchaseDto> result;
		if (AuthService.isAuthenticatedAdmin()) {
			result = purchaseService.findAll(criteria);
		} else {
			Account account = AuthService.getAuthenticatedAccount();
			result = purchaseService.findAll(account.getAccountId(), criteria);
		}
		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Process an order")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Order processed successfully"),
		@ApiResponse(responseCode = "400", description = "Bad request")
	})
	@SecurityRequirement(name = "cookieAuth")
	@PostMapping
	public ResponseEntity<?> processOrder(
		@Valid @RequestBody PurchasePostDto purchasePostDto
	) {
		if (!AuthService.isAuthenticatedUser()) {
			return ResponseEntity.badRequest().body("Must be logged in to process order");
		}
		PurchaseDto purchaseDto = orderProcessingService.processOrder(purchasePostDto);
		return ResponseEntity.ok(purchaseDto);
	}
}
