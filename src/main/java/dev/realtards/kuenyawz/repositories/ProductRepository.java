package dev.realtards.kuenyawz.repositories;

import dev.realtards.kuenyawz.entities.Product;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

	List<Product> findAllByNameLikeIgnoreCase(String name);

	List<Product> findAllByCategory(Product.Category category);

	List<Product> findAllByCategoryIsAndNameLikeIgnoreCase(Product.Category category, String name);

	boolean existsByNameIgnoreCase(String name);

	boolean existsByNameIgnoreCaseAndProductIdNot(String name, Long productId);

	@Query("SELECT p FROM Product p WHERE p.productId = :productId")
	@SQLRestriction("")
	Optional<Product> findByIdUnfiltered(@Param("productId") Long id);

	@Modifying
	@Query("DELETE FROM Product p WHERE p.productId = :productId AND p.deleted IN (true, false)")
	void deleteProductPermanently(@Param("productId") Long id);

	@Modifying
	@Query("DELETE FROM Product p WHERE p.deleted IN (true, false)")
	void deleteAllProductsPermanently();

	@Modifying
	@Query("UPDATE Product p SET p.deleted = true WHERE p.productId = :productId AND p.deleted = false")
	void updateProductDeletedStatusToFalse(@Param("productId") Long id);

	@Modifying
	@Query("UPDATE Product p SET p.deleted = true WHERE p.deleted = false")
	void updateAllDeletedStatusToFalse();
}
