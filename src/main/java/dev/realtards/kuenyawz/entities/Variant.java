package dev.realtards.kuenyawz.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.realtards.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = "product")
//@ToString(callSuper = true, exclude = "product")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Variant extends Auditables {

	@Id
	@SnowFlakeIdValue(name = "variant_id")
	@Column(name = "variant_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
	private Long variantId;

	@Column
	private BigDecimal price;

	@Column
	private String type;

	@Version
	private Long version;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	@JsonBackReference
	private Product product;

	@Override
	public String toString() {
		return "Variant{" +
			"variantId=" + variantId +
			", price=" + price +
			", type='" + type + '\'' +
			", version=" + version +
			", productId=" + (product != null ? product.getProductId() : null) +
			'}';
	}
}
