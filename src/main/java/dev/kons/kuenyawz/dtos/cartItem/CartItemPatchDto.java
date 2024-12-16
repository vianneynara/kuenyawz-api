package dev.kons.kuenyawz.dtos.cartItem;

import dev.kons.kuenyawz.utils.stringtrimmer.CleanString;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "New variant Id, used to switch variants", example = "12345")
    private Long variantId;

    @Schema(description = "Variant quantity", example = "10")
    private Integer quantity;

    @Schema(description = "Note for variant")
    @CleanString
    private String note;
}
