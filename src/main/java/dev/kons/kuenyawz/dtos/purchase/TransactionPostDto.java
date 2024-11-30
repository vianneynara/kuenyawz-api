package dev.kons.kuenyawz.dtos.purchase;

import dev.kons.kuenyawz.constants.PaymentType;
import dev.kons.kuenyawz.entities.Transaction;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionPostDto {

	@Column
	private Long transactionId;

	@Column
	private String invoiceId;

	@Column
	private BigDecimal amount;

	@Column
	private String invoiceLink;

	@Column(nullable = false)
	private Transaction.TransactionStatus status;

	@Column(nullable = false)
	private PaymentType paymentType;

	@Column
	private LocalDateTime expiresAt;

	@Column
	private LocalDateTime finalizedAt;

	@Column
	private Long accountId;

	@Column
	private Long purchaseId;
}
