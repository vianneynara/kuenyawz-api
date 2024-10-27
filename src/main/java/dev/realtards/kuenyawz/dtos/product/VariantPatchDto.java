package dev.realtards.kuenyawz.dtos.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "Variant patch request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantPatchDto {

	@Schema(description = "Variant price", example = "10000.0")
	@DecimalMin(value = "1.0", message = "Price must be at least 0.0")
	private BigDecimal price;

	@Schema(description = "Variant type", example = "chocolate")
	@Size(min = 2, max = 50, message = "Type must be between 2 and 50 characters")
	private String type;
}
