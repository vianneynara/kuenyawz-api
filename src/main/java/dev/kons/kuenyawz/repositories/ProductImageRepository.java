package dev.kons.kuenyawz.repositories;

import dev.kons.kuenyawz.entities.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

	List<ProductImage> findByProduct_ProductId(Long productId);

	Optional<ProductImage> findByProduct_ProductIdAndProductImageId(Long productId, Long productImageId);

	@Modifying
	@Query("DELETE FROM ProductImage pi WHERE pi.product.productId = :productId")
	int deleteAllByProduct_ProductId(@Param("productId") Long productId);
}
