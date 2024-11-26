package dev.kons.kuenyawz.dtos.purchase;

import dev.kons.kuenyawz.dtos.product.VariantDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(name = "Item of a purchase", description = "Purchase item of specific variant for a purchase")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseItemDto {

	@Schema(description = "Purchase item id", example = "12345678")
	private Long purchaseItemId;

	@Schema(description = "Note of the purchase item", example = "Make it slightly spicy")
	private String note;

	@Schema(description = "Quantity of the purchase item", example = "1")
	private Integer quantity;

	@Schema(description = "Bought price of the purchase item at the time of creation", example = "10000.00")
	private BigDecimal boughtPrice;

	@Schema(description = "Variant id of the purchase item")
	private VariantDto variantDto;
}
