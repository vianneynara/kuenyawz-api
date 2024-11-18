package dev.realtards.kuenyawz.dtos.product;

import dev.realtards.kuenyawz.utils.stringtrimmer.CleanString;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Product creation request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPostDto {

	@Schema(description = "Product name", example = "Muffin", minLength = 2, maxLength = 128)
	@NotBlank(message = "Name is required")
	@Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
	@CleanString
	private String name;

	@Schema(description = "Product tagline", example = "The best muffin in Jakarta", minLength = 2, maxLength = 128)
	@NotBlank(message = "Tagline is required")
	@Size(min = 2, max = 50, message = "Tagline must be between 2 and 50 characters")
	@CleanString
	private String tagline;

	@Schema(description = "Product description", example = "A moist and delicious chocolate muffin", minLength = 2, maxLength = 1024)
	@NotBlank(message = "Description is required")
	@Size(min = 2, max = 255, message = "Description must be between 2 and 255 characters")
	@CleanString
	private String description;

	@Schema(description = "Product category", example = "cake")
	@NotBlank(message = "Category is required")
	@Pattern(regexp = "cake|pie|pastry|pasta|other", message = "Invalid category")
	@CleanString
	private String category;

	@Schema(description = "Product availability", example = "true", defaultValue = "false")
	private boolean available;

	@Schema(description = "Product variants")
	@NotNull(message = "Variants are required")
    @Size(min = 1, message = "At least one variant is required")
	@Valid
	private List<VariantPostDto> variants;
}
