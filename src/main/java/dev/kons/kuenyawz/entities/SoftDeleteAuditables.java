package dev.kons.kuenyawz.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * This abstract class is used for simple deletable {@link Auditables} with {@link #deleted}
 * attribute for deleted checking.
 */
@MappedSuperclass
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class SoftDeleteAuditables extends Auditables {

	@Column(name = "deleted", nullable = false)
	private Boolean deleted = false;
}
