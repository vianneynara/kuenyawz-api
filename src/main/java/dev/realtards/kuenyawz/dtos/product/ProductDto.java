package dev.realtards.kuenyawz.dtos.product;

import dev.realtards.kuenyawz.entities.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Product public data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

	@Schema(description = "Product ID", example = "1234")
	private Long productId;

	@Schema(description = "Product name", example = "Muffin", minLength = 2, maxLength = 128)
	private String name;

	@Schema(description = "Product tagline", example = "The best muffin in Jakarta", minLength = 2, maxLength = 128)
	private String tagline;

	@Schema(description = "Product description", example = "A moist and delicious chocolate muffin", minLength = 2, maxLength = 1024)
	private String description;

	@Schema(description = "Product category", example = "cake")
	private Product.Category category;

	@Schema(description = "Minimum quantity of product that can be ordered", example = "1")
	private Integer minQuantity;

	@Schema(description = "Maximum quantity of product that can be ordered", example = "10")
	private Integer maxQuantity;

	@Schema(description = "Product variants")
	private List<VariantDto> variants;
}
