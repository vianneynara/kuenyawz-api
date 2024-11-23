package dev.realtards.kuenyawz.dtos.orderItems;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Response request of Order Items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {

    @Schema(description = "Order Item Id")
    private Long orderItemId;

    @Schema(description = "Note for Order Item")
    private String note;

    @Schema(description = "Order Item quantity", example = "10")
    private Integer quantity;

    @Schema(description = "Bought price of the order item")
    private Double boughtPrice;

    @Schema(description = "Order Id of Order")
    private Long OrderId;

    @Schema(description = "Reference to the variant")
    private Long variantId;
}
