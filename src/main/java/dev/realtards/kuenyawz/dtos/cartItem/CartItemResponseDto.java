package dev.realtards.kuenyawz.dtos.cartItem;

import dev.realtards.kuenyawz.dtos.product.ProductDto;
import dev.realtards.kuenyawz.utils.stringtrimmer.CleanString;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Response request of Cart Item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponseDto {

    @Schema(description = "Product in Cart Item")
    private ProductDto product;

    @Schema(description = "Selected Variant in the Product", example = "12345")
    private Long selectedVariantId;

    @Schema(description = "Variant quantity", example = "10")
    private int quantity;

    @Schema(description = "Note for variant")
    private String note;
}
