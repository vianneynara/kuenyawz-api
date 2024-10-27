package dev.realtards.kuenyawz.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.realtards.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage extends Auditables {

	@Id
	@SnowFlakeIdValue(name = "product_image_id")
	@Column(name = "product_image_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
	private Long productImageId;

	@Column
	private String relativePath;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	@JsonBackReference
	private Product product;
}
