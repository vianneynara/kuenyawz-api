package dev.kons.kuenyawz.dtos.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Product patch availability request body")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPatchAvailabilityDto {

	@Schema(description = "Product availability", example = "true")
	private boolean available;
}
