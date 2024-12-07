package dev.kons.kuenyawz.dtos.midtrans;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Midtrans purchase callbacks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseCallbacksPostDto {

	@Schema(description = "URL to redirect when the transaction is successful", example = "https://example.com/success")
	@NotBlank(message = "Success URL must not be blank")
	private String finish;

	@Schema(description = "URL to redirect when the transaction fails", example = "https://example.com/failed")
	@NotBlank(message = "Error URL must not be blank")
	private String error;
}
