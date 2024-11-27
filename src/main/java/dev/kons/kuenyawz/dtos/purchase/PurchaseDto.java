package dev.kons.kuenyawz.dtos.purchase;

import dev.kons.kuenyawz.constants.PaymentType;
import dev.kons.kuenyawz.entities.Coordinate;
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

	@Schema(description = "Full address of the purchase", example = "Merdeka Square, Jakarta, Jalan Lapangan Monas, Gambir, Central Jakarta City, Jakarta 10110")
	private String fullAddress;

	@Schema(description = "Coordinate of the event")
	private Coordinate coordinate;

	@Schema(description = "Event date", example = "2024-04-21")
	private LocalDate eventDate;

	@Schema(description = "Payment type of the purchase", example = "DOWN_PAYMENT")
	private PaymentType paymentType;

	@Schema(description = "Delivery option of the purchase", example = "DELIVERY")
	private Purchase.DeliveryOption deliveryOption;

	@Schema(description = "Delivery fee of the purchase", example = "10000.00")
	private BigDecimal deliveryFee;

	@Schema(description = "Purchase status", example = "PENDING")
	private Purchase.PurchaseStatus status;

	@Schema(description = "Items of the purchase")
	private List<PurchaseItemDto> purchaseItems;

	@Schema(description = "Transactions of the purchase")
	private List<TransactionDto> transactions;

	@Schema(description = "Time of creation")
	private LocalDateTime createdAt;
}
