package dev.realtards.kuenyawz.repositories;

import dev.realtards.kuenyawz.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findAllByNameLikeIgnoreCase(String name);

	List<Product> findAllByCategoryIs(Product.Category category);

	boolean existsByNameIgnoreCase(String name);
}
