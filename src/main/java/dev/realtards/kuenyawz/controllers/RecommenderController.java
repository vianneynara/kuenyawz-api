package dev.realtards.kuenyawz.controllers;

import dev.realtards.kuenyawz.dtos.product.ListOfProductDto;
import dev.realtards.kuenyawz.dtos.product.ProductDto;
import dev.realtards.kuenyawz.services.logic.RecommenderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Recommender Controller", description = "Product recommender endpoints")
@RequestMapping("/recommender")
@RestController
@RequiredArgsConstructor
public class RecommenderController {

	private final RecommenderService recommenderService;

	@Operation(summary = "Get recommended products of a product by transaction history")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Successfully retrieved recommended products"),
		@ApiResponse(responseCode = "403", description = "Not enough products to recommend"),
		@ApiResponse(responseCode = "404", description = "Product not found"),

	})
	@GetMapping("/{productId}")
	public ResponseEntity<Object> getRecommendsOfProduct(
		@PathVariable Long productId,
		@RequestParam(required = false) Boolean addRandom
	) {
		List<ProductDto> productDtos = recommenderService.getRecommendsOfProduct(productId, addRandom);
		return ResponseEntity.status(HttpStatus.OK).body(new ListOfProductDto(productDtos));
	}
}
