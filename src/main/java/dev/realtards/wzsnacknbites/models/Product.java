package dev.realtards.wzsnacknbites.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.realtards.wzsnacknbites.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product extends Auditables {

	@Id
	@SnowFlakeIdValue(name = "product_id")
	@Column(name = "product_id")
	private Long productId;
	private UUID uuid = UUID.randomUUID();
	@Column
	private String name;
	@Column
	private String tagline;
	@Column
	private String description;
	@Column
	private Category category;
	@Column
	private Integer min_quantity;
	@Column
	private Integer max_quantity;
	@Column
	private boolean isAvailable;

	@OneToMany(mappedBy = "product")
	@JsonManagedReference
	private Set<ProductVariant> variants = new HashSet<>();

	/**
	 * This contains the category of foods available in the store.
	 */
	public enum Category {
		CAKE("cake"),
		PASTRY("pastry"),
		PASTA("pasta"),
		PIE("pie"),
		OTHER("other");

		private final String value;

		Category(String value) {
			this.value = value;
		}
	}
}
