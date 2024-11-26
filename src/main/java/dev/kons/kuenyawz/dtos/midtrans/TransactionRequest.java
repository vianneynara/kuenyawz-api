package dev.kons.kuenyawz.dtos.midtrans;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.kons.kuenyawz.entities.Account;
import dev.kons.kuenyawz.entities.Purchase;
import dev.kons.kuenyawz.entities.PurchaseItem;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/// The structure follows Midtrans's Postman collection for creating a transaction.
/// This will be used for `POST https://app.sandbox.midtrans.com/snap/v1/transactions`
///
/// The structure is as follows:
/// ```
/// {
///   "transaction_details": {
///     "order_id": "ORDER-102-{{$timestamp}}",
///     "gross_amount": 10000
///   },
///   "credit_card": {
///     "secure": true
///   },
///   "item_details": [{
///     "id": "ITEM1",
///     "price": 10000,
///     "quantity": 1,
///     "name": "Midtrans Bear"
///   }],
///   "customer_details": {
///     "first_name": "TEST",
///     "last_name": "MIDTRANSER",
///     "email": "noreply@example.com",
///     "phone": "+628123456",
///     "billing_address": {
///       "first_name": "TEST",
///       "last_name": "MIDTRANSER",
///       "email": "noreply@example.com",
///       "phone": "081 2233 44-55",
///       "address": "Sudirman",
///       "city": "Jakarta",
///       "postal_code": "12190",
///       "country_code": "IDN"
///    },
///     "shipping_address": {
///       "first_name": "TEST",
///       "last_name": "MIDTRANSER",
///       "email": "noreply@example.com",
///       "phone": "0812345678910",
///       "address": "Sudirman",
///       "city": "Jakarta",
///       "postal_code": "12190",
///       "country_codeDN"
///     }
///   }
/// }
/// ```
///
/// Refer to https://docs.midtrans.com/reference/request-body-json-parameter for the
/// full information request body parameters. Many optional fields are removed.
@Data
@Builder
public class TransactionRequest {

	@NotNull
    @JsonProperty("transaction_details")
    private TransactionDetails transactionDetails;

	@NotNull
    @JsonProperty("item_details")
    private List<ItemDetail> itemDetails;

	@NotNull
    @JsonProperty("customer_details")
    private CustomerDetails customerDetails;

	@NotNull
	@JsonProperty("expiry")
	private Expiry expiry;

    @Data
    @Builder
    public static class TransactionDetails {
        @JsonProperty("order_id")
        private String orderId;

        @JsonProperty("gross_amount")
        private BigDecimal grossAmount;

		public static TransactionDetails of(Purchase purchase) {
			return TransactionDetails.builder()
				.orderId(purchase.getPurchaseId().toString())
				.grossAmount(purchase.getTotalPriceWithFee())
				.build();
		}
    }

    @Data
    @Builder
    public static class ItemDetail {
        private String id;
        private BigDecimal price;
        private Integer quantity;
        private String name;

		public static ItemDetail of(PurchaseItem purchaseItem) {
			return ItemDetail.builder()
				.id(purchaseItem.getVariant().getVariantId().toString())
				.price(purchaseItem.getBoughtPrice())
				.quantity(purchaseItem.getQuantity())
				.name(purchaseItem.getVariant().getProduct().getName())
				.build();
		}

		public static List<ItemDetail> of(List<PurchaseItem> purchaseItems) {
			return purchaseItems.stream()
				.map(ItemDetail::of)
				.toList();
		}
    }

    @Data
    @Builder
    public static class CustomerDetails {
        @JsonProperty("first_name")
        private String firstName;

        @JsonProperty("last_name")
        private String lastName;

        private String email;
        private String phone;

        @JsonProperty("shipping_address")
        private Address shippingAddress;

		public static CustomerDetails of(Purchase purchase, Account account) {
			return CustomerDetails.builder()
				.firstName(account.getFirstName())
				.phone(account.getPhone())
				.shippingAddress(Address.of(purchase, account))
				.build();
		}
    }

    @Data
    @Builder
    public static class Address {
        @JsonProperty("first_name")
        private String firstName;

        @JsonProperty("last_name")
        private String lastName;

        private String email;
        private String phone;
        private String address;

        @JsonProperty("country_code")
        private String countryCode;

		public static Address of(Purchase purchase, Account account) {
			Address address = Address.builder()
				.firstName(account.getFirstName())
				.phone(account.getPhone())
				.address(purchase.getFullAddress())
				.countryCode("IDN")
				.build();

			final String email = account.getEmail();
			if (StringUtils.hasText(email)) {
				address.setEmail(email);
			}

			final String lastName = account.getLastName();
			if (StringUtils.hasText(lastName)) {
				address.setLastName(lastName);
			}

			return address;
		}
    }

	@Data
	@Builder
	public static class Expiry {

		private Integer duration;
		private String unit;

		public static Expiry defaultExpiry() {
			return Expiry.builder()
				.duration(1)
				.unit("day")
				.build();
		}
	}
}