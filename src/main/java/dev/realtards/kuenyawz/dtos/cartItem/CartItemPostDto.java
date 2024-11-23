package dev.realtards.kuenyawz.dtos.cartItem;

import dev.realtards.kuenyawz.utils.stringtrimmer.CleanString;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
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

    @Schema(description = "Variant Id", example = "12345")
    @NotNull(message = "Variant Id is required")
    private Long variantId;

    @Schema(description = "Variant quantity", example = "10", defaultValue = "1")
    @Min(value = 1, message = "Minimum quantity must be at least 1")
    @Max(value = 250, message = "Maximum quantity must be at most 250")
    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @Schema(description = "Note for variant")
    @Size(min = 1, max = 128, message = "Description must be between 1 and 128 characters")
    @CleanString
    private String note;
}
