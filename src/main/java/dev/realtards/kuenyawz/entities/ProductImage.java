package dev.realtards.kuenyawz.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage extends Auditables {

	@Id
//	@SnowFlakeIdValue(name = "product_image_id")
	@Column(name = "product_image_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
	private Long productImageId;

	@Column
	private String originalFilename;

	@Column
	private String relativePath;

	@Column
	private Long fileSize;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	@JsonBackReference
	private Product product;
}
