package dev.realtards.kuenyawz.dtos.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "Variant public data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantDto {

	@Schema(description = "Variant identifier", example = "1221991247904768")
	private Long variantId;

	@Schema(description = "Variant price", example = "10000.0")
	private BigDecimal price;

	@Schema(description = "Variant type", example = "chocolate")
	private String type;
}
