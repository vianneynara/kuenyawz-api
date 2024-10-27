package dev.realtards.kuenyawz.repositories;

import dev.realtards.kuenyawz.entities.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VariantRepository extends JpaRepository<Variant, Long> {

	Optional<Variant> findFirstByProduct_ProductId(Long productId);

	List<Variant> findAllByProduct_ProductId(Long productId);

	@Query("SELECT COUNT(v) FROM Variant v WHERE v.product.productId = :productId")
	int countVariantsByProduct_ProductId(@Param("productId") Long productId);

    @Modifying
    @Query("DELETE FROM Variant v WHERE v.variantId = :variantId AND v.product.productId = :productId")
    int deleteByVariantIdAndProduct_ProductId(
        @Param("variantId") Long variantId,
        @Param("productId") Long productId
    );
}
