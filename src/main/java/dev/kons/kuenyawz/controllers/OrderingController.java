package dev.kons.kuenyawz.controllers;

import dev.kons.kuenyawz.dtos.purchase.PurchaseDto;
import dev.kons.kuenyawz.dtos.purchase.PurchasePostDto;
import dev.kons.kuenyawz.dtos.purchase.TransactionDto;
import dev.kons.kuenyawz.entities.Account;
import dev.kons.kuenyawz.services.entity.PurchaseService;
import dev.kons.kuenyawz.services.logic.AuthService;
import dev.kons.kuenyawz.services.logic.OrderingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
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
import java.util.Map;

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
		@RequestParam(required = false) @Schema(description = "statuses, separated by commas") String statuses,
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
			statuses,
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

	@Operation(summary = "Get a purchase/order")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Order fetched successfully"),
		@ApiResponse(responseCode = "400", description = "Bad request")
	})
	@SecurityRequirement(name = "cookieAuth")
	@GetMapping("/{purchaseId}")
	public ResponseEntity<?> getOrder(
		@PathVariable Long purchaseId
	) {
		PurchaseDto purchaseDto = orderingService.findPurchase(purchaseId);
		return ResponseEntity.ok(purchaseDto);
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

	@Operation(summary = "Refunds an order")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Order refunded successfully"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "401", description = "Unauthorized"),
		@ApiResponse(responseCode = "403", description = "Illegal operation occurred, can not refund order with current status"),
	})
	@SecurityRequirement(name = "cookieAuth")
	@PostMapping("/{purchaseId}/refund")
	public ResponseEntity<?> refundOrder(
		@PathVariable Long purchaseId
	) {
		PurchaseDto purchaseDto = orderingService.refundOrder(purchaseId);
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
		TransactionDto transactionDto = orderingService.findTransactionOfPurchase(purchaseId);
		return ResponseEntity.ok(transactionDto);
	}

	@Operation(summary = "Get the next status of an order")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Next status fetched successfully"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "403", description = "Next status states unavailable"),
		@ApiResponse(responseCode = "404", description = "Not found")
	})
	@SecurityRequirement(name = "cookieAuth")
	@GetMapping("/{purchaseId}/status/next")
	public ResponseEntity<?> getNextStatus(
		@PathVariable Long purchaseId
	) {
		Map<String, String> statuses = orderingService.availableStatuses(purchaseId);
		return ResponseEntity.ok(statuses);
	}

	@Operation(summary = "Upgrade an order's status to its next stage")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Order status upgraded successfully"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "401", description = "Unauthorized"),
		@ApiResponse(responseCode = "403", description = "Can not proceed to next status"),
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
		@ApiResponse(responseCode = "403", description = "Can not proceed to next status"),
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
