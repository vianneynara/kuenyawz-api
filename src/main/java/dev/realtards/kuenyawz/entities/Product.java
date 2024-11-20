package dev.realtards.kuenyawz.entities;

import dev.realtards.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

// TODO: Remove sql restriction and use a custom repository to filter out deleted products
@Entity
@Getter @Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor @NoArgsConstructor @SuperBuilder
public class Product extends DeletableAuditables {

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

	@Column(name = "available", nullable = false)
	private Boolean available;

	@Version
	private Long version;

	@OneToMany(mappedBy = "product", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Variant> variants = new HashSet<>();

	@OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ProductImage> images = new HashSet<>();

	/**
	 * This contains the category of foods available in the store.
	 */
	public enum Category {
		CAKE("CAKE"),
		PASTRY("PASTRY"),
		PASTA("PASTA"),
		PIE("PIE"),
		OTHER("OTHER");

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
