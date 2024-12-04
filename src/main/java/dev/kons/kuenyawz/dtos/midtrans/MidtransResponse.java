package dev.kons.kuenyawz.dtos.midtrans;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/// Midtrans transaction response [Reference](https://docs.midtrans.com/reference/backend-integration)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MidtransResponse {
    private String token;

    @JsonProperty("redirect_url")
    private String redirectUrl;

    @JsonProperty("transaction_id")
    private String transactionId;

    /// Transaction details from transaction status

    @JsonProperty("payment_type")
    private String paymentType;

    @JsonProperty("transaction_time")
    private String transactionTime;

    @JsonProperty("transaction_status")
    private String transactionStatus;

    @JsonProperty("fraud_status")
    private String fraudStatus;

    @JsonProperty("signature_key")
    private String signatureKey;

    @JsonProperty("bank")
    private String bank;

    @JsonProperty("gross_amount")
    private String grossAmount;

    @JsonProperty("reference_id")
    private String referenceId;

    /// Error messages from Midtrans API

    @JsonProperty("error_messages")
    private List<String> errorMessages;

    @JsonProperty("status_code")
    private String statusCode;

    @JsonProperty("status_message")
    private String statusMessage;
}