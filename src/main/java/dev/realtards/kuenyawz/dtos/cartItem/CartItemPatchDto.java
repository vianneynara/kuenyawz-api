package dev.realtards.kuenyawz.dtos.cartItem;

import dev.realtards.kuenyawz.dtos.product.VariantPostDto;
import dev.realtards.kuenyawz.utils.stringtrimmer.CleanString;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Edit Item on Cart request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemPatchDto {

    @Schema(description = "Variant Id", example = "12345")
    @NotBlank(message = "Variant Id is required")
    private Long variantId;

    @Schema(description = "Variant quantity", example = "10")
    @NotBlank(message = "Quantity is required")
    private int quantity;

    @Schema(description = "Note for variant")
    @CleanString
    private String note;
}
