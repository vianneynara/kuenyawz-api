package dev.kons.kuenyawz.controllers;

import dev.kons.kuenyawz.dtos.purchase.PurchaseDto;
import dev.kons.kuenyawz.dtos.purchase.PurchasePostDto;
import dev.kons.kuenyawz.services.logic.AuthService;
import dev.kons.kuenyawz.services.logic.OrderProcessingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order Processing Routes", description = "Endpoints for processing orders")
@RequestMapping("/orders")
@RestController
@RequiredArgsConstructor
@Validated
public class OrderProcessingController {

	private final OrderProcessingService orderProcessingService;

	@Operation(summary = "Process an order")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order processed successfully"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request")
	})
	@SecurityRequirement(name = "cookieAuth")
	@PostMapping
	public ResponseEntity<?> processOrder(
		@Valid @RequestBody PurchasePostDto purchasePostDto
	) {
		AuthService.isAuthenticatedUser();
		PurchaseDto purchaseDto = orderProcessingService.processOrder(purchasePostDto);
		return ResponseEntity.ok(purchaseDto);
	}
}
