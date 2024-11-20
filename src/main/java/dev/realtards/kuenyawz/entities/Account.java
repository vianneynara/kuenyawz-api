package dev.realtards.kuenyawz.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import dev.realtards.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account extends Auditables implements UserDetails {

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
	private String phone;

	@Column
	private LocalDateTime phoneVerifiedAt;

	@Column(unique = true)
	private String email;

	@Column
	private LocalDateTime emailVerifiedAt;

	@Column
	private Privilege privilege;

	@Version
	private Long version;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(privilege);
	}

	@Override
	public String getUsername() {
		return phone;
	}

	@Override
	public boolean isAccountNonExpired() {
		return UserDetails.super.isAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return UserDetails.super.isAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return UserDetails.super.isCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled() {
		return UserDetails.super.isEnabled();
	}

	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Transactions> transactions;

	/**
	 * Type of privilege of an account.
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	public enum Privilege implements GrantedAuthority {
		@JsonProperty("ADMIN")
		ADMIN("ROLE_ADMIN"),

		@JsonProperty("USER")
		USER("ROLE_USER");

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

		@Override
		public String getAuthority() {
			return privilege;
		}
	}
}
