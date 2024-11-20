package dev.realtards.kuenyawz.dtos.product;

import dev.realtards.kuenyawz.utils.stringtrimmer.CleanString;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Product patch request body")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPatchDto {

	@Schema(description = "Product name", example = "Muffin", minLength = 2, maxLength = 128)
	@Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
	@CleanString
	private String name;

	@Schema(description = "Product tagline", example = "The best muffin in Jakarta", minLength = 2, maxLength = 128)
	@Size(min = 2, max = 50, message = "Tagline must be between 2 and 50 characters")
	@CleanString
	private String tagline;

	@Schema(description = "Product description", example = "A moist and delicious chocolate muffin", minLength = 2, maxLength = 1024)
	@Size(min = 2, max = 255, message = "Description must be between 2 and 255 characters")
	@CleanString
	private String description;

	@Schema(description = "Product category", example = "CAKE")
	@Pattern(regexp = "CAKE|PIE|PASTRY|PASTA|OTHER", message = "Invalid category, should be any of [CAKE, PIE, PASTRY, PASTA, OTHER]")
	@CleanString
	private String category;

	@Schema(description = "Product availability", example = "true")
	private boolean available;
}
