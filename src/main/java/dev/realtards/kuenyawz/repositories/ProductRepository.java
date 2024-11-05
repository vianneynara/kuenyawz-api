package dev.realtards.kuenyawz.repositories;

import dev.realtards.kuenyawz.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findAllByNameLikeIgnoreCase(String name);

	List<Product> findAllByCategoryIs(Product.Category category);

    boolean existsByNameIgnoreCase(String name);

    /**
     * This is not used, but is good to document for future reference.
     */
    @Modifying
    @Query("UPDATE Product p SET " +
        "p.deleted = true, " +
        "p.version = (p.version + 1) " +
        "WHERE p.productId = :productId AND p.version = :version")
    int softDeleteById(@Param("productId") Long id, @Param("version") Long version);

    List<Product> findAllByDeletedTrue();
}
