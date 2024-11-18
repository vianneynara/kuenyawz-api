package dev.realtards.kuenyawz.dtos.product;

import dev.realtards.kuenyawz.utils.stringtrimmer.CleanString;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "Variant creation request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantPostDto {

	@Schema(description = "Variant type", example = "chocolate")
	@NotBlank(message = "Type is required")
	@Size(min = 2, max = 50, message = "Type must be between 2 and 50 characters")
	@CleanString
	private String type;

	@Schema(description = "Variant price", example = "10000.0")
	@NotNull(message = "Price is required")
	@DecimalMin(value = "1.0", message = "Price must be at least 1.0")
	private BigDecimal price;

	@Schema(description = "Minimum quantity of variant that can be ordered", example = "1")
	@Min(value = 1, message = "Minimum quantity must be at least 1")
	@Max(value = 250, message = "Maximum quantity must be at most 250")
	private Integer minQuantity = 1;

	@Schema(description = "Maximum quantity of variant that can be ordered", example = "10")
	@Min(value = 1, message = "Maximum quantity must be at least 1")
	@Max(value = 250, message = "Maximum quantity must be at most 250")
	private Integer maxQuantity = 250;

	@Schema(hidden = true)
	@AssertTrue(message = "Minimum quantity and maximum quantity must be consistent")
	public boolean isQuantityConsistent() {
		if (!(minQuantity == null) && !(maxQuantity == null)) {
			return minQuantity <= maxQuantity;
		} else {
			return true;
		}
	}
}
