package dev.realtards.wzsnacknbites.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import dev.realtards.wzsnacknbites.utils.idgenerator.SnowFlakeIdValue;

import java.math.BigInteger;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant extends Auditables {

	@Id
	@SnowFlakeIdValue(name = "product_variant_id")
	@Column(name = "product_variant_id")
	private Long variantId;
	@Column
	private BigInteger price;
	@Column
	private String type;

	@ManyToOne()
	@JoinColumn(name = "product_id")
	@JsonBackReference
	private Product product = new Product();
}
