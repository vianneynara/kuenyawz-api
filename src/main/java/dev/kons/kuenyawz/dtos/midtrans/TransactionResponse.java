package dev.kons.kuenyawz.dtos.midtrans;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/// Midtrans transaction response [Reference](https://docs.midtrans.com/reference/backend-integration)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private String token;

    @JsonProperty("redirect_url")
    private String redirectUrl;

    @JsonProperty("transaction_id")
    private String transactionId;

    /// Error messages from Midtrans API

    @JsonProperty("error_messages")
    private List<String> errorMessages;

    @JsonProperty("status_code")
    private String statusCode;

    @JsonProperty("status_message")
    private String statusMessage;
}