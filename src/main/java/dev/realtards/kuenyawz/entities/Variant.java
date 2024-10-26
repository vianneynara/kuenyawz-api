package dev.realtards.kuenyawz.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import dev.realtards.kuenyawz.utils.idgenerator.SnowFlakeIdValue;

import java.math.BigInteger;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Variant extends Auditables {

	@Id
	@SnowFlakeIdValue(name = "variant_id")
	@Column(name = "variant_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
	private Long variantId;
	@Column
	private BigInteger price;
	@Column
	private String type;

	@ManyToOne()
	@JoinColumn(name = "product_id", nullable = false)
	@JsonBackReference
	private Product product;
}
