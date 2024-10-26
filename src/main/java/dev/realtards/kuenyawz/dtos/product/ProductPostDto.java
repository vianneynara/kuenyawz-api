package dev.realtards.kuenyawz.dtos.product;

import io.swagger.v3.oas.annotations.media.Schema;
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
	private String name;

	@Schema(description = "Product tagline", example = "The best muffin in Jakarta", minLength = 2, maxLength = 128)
	@NotBlank(message = "Tagline is required")
	@Size(min = 2, max = 50, message = "Tagline must be between 2 and 50 characters")
	private String tagline;

	@Schema(description = "Product description", example = "A moist and delicious chocolate muffin", minLength = 2, maxLength = 1024)
	@NotBlank(message = "Description is required")
	@Size(min = 2, max = 255, message = "Description must be between 2 and 255 characters")
	private String description;

	@Schema(description = "Product category", example = "cake")
	@NotNull(message = "Category is required")
	@Pattern(regexp = "cake|bread|pastry|pasta|other", message = "Invalid category")
	private String category;

	@Schema(description = "Minimum quantity of product that can be ordered", example = "1")
	@NotNull(message = "Minimum quantity is required")
	@Min(value = 1, message = "Minimum quantity must be at least 1")
	private Integer minQuantity;

	@Schema(description = "Maximum quantity of product that can be ordered", example = "10")
	@NotNull(message = "Maximum quantity is required")
	@Min(value = 1, message = "Maximum quantity must be at least 1")
	@Max(value = 200, message = "Maximum quantity must be at most 200")
	private Integer maxQuantity;

	@Schema(description = "Product variants")
	@NotNull(message = "Variants are required")
    @Size(min = 1, message = "At least one variant is required")
	private List<VariantPostDto> variants;

	@AssertTrue(message = "Minimum quantity and maximum quantity must be consistent")
	public boolean isQuantityConsistent() {
		return minQuantity <= maxQuantity;
	}
}
