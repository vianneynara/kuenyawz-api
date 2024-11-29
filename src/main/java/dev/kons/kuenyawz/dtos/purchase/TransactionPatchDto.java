package dev.kons.kuenyawz.dtos.purchase;

import dev.kons.kuenyawz.entities.Transaction;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionPatchDto {

	@NotNull(message = "Transaction status must be filled")
	private Transaction.TransactionStatus status;
}
