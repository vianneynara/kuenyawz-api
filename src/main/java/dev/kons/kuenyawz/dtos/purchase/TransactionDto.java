package dev.kons.kuenyawz.dtos.purchase;

import dev.kons.kuenyawz.constants.PaymentType;
import dev.kons.kuenyawz.entities.Transaction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(name = "Transaction", description = "Exposed transaction data")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDto {

	@Schema(description = "Transaction id", example = "12345678")
	private Long transactionId;

	@Schema(description = "Midtrans invoice id", example = "jsDAj9768ASdy7")
	private String invoiceId;

	@Schema(description = "Amount of the transaction", example = "10000.00")
	private BigDecimal amount;

	@Schema(description = "Transaction status", example = "PENDING")
	private Transaction.TransactionStatus status;

	@Schema(description = "Payment type of the transaction", example = "DOWN_PAYMENT")
	private PaymentType paymentType;

	@Schema(description = "Time of expiration")
	private LocalDateTime expiresAt;

	@Schema(description = "Time of finalization")
	private LocalDateTime finalizedAt;

	@Schema(description = "Time of creation")
	private LocalDateTime createdAt;

    public static TransactionDto fromEntity(Transaction transaction) {
        return TransactionDto.builder()
            .transactionId(transaction.getTransactionId())
            .invoiceId(transaction.getInvoiceId())
            .amount(transaction.getAmount())
            .status(transaction.getStatus())
            .expiresAt(transaction.getExpiresAt())
            .finalizedAt(transaction.getFinalizedAt())
            .createdAt(transaction.getCreatedAt())
            .build();
    }
}
