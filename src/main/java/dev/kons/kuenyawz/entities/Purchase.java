package dev.kons.kuenyawz.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import dev.kons.kuenyawz.constants.PaymentType;
import dev.kons.kuenyawz.exceptions.IllegalOperationException;
import dev.kons.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(indexes = {
	@Index(name = "idx_purchase_status", columnList = "status"),
	@Index(name = "idx_purchase_purchasedate", columnList = "purchase_date")
})
public class Purchase extends Auditables {
	@Id
	@SnowFlakeIdValue(name = "purchase_id")
	@Column(name = "purchase_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
	private Long purchaseId;

	@Column
	private String fullAddress;

	@Column
	private LocalDate purchaseDate;

	@Embedded
	private Coordinate coordinate;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PurchaseStatus status;

	@Column
	private BigDecimal fee;

	@Column
	private PaymentType paymentType;

	@Version
	private Long version;

	@OneToMany(mappedBy = "purchase", fetch = FetchType.LAZY)
	private List<Transaction> transactions;

	@OneToMany(mappedBy = "purchase", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PurchaseItem> purchaseItems;

	// Helper methods to see payment status:

	public BigDecimal getTotalPrice() {
		return purchaseItems.stream()
			.map(PurchaseItem::getBoughtPrice)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public BigDecimal getTotalPriceWithFee() {
		return getTotalPrice().add(fee);
	}

	/**
	 * Gets a down payment transaction.
	 *
	 * @return {@link Optional} of {@link Transaction}
	 */
	public Optional<Transaction> getDownPayment() {
		return transactions.stream()
			.filter(t -> t.getPaymentType() == PaymentType.DOWN_PAYMENT)
			.findFirst();
	}

	/**
	 * Gets a fulfillment payment transaction.
	 *
	 * @return {@link Optional} of {@link Transaction}
	 */
	public Optional<Transaction> getFulfillmentPayment() {
		return transactions.stream()
			.filter(t -> t.getPaymentType() == PaymentType.FULL_PAYMENT)
			.findFirst();
	}

	/**
	 * Checks if the purchase is fully paid.
	 */
	public boolean isFullyPaid() {
		if (paymentType == PaymentType.FULL_PAYMENT) {
			return transactions.stream()
				.anyMatch(t -> t.getPaymentType() == PaymentType.FULL_PAYMENT
					&& t.getStatus() == Transaction.TransactionStatus.PAID);
		} else {
			return getDownPayment()
				.filter(dp -> dp.getStatus() == Transaction.TransactionStatus.PAID)
				.isPresent()
				&& getFulfillmentPayment()
				.filter(fp -> fp.getStatus() == Transaction.TransactionStatus.PAID)
				.isPresent();
		}
	}

	/**
	 * Checks if the purchase requires cancellation.
	 */
	public boolean requiresCancellation() {
		return paymentType == PaymentType.DOWN_PAYMENT && getDownPayment()
			.filter(dp -> dp.getStatus() == Transaction.TransactionStatus.EXPIRED)
			.isPresent();
	}

	public boolean isConfirmed() {
		return (this.status.ordinal() >= PurchaseStatus.CONFIRMED.ordinal());
	}

	public boolean isFinished() {
		return (this.status == PurchaseStatus.DELIVERED || this.status == PurchaseStatus.CANCELLED);
	}

	/**
	 * Ongoing status.
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@Getter
	public enum PurchaseStatus {
		@JsonProperty("PENDING")
		PENDING("Waiting for system"),

		@JsonProperty("WAITING_DOWN_PAYMENT")
		WAITING_DOWN_PAYMENT("Waiting for down payment"),

		@JsonProperty("CONFIRMING")
		CONFIRMING("Waiting for confirmation from seller"),

		@JsonProperty("CONFIRMED")
		CONFIRMED("Confirmed by seller"),

		@JsonProperty("WAITING_SETTLEMENT")
		WAITING_SETTLEMENT("Waiting for settlement"),

		@JsonProperty("PROCESSING")
		PROCESSING("Being processed"),

		@JsonProperty("DELIVERED")
		DELIVERED("Purchase delivered"),

		@JsonProperty("CANCELLED")
		CANCELLED("Purchase cancelled");

		private final String description;

		PurchaseStatus(String description) {
			this.description = description;
		}

		@JsonValue
		public String getStatus() {
			return name();
		}

		@JsonCreator
		public static PurchaseStatus fromString(String value) {
			for (PurchaseStatus status : PurchaseStatus.values()) {
				if (status.name().equalsIgnoreCase(value)) {
					return status;
				}
			}
			throw new IllegalArgumentException("Invalid status: " + value);
		}

		/**
		 * Returns the next status of the current ordinal.
		 *
		 * @return {@link PurchaseStatus}
		 */
		public PurchaseStatus next() {
			PurchaseStatus[] statuses = values();
			int currentIndex = ordinal();

			if (currentIndex >= statuses.length - 1 || this == CANCELLED) {
				throw new IllegalOperationException("Cannot progress beyond finished status: " + this);
			}

			return statuses[currentIndex + 1];
		}
	}
}
