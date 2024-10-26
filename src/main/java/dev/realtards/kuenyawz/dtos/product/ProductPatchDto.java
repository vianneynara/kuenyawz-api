package dev.realtards.kuenyawz.dtos.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public class ProductPatchDto {

	@Schema(description = "Product name", example = "Muffin", minLength = 2, maxLength = 128)
	@Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
	private String name;

	@Schema(description = "Product tagline", example = "The best muffin in Jakarta", minLength = 2, maxLength = 128)
	@Size(min = 2, max = 50, message = "Tagline must be between 2 and 50 characters")
	private String tagline;

	@Schema(description = "Product description", example = "A moist and delicious chocolate muffin", minLength = 2, maxLength = 1024)
	@Size(min = 2, max = 255, message = "Description must be between 2 and 255 characters")
	private String description;

	@Schema(description = "Product category", example = "cake")
	@Pattern(regexp = "cake|bread|pastry|pasta|other", message = "Invalid category")
	private String category;

	@Schema(description = "Minimum quantity of product that can be ordered", example = "1")
	@Min(value = 1, message = "Minimum quantity must be at least 1")
	private Integer minQuantity;

	@Schema(description = "Maximum quantity of product that can be ordered", example = "10")
	@Min(value = 1, message = "Maximum quantity must be at least 1")
	@Max(value = 200, message = "Maximum quantity must be at most 200")
	private Integer maxQuantity;

	@AssertTrue(message = "Minimum quantity and maximum quantity must be consistent")
	public boolean isQuantityConsistent() {
		return minQuantity <= maxQuantity;
	}
}
