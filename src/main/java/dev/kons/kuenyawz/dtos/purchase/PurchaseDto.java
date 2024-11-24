package dev.kons.kuenyawz.dtos.purchase;

import dev.kons.kuenyawz.entities.Purchase;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "Purchase", description = "Purchase response")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseDto {

	@Schema(description = "Purchase id", example = "12345678")
	private Long purchaseId;

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

	@Schema(description = "Fee of the purchase", example = "10000.00")
	private BigDecimal fee;

	@Schema(description = "Items of the purchase")
	private List<PurchaseItemDto> purchaseItems;

	@Schema(description = "Transactions of the purchase")
	private List<TransactionDto> transactions;

	@Schema(description = "Time of creation")
	private LocalDateTime createdAt;
}
