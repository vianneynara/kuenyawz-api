package dev.kons.kuenyawz.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum PaymentType {
		@JsonProperty("DOWN_PAYMENT")
		DOWN_PAYMENT("Down Payment", "DP"),

		@JsonProperty("FULL_PAYMENT")
		FULL_PAYMENT("Full Payment", "FP");

		private final String description;
		private final String alias;

		PaymentType(String description, String alias) {
			this.description = description;
			this.alias = alias;
		}

		@JsonValue
		public String getType() {
			return name();
		}

		@JsonCreator
		public static PaymentType fromString(String value) {
			for (PaymentType type : PaymentType.values()) {
				if (type.name().equalsIgnoreCase(value) || type.alias.equalsIgnoreCase(value)) {
					return type;
				}
			}
			throw new IllegalArgumentException("Invalid payment type: " + value);
		}
	}