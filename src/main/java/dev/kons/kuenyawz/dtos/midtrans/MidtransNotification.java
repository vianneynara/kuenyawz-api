package dev.kons.kuenyawz.dtos.midtrans;

/// Notification from Midtrans on payment processes
/// [Reference](https://docs.midtrans.com/docs/https-notification-webhooks)

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Notification from Midtrans on payment processes
 * @see <a href="https://docs.midtrans.com/docs/https-notification-webhooks">Midtrans Notification Webhooks Documentation</a>
 */
@Schema(description = "Notification from Midtrans on payment processes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MidtransNotification {
    // Common Fields for All Payment Types
    @JsonProperty("transaction_time") @Schema(example = "2020-01-09 18:27:19")
    private String transactionTime;

    @JsonProperty("transaction_status") @Schema(example = "capture")
    private String transactionStatus;

    @JsonProperty("transaction_id") @Schema(example = "57d5293c-e65f-4a29-95e4-5959c3fa335b")
    private String transactionId;

    @JsonProperty("status_message") @Schema(example = "midtrans payment notification")
    private String statusMessage;

    @JsonProperty("status_code") @Schema(example = "200")
    private String statusCode;

    @JsonProperty("signature_key")
    private String signatureKey;

    @JsonProperty("payment_type") @Schema(example = "credit_card")
    private String paymentType;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("merchant_id") @Schema(example = "G123456789")
    private String merchantId;

    @JsonProperty("gross_amount")
    private String grossAmount;

    @JsonProperty("fraud_status") @Schema(example = "accept")
    private String fraudStatus;

    @JsonProperty("currency") @Schema(example = "IDR")
    private String currency;

    // Credit Card Specific Fields
    @JsonProperty("masked_card")
    private String maskedCard;

    @JsonProperty("eci")
    private String eci;

    @JsonProperty("channel_response_message")
    private String channelResponseMessage;

    @JsonProperty("channel_response_code")
    private String channelResponseCode;

    @JsonProperty("card_type")
    private String cardType;

    @JsonProperty("bank")
    private String bank;

    @JsonProperty("approval_code") @Schema(example = "123456")
    private String approvalCode;

    // Virtual Account (VA) Specific Fields
    @JsonProperty("va_numbers")
    private List<VaNumber> vaNumbers;

    // Settlement and Timing Fields
    @JsonProperty("settlement_time")
    private String settlementTime;

    // Payment Method Specific Fields
    @JsonProperty("issuer")
    private String issuer;

    @JsonProperty("acquirer")
    private String acquirer;

    @JsonProperty("payment_amounts")
    private List<PaymentAmount> paymentAmounts;

    // Nested Classes for Complex Structures
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VaNumber {
        @JsonProperty("va_number") @Schema(example = "1234567890")
        private String vaNumber;

        @JsonProperty("bank") @Schema(example = "bca")
        private String bank;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentAmount {
        @JsonProperty("paid_at") @Schema(example = "2020-01-09 18:27:19")
        private String paidAt;

        @JsonProperty("amount")
        private String amount;
    }
}