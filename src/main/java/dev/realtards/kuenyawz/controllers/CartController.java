package dev.realtards.kuenyawz.controllers;

import dev.realtards.kuenyawz.dtos.cartItem.CartItemDto;
import dev.realtards.kuenyawz.dtos.cartItem.CartItemPatchDto;
import dev.realtards.kuenyawz.dtos.cartItem.CartItemPostDto;
import dev.realtards.kuenyawz.services.logic.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cart Routes", description = "Endpoints for managing user cart")
@RequestMapping("user/cart")
@RestController
@RequiredArgsConstructor
@Validated
public class CartController {

	private final CartService cartService;

	@Operation(summary = "Get cart items of a user (account)")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Cart items retrieved successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid request parameters"),
		@ApiResponse(responseCode = "403", description = "Unauthorized access"),
	})
	@SecurityRequirement(name = "bearerAuth")
	@GetMapping
	public ResponseEntity<?> getCartItems(
		@Valid @RequestParam(required = false) Boolean paginate,
		@Valid @RequestParam(required = false) Integer page,
		@Valid @RequestParam(required = false) Integer pageSize
	) {
		if (paginate != null && paginate) {
			Page<CartItemDto> cartItems = cartService.getCartItems(page, pageSize);
			return ResponseEntity.status(HttpStatus.OK).body(cartItems);
		}
		List<CartItemDto> cartItems = cartService.getCartItems();
		return ResponseEntity.ok(cartItems);
	}

	@Operation(summary = "Add a new item to the user cart")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Item added to cart successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid request parameters"),
		@ApiResponse(responseCode = "403", description = "Unauthorized access"),
	})
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping
	public ResponseEntity<?> addCartItem(
		@Valid @RequestBody CartItemPostDto cartItemPostDto
	) {
		CartItemDto cartItem = cartService.addCartItem(cartItemPostDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
	}

	@Operation(summary = "Edit a cart item of a user")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Cart item edited successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid request parameters"),
		@ApiResponse(responseCode = "403", description = "Unauthorized access"),
		@ApiResponse(responseCode = "404", description = "Cart item not found"),
	})
	@SecurityRequirement(name = "bearerAuth")
	@PatchMapping("/{cartItemId}")
	public ResponseEntity<?> editCartItem(
		@PathVariable Long cartItemId,
		@Valid @RequestBody CartItemPatchDto cartItemPatchDto
	) {
		CartItemDto cartItem = cartService.editCartItem(cartItemId, cartItemPatchDto);
		return ResponseEntity.ok(cartItem);
	}

	@Operation(summary = "Delete a cart item of a user")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Cart item deleted successfully"),
		@ApiResponse(responseCode = "403", description = "Unauthorized access"),
		@ApiResponse(responseCode = "404", description = "Cart item not found"),
	})
	@DeleteMapping("/{cartItemId}")
	public ResponseEntity<?> deleteCartItem(
		@PathVariable Long cartItemId
	) {
		boolean deleted = cartService.deleteCartItem(cartItemId);
		return ResponseEntity.status(deleted ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND).build();
	}

	@Operation(summary = "Delete all cart items of a user")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Cart items deleted successfully"),
		@ApiResponse(responseCode = "403", description = "Unauthorized access"),
		@ApiResponse(responseCode = "404", description = "Cart items not found"),
	})
	@DeleteMapping("/all")
	public ResponseEntity<?> deleteCartItems() {
		boolean deleted = cartService.deleteCartItems();
		return ResponseEntity.status(deleted ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND).build();
	}
}
