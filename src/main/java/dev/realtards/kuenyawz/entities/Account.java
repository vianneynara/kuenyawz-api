package dev.realtards.kuenyawz.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import dev.realtards.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account extends Auditables {

	@Id
	@SnowFlakeIdValue(name = "account_id")
	@Column(name = "account_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
	private Long accountId;
	@Column
	private String password;
	@Column
	private String fullName;
	@Column(unique = true)
	private String googleId;
	@Column(unique = true)
	private String email;
	@Column
	private LocalDateTime emailVerifiedAt;
	@Column(unique = true)
	private String phone;
	@Column
	private Privilege privilege;
	@Version
	private Long version;

	/**
	 * Type of privilege of an account.
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	public enum Privilege {
		@JsonProperty("admin")
		ADMIN("admin"),

		@JsonProperty("user")
		USER("user");

		private final String privilege;

		Privilege(String privilege) {
			this.privilege = privilege;
		}

		// This tells Jackson to use this value when serializing to JSON
		@JsonValue
		public String getPrivilege() {
			return privilege;
		}

		// This helps Jackson create enum from string value
		@JsonCreator
		public static Privilege fromString(String value) {
			for (Privilege p : Privilege.values()) {
				// If the string matches the current iterated Enum
				if (p.privilege.equalsIgnoreCase(value) ||
					p.name().equalsIgnoreCase(value)) {
					return p;
				}
			}
			throw new IllegalArgumentException("Invalid privilege: " + value);
		}
	}
}
