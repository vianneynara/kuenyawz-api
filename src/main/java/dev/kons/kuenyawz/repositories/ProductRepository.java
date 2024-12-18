package dev.kons.kuenyawz.repositories;

import dev.kons.kuenyawz.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

	List<Product> findAllByNameLikeIgnoreCase(String name);

	List<Product> findAllByCategory(Product.Category category);

	List<Product> findAllByCategoryIsAndNameLikeIgnoreCase(Product.Category category, String name);

	@Query("SELECT p.productId FROM Product p WHERE p.available=TRUE AND p.deleted=FALSE")
	List<Long> findAllAvailableIds();
}