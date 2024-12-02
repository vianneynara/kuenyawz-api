package dev.kons.kuenyawz.dtos.purchase;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(name = "Purchase Post", description = "Purchase creation request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchasePostDto {

	@Schema(description = "Full address of the purchase", example = "Merdeka Square, Jakarta, Jalan Lapangan Monas, Gambir, Central Jakarta City, Jakarta 10110")
	@NotBlank(message = "Full address must not be blank")
	private String fullAddress;

	@Schema(description = "Latitude for the purchase place", example = "-6.1751833254326085")
	@NotNull(message = "Latitude is required")
	private Double latitude;

	@Schema(description = "Longitude for the purchase place", example = "106.82710076786921")
	@NotNull(message = "Longitude is required")
	private Double longitude;

	@Schema(description = "Date of the event (ISO-8601)", example = "2024-12-06")
	@NotBlank(message = "Event date is required")
	@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Invalid date format")
	private String eventDate;

	@Schema(description = "Payment type of the purchase", example = "FULL_PAYMENT")
	@NotBlank(message = "Payment type is required")
	@Pattern(regexp = "^(DOWN_PAYMENT|FULL_PAYMENT)$", message = "Invalid payment type")
	private String paymentType;

	@Schema(description = "Delivery option of the purchase", example = "DELIVERY")
	@NotBlank(message = "Delivery option is required")
	@Pattern(regexp = "^(DELIVERY|PICK_UP)$", message = "Invalid delivery option")
	private String deliveryOption;

	@Schema(description = "Delivery fee of the purchase", example = "9000")
	private Double deliveryFee;

	@Schema(description = "Purchase items of the purchase")
	@NotNull(message = "Purchase items must not be filled")
    @Size(min = 1, message = "At least one item is required")
	private List<PurchaseItemPostDto> purchaseItems;
}
