package dev.kons.kuenyawz.entities;

import dev.kons.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RefreshToken extends Auditables {

	@Id
	@SnowFlakeIdValue(name = "refresh_token_id")
	@Column(name = "refresh_token_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
	private Long refreshTokenId;

	@Column(nullable = false, unique = true)
	private String token;

	@Column(nullable = false)
	private Long accountId;

	@Column(nullable = false)
	private String username;

	@Column
	private LocalDateTime expiresAt;

	@Column
	private boolean isRevoked;

	@Version
	private Long version;
}
