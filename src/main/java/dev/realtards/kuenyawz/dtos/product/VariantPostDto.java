package dev.realtards.kuenyawz.dtos.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

	@Schema(description = "Variant price", example = "10000.0")
	@NotBlank(message = "Price is required")
	@DecimalMin(value = "1.0", message = "Price must be at least 0.0")
	private BigDecimal price;

	@Schema(description = "Variant type", example = "chocolate")
	@NotBlank(message = "Type is required")
	@Size(min = 2, max = 50, message = "Type must be between 2 and 50 characters")
	private String type;
}
