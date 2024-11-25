package dev.kons.kuenyawz.dtos.purchase;

import dev.kons.kuenyawz.entities.Purchase;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Schema(description = "Purchase patch request/DTO")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchasePatchDto {

	@Schema(description = "Purchase date", example = "2024-04-21")
	private LocalDate purchaseDate;

	@Schema(description = "Full address of the purchase", example = "Merdeka Square, Jakarta, Jalan Lapangan Monas, Gambir, Central Jakarta City, Jakarta 10110")
	private String fullAddress;

	@Schema(description = "Longitude for the purchase place", example = "-6.1745517")
	private Double latitude;

	@Schema(description = "Longitude for the purchase place", example = "106.8205983")
	private Double longitude;

	@Schema(description = "Purchase status", example = "PENDING")
	private Purchase.PurchaseStatus status;
}
