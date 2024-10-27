package dev.realtards.kuenyawz.controllers;

import dev.realtards.kuenyawz.dtos.product.*;
import dev.realtards.kuenyawz.services.ProductService;
import dev.realtards.kuenyawz.services.VariantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Product Routes", description = "Endpoints for managing products and its variants")
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Validated
public class ProductController extends BaseController {

	private final ProductService productService;
	private final VariantService variantService;

	// PRODUCT ENDPOINTS

	@Operation(summary = "(Master) Get all products", description = "Retrieves a list of all products with their variants")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Successfully retrieved all products",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = ListOfProductDto.class)
			)
		),
		@ApiResponse(responseCode = "403", description = "Forbidden")
	})
	@GetMapping("/all")
	public ResponseEntity<Object> getAllProducts() {
		List<ProductDto> productDtos = productService.getAllProducts();
		return ResponseEntity.status(HttpStatus.OK).body(new ListOfProductDto(productDtos));
	}

	@Operation(summary = "Creates a new product with variant")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Product created successfully",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = ProductDto.class)
			)
		),
		@ApiResponse(responseCode = "400", description = "Invalid request body")
	})
	@PostMapping
	public ResponseEntity<Object> createProduct(
		@Valid @RequestBody ProductPostDto productPostDto
	) {
		ProductDto productDto = productService.createProduct(productPostDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(productDto);
	}

	@Operation(summary = "Get a product by ID")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Product retrieved successfully",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = ProductDto.class)
			)
		),
		@ApiResponse(responseCode = "404", description = "Product not found")
	})
	@GetMapping("{productId}")
	public ResponseEntity<Object> getProduct(
		@PathVariable Long productId
	) {
		ProductDto productDto = productService.getProduct(productId);
		return ResponseEntity.status(HttpStatus.OK).body(productDto);
	}

	@Operation(summary = "Search products by keyword (simple)")
	@ApiResponse(responseCode = "200", description = "Products retrieved successfully",
		content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
			schema = @Schema(implementation = ListOfProductDto.class)
		)
	)
	@GetMapping("/search/{keyword}")
	public ResponseEntity<Object> searchProducts(
		@PathVariable String keyword
	) {
		List<ProductDto> productDtos = productService.getAllProductByKeyword(keyword);
		return ResponseEntity.status(HttpStatus.OK).body(new ListOfProductDto(productDtos));
	}

	@Operation(summary = "Get products by category")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Products retrieved successfully",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = ListOfProductDto.class)
			)
		),
		@ApiResponse(responseCode = "400", description = "Invalid category")
	})
	@GetMapping("/category/{category}")
	public ResponseEntity<Object> getProductsByCategory(
		@PathVariable String category
	) {
		List<ProductDto> productDtos = productService.getProductsByCategory(category);
		return ResponseEntity.status(HttpStatus.OK).body(new ListOfProductDto(productDtos));
	}

	@Operation(summary = "Deletes a product by ID")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Product deleted successfully"),
		@ApiResponse(responseCode = "404", description = "Product not found")
	})
	@DeleteMapping("{productId}")
	public ResponseEntity<Object> deleteProduct(
		@PathVariable Long productId
	) {
		productService.deleteProduct(productId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "Patch a product by ID")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Product patched successfully",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = ProductDto.class)
			)
		),
		@ApiResponse(responseCode = "404", description = "Product not found"),
		@ApiResponse(responseCode = "400", description = "Invalid request body")
	})
	@PatchMapping("{productId}")
	public ResponseEntity<Object> patchProduct(
		@PathVariable Long productId,
		@Valid @RequestBody ProductPatchDto productPatchDto
	) {
		ProductDto productDto = productService.patchProduct(productId, productPatchDto);
		return ResponseEntity.status(HttpStatus.OK).body(productDto);
	}

	// VARIANT ENDPOINTS

	@Operation(summary = "(Master) Get all variants")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Successfully retrieved all variants",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = ListOfVariantDto.class)
			)
		),
		@ApiResponse(responseCode = "403", description = "Forbidden")
	})
	@GetMapping("/variants/all")
	public ResponseEntity<Object> getAllVariants() {
		List<VariantDto> variantDtos = variantService.getAllVariants();
		return ResponseEntity.status(HttpStatus.OK).body(new ListOfVariantDto(variantDtos));
	}

	@Operation(summary = "Create a new variant for a product")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Variant created successfully",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = VariantDto.class)
			)
		),
		@ApiResponse(responseCode = "404", description = "Product not found"),
		@ApiResponse(responseCode = "400", description = "Invalid request body")
	})
	@PostMapping("{productId}/variant")
	public ResponseEntity<Object> createVariant(
		@PathVariable Long productId,
		@Valid @RequestBody VariantPostDto variantPostDto
	) {
		VariantDto variantDto = variantService.createVariant(productId, variantPostDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(variantDto);
	}

	@Operation(summary = "Create multiple variants for a product")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Variants created successfully",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = ListOfVariantDto.class)
			)
		),
		@ApiResponse(responseCode = "404", description = "Product not found"),
		@ApiResponse(responseCode = "400", description = "Invalid request body")
	})
	@PostMapping("{productId}/variants")
	public ResponseEntity<Object> createVariants(
		@PathVariable Long productId,
		@Valid @RequestBody List<VariantPostDto> variantPostDtos
	) {
		List<VariantDto> variantDtos = variantService.createVariants(
			productId,
			variantPostDtos.toArray(new VariantPostDto[0])
		);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ListOfVariantDto(variantDtos));
	}

	@Operation(summary = "Get a variants of product ID")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Variants retrieved successfully",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = VariantDto.class)
			)
		),
		@ApiResponse(responseCode = "404", description = "Product not found")
	})
	@GetMapping("{productId}/variants")
	public ResponseEntity<Object> getVariantsOfProductId(
		@PathVariable Long productId
	) {
		List<VariantDto> variantDtos = variantService.getVariantsOfProductId(productId);
		return ResponseEntity.status(HttpStatus.OK).body(new ListOfVariantDto(variantDtos));
	}

	@Operation(summary = "Get a variant by ID")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Variant retrieved successfully",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = VariantDto.class)
			)
		),
		@ApiResponse(responseCode = "404", description = "Variant not found")
	})
	@PatchMapping("{productId}/variant/{variantId}")
	public ResponseEntity<Object> patchVariant(
		@PathVariable Long productId,
		@PathVariable Long variantId,
		@Valid @RequestBody VariantPatchDto variantPatchDto
	) {
		VariantDto variantDto = variantService.patchVariant(productId, variantId, variantPatchDto);
		return ResponseEntity.status(HttpStatus.OK).body(variantDto);
	}
}
