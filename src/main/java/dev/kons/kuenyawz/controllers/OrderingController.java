package dev.kons.kuenyawz.controllers;

import dev.kons.kuenyawz.dtos.purchase.PurchaseDto;
import dev.kons.kuenyawz.dtos.purchase.PurchasePostDto;
import dev.kons.kuenyawz.entities.Account;
import dev.kons.kuenyawz.services.entity.PurchaseService;
import dev.kons.kuenyawz.services.logic.AuthService;
import dev.kons.kuenyawz.services.logic.OrderingService;
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
public class OrderingController {

	private final OrderingService orderingService;
	private final PurchaseService purchaseService;

	@Operation(summary = "Get purchases/orders")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Orders fetched successfully"),
		@ApiResponse(responseCode = "400", description = "Bad request")
	})
	@SecurityRequirement(name = "cookieAuth")
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
		PurchaseDto purchaseDto = orderingService.processOrder(purchasePostDto);
		return ResponseEntity.ok(purchaseDto);
	}

	@Operation(summary = "Cancels an order")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
		@ApiResponse(responseCode = "400", description = "Bad request")
	})
	@SecurityRequirement(name = "cookieAuth")
	@PostMapping("/{purchaseId}/cancel")
	public ResponseEntity<?> cancelOrder(
		@PathVariable Long purchaseId
	) {
		PurchaseDto purchaseDto = orderingService.cancelOrder(purchaseId);
		return ResponseEntity.ok(purchaseDto);
	}

	@Operation(summary = "Confirms an order")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Order confirmed successfully"),
		@ApiResponse(responseCode = "400", description = "Bad request")
	})
	@SecurityRequirement(name = "cookieAuth")
	@PostMapping("/{purchaseId}/confirm")
	public ResponseEntity<?> confirmOrder(
		@PathVariable Long purchaseId
	) {
		PurchaseDto purchaseDto = orderingService.confirmOrder(purchaseId);
		return ResponseEntity.ok(purchaseDto);
	}

	@Operation(summary = "Get a purchase/order's transaction")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Transaction fetched successfully"),
		@ApiResponse(responseCode = "400", description = "Bad request")
	})
	@SecurityRequirement(name = "cookieAuth")
	@GetMapping("/{purchaseId}/transaction")
	public ResponseEntity<?> getTransaction(
		@PathVariable Long purchaseId
	) {
		PurchaseDto purchaseDto = orderingService.findPurchase(purchaseId);
		return ResponseEntity.ok(purchaseDto);
	}

	@Operation(summary = "Upgrade an order's status to its next stage")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Order status upgraded successfully"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "401", description = "Unauthorized"),
		@ApiResponse(responseCode = "403", description = "Forbidden"),
		@ApiResponse(responseCode = "404", description = "Not found")
	})
	@SecurityRequirement(name = "cookieAuth")
	@PostMapping("/{purchaseId}/status/next")
	public ResponseEntity<?> upgradeStatus(
		@PathVariable Long purchaseId
	) {
		PurchaseDto purchaseDto = orderingService.upgradeOrderStatus(purchaseId);
		return ResponseEntity.ok(purchaseDto);
	}

	@Operation(summary = "Change an order's status")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Order status changed successfully"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "401", description = "Unauthorized"),
		@ApiResponse(responseCode = "403", description = "Forbidden"),
		@ApiResponse(responseCode = "404", description = "Not found")
	})
	@SecurityRequirement(name = "cookieAuth")
	@PostMapping("/{purchaseId}/status")
	public ResponseEntity<?> changeStatus(
		@PathVariable Long purchaseId,
		@RequestParam String status
	) {
		PurchaseDto purchaseDto = orderingService.changeOrderStatus(purchaseId, status);
		return ResponseEntity.ok(purchaseDto);
	}
}
