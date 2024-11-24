package dev.kons.kuenyawz.dtos.purchase;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

	@Schema(description = "Latitude for the purchase place", example = "-6.1745517")
	private Double latitude;

	@Schema(description = "Longitude for the purchase place", example = "106.8205983")
	private Double longitude;

	@Schema(description = "Purchase items of the purchase", example = "[{...}, {...}]")
	@NotNull(message = "Purchase items must not be filled")
    @Size(min = 1, message = "At least one item is required")
	private List<PurchaseItemPostDto> purchaseItems;
}
