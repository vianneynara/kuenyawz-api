package dev.realtards.kuenyawz.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.realtards.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product extends Auditables {

	@Id
	@SnowFlakeIdValue(name = "product_id")
	@Column(name = "product_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
	private Long productId;

	@Column
	private String name;

	@Column
	private String tagline;

	@Column
	private String description;

	@Column
	private Category category;

	@Column
	private Integer minQuantity;

	@Column
	private Integer maxQuantity;

	@Column
	private boolean isAvailable;

	@Version
	private Long version;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private Set<Variant> variants = new HashSet<>();

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private Set<ProductImage> images = new HashSet<>();

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

		public static Category fromString(String value) {
			for (Category category : Category.values()) {
				if (category.value.equalsIgnoreCase(value)) {
					return category;
				}
			}
			throw new IllegalArgumentException("Invalid category: " + value);
		}
	}
}
