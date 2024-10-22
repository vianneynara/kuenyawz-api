package dev.realtards.wzsnacknbites.models;

import dev.realtards.wzsnacknbites.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
	private Long accountId;
	@Column
	private String password;
	@Column
	private String fullName;
	@Column
	private String googleId;
	@Column
	private String email;
	@Column
	private LocalDateTime email_verified_at;
	@Column
	private String phone;
	@Column
	private Privilege privilege;

	/**
	 * Type of privilege of an account.
	 */
	public enum Privilege {
		ADMIN("admin"),
		USER("user");

		private final String privilege;

		Privilege(String privilege) {
			this.privilege = privilege;
		}
	}
}
