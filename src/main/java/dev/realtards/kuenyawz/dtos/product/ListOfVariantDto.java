package dev.realtards.kuenyawz.dtos.product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response object for list of products")
public record ListOfVariantDto(

	@Schema(description = "List of variants")
	List<VariantDto> variants
) {
}
