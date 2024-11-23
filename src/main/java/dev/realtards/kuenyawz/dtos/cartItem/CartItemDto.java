package dev.realtards.kuenyawz.dtos.cartItem;

import dev.realtards.kuenyawz.dtos.product.ProductDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Response request of Cart Item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDto {

    @Schema(description = "Cart Item Id")
    private Long cartItemId;

    @Schema(description = "Product in Cart Item")
    private ProductDto product;

    @Schema(description = "Selected Variant in the Product", example = "12345")
    private Long selectedVariantId;

    @Schema(description = "Variant quantity", example = "10")
    private Integer quantity;

    @Schema(description = "Note for variant")
    private String note;
}
