package dev.kons.kuenyawz.dtos.purchase;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(name = "Purchase item of specific variant", description = "Purchase item of specific variant for a purchase")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseItemPostDto {

	@Schema(description = "Note of the purchase item", example = "Make it slightly spicy")
	private String note;

	@Schema(description = "Quantity of the purchase item", example = "1")
	@Min(1)
	private Integer quantity;

	@Schema(description = "Bought price of the purchase item at the time of creation", example = "10000.00")
	@NotNull
	@DecimalMin(value = "1.0")
	private BigDecimal boughtPrice;

	@Schema(description = "Variant id of the purchase item", example = "12345678")
	@NotNull
	private Long variantId;
}
