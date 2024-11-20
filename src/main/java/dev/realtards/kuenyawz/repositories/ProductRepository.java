package dev.realtards.kuenyawz.repositories;

import dev.realtards.kuenyawz.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

	List<Product> findAllByNameLikeIgnoreCase(String name);

	List<Product> findAllByCategory(Product.Category category);

	List<Product> findAllByCategoryIsAndNameLikeIgnoreCase(Product.Category category, String name);
}
