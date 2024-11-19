package dev.realtards.kuenyawz.entities;

import dev.realtards.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
import java.util.Set;

// TODO: Remove sql restriction and use a custom repository to filter out deleted products
@Entity
@SQLRestriction("deleted = false")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
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

	@Column(name = "available", nullable = false)
	private boolean available;

	@Column(name = "deleted", nullable = false)
	private boolean deleted = false;

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
