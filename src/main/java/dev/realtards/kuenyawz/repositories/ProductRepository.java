package dev.realtards.kuenyawz.repositories;

import dev.realtards.kuenyawz.entities.Product;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findAllByNameLikeIgnoreCase(String name);

	List<Product> findAllByCategory(Product.Category category);

	List<Product> findAllByCategoryIsAndNameLikeIgnoreCase(Product.Category category, String name);

	Page<Product> findAll(Specification<Product> specification, Pageable pageable);

	boolean existsByNameIgnoreCase(String name);

	boolean existsByNameIgnoreCaseAndProductIdNot(String name, Long productId);

	@Query("SELECT p FROM Product p WHERE p.productId = :productId")
	@SQLRestriction("")
	Optional<Product> findByIdUnfiltered(@Param("productId") Long id);

	@Query("SELECT p FROM Product p")
	@SQLRestriction("")
	List<Product> findAllUnfiltered();

	@Modifying
	@Transactional
	@Query("DELETE FROM Product p WHERE p.productId = :productId AND p.deleted IN (true, false)")
	void deleteProductPermanently(@Param("productId") Long id);

	@Modifying
	@Transactional
	@Query("DELETE FROM Product p WHERE p.deleted IN (true, false)")
	void deleteAllProductsPermanently();

	@Modifying
	@Transactional
	@SQLRestriction("")
	@Query("UPDATE Product p SET p.deleted = true WHERE p.productId = :productId AND p.deleted = false")
	void updateProductDeletedStatusToFalse(@Param("productId") Long id);

	@Modifying
	@Transactional
	@Query("UPDATE Product p SET p.deleted = true WHERE p.deleted = false")
	@SQLRestriction("")
	void updateAllDeletedStatusToFalse();
}
