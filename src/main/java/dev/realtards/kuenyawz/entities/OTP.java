package dev.realtards.kuenyawz.entities;

import dev.realtards.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor @NoArgsConstructor @Builder
public class OTP extends Auditables {

	@Id
	@SnowFlakeIdValue(name = "otp_id")
	@Column(name = "otp_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
	private Long otp_id;

	@Column(unique = true, nullable = false)
	private String phone;

	@Column(unique = true, nullable = false)
	private String otp;

	@Column
	private LocalDateTime expiresAt;

	@Column
	private String ipAddress;
}
