package dev.kons.kuenyawz.entities;

import dev.kons.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = "product")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Variant extends Auditables {

	@Id
	@SnowFlakeIdValue(name = "variant_id")
	@Column(name = "variant_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
	private Long variantId;

	@Column
	private BigDecimal price;

	@Column
	private String type;

	@Column
	private Integer minQuantity;

	@Column
	private Integer maxQuantity;

	@Version
	private Long version;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Override
	public String toString() {
		return "Variant{" +
				"variantId=" + variantId +
				", price=" + price +
				", type='" + type + '\'' +
				", minQuantity=" + minQuantity +
				", maxQuantity=" + maxQuantity +
				", version=" + version +
				'}';
	}
}
