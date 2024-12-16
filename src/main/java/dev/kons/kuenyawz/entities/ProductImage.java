package dev.kons.kuenyawz.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ProductImage extends Auditables {

	@Id
//	@SnowFlakeIdValue(name = "product_image_id")
	// This is defined by the builder when saving, making it consistent with the filename id
	@Column(name = "product_image_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
	private Long productImageId;

	@Column
	private String originalFilename;

	@Column
	private String storedFilename;

	@Column
	private String relativePath;

	@Column
	private Long fileSize;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	@JsonBackReference
	private Product product;
}
