package dev.realtards.kuenyawz.controllers;

import dev.realtards.kuenyawz.dtos.image.BatchImageUploadDto;
import dev.realtards.kuenyawz.dtos.image.ImageResourceDTO;
import dev.realtards.kuenyawz.dtos.image.ImageUploadDto;
import dev.realtards.kuenyawz.dtos.image.ListOfImageResourceDto;
import dev.realtards.kuenyawz.services.logic.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Tag(name = "Product Images Relay Endpoints")
@Controller
@RequestMapping("/images")
@RequiredArgsConstructor
@Validated
public class ProductImageController extends BaseController {

	private final ImageStorageService imageStorageService;

	@Operation(summary = "Upload an image for a product using form-data")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Image uploaded successfully",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = ImageResourceDTO.class))),
		@ApiResponse(responseCode = "400", description = "Invalid image file"),
		@ApiResponse(responseCode = "404", description = "Product not found"),
	})
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping("{productId}")
	public ResponseEntity<Object> uploadImage(
		@PathVariable Long productId,
		@Valid @ModelAttribute ImageUploadDto imageUploadDto
	) {
		ImageResourceDTO imageResourceDTO = imageStorageService.store(productId, imageUploadDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(imageResourceDTO);
	}

	@Operation(summary = "Batch upload multiple images using form-data")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Images uploaded successfully",
		content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
		@ApiResponse(responseCode = "400", description = "Invalid image file"),
		@ApiResponse(responseCode = "404", description = "Product not found"),
	})
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping("{productId}/batch")
	public ResponseEntity<Object> batchUploadImage(
		@PathVariable Long productId,
		@Valid @ModelAttribute BatchImageUploadDto batchImageUploadDto
	) {
		List<ImageResourceDTO> listOfImageResourceDto = imageStorageService.batchStore(productId, batchImageUploadDto);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new ListOfImageResourceDto(listOfImageResourceDto));
	}

	@Operation(summary = "Serve a specific image for a product")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Image served successfully",
			content = @Content(mediaType = "image/*")),
		@ApiResponse(responseCode = "404", description = "Product or image not found"),
	})
	@GetMapping("{productId}/{resourceUri}")
	public ResponseEntity<Object> serveImage(
		@PathVariable Long productId,
		@PathVariable String resourceUri)
	{
		try {
			Resource resource = imageStorageService.loadAsResource(productId, resourceUri);
			return ResponseEntity.status(HttpStatus.OK)
				.header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(resource.getFile().toPath()))
                .header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
				.body(resource);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("Erm, something went wrong while trying to serve the image.");
		}
	}

	@Operation(summary = "Delete a specific image for a product")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Image deleted successfully"),
		@ApiResponse(responseCode = "404", description = "Product or image not found"),
	})
	@SecurityRequirement(name = "bearerAuth")
	@DeleteMapping("{productId}/{resourceUri}")
	public ResponseEntity<Object> deleteImage(
		@PathVariable Long productId,
		@PathVariable String resourceUri
	) {
		imageStorageService.delete(productId, resourceUri);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "Delete all images of a product")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Images deleted successfully"),
		@ApiResponse(responseCode = "404", description = "Product not found"),
	})
	@SecurityRequirement(name = "bearerAuth")
	@DeleteMapping("{productId}")
	public ResponseEntity<Object> deleteAllImagesOfProduct(
		@PathVariable Long productId
	) {
		imageStorageService.deleteAllOfProductId(productId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "Delete all images")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Images deleted successfully"),
	})
	@SecurityRequirement(name = "bearerAuth")
	@DeleteMapping("all")
	public ResponseEntity<Object> deleteAllImages() {
		imageStorageService.deleteAll();
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}