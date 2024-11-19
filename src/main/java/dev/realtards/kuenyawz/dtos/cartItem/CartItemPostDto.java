package dev.realtards.kuenyawz.dtos.cartItem;

import dev.realtards.kuenyawz.dtos.product.VariantPostDto;
import dev.realtards.kuenyawz.utils.stringtrimmer.CleanString;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Add Item into Cart request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemPostDto {

    @Schema(description = "Product(Cart Item) name", example = "Muffin", minLength = 2, maxLength = 128)
    @NotBlank(message = "Name is required")
    @CleanString
    private String name;

    @Schema(description = "Product(Cart Item) quantity", example = "10")
    @NotBlank(message = "Quantity is required")
    private int quantity;

    @Schema(description = "Product(Cart Item) variant")
    @NotBlank(message = "Variant is required")
    @CleanString
    private VariantPostDto variant;
}
