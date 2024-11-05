package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.dtos.product.ProductDto;
import dev.realtards.kuenyawz.dtos.product.ProductPatchDto;
import dev.realtards.kuenyawz.dtos.product.ProductPostDto;
import dev.realtards.kuenyawz.entities.Product;
import dev.realtards.kuenyawz.exceptions.InvalidRequestBodyValue;
import dev.realtards.kuenyawz.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductService {

	/**
	 * Master method to get all products.
	 *
	 * @return {@link List} of {@link ProductDto}
	 */
	@Transactional(readOnly = true)
	List<ProductDto> getAllProducts();

	/**
	 * Creates a new product from the DTO with the provided variants' DTOs.
	 *
	 * @param productPostDto {@link ProductPostDto}
	 * @return {@link ProductDto}
	 */
	@Transactional
	ProductDto createProduct(ProductPostDto productPostDto);

	/**
	 * Retrieves a product by its ID.
	 *
	 * @param productId {@link Long}
	 * @return {@link ProductDto}
	 * @throws ResourceNotFoundException if the product is not found
	 */
	@Transactional(readOnly = true)
	ProductDto getProduct(long productId);

	/**
	 * Retrieves a product by a keyword.
	 *
	 * @param keyword {@link String}
	 * @return {@link ProductDto}
	 * @throws ResourceNotFoundException if the product is not found
	 */
	@Transactional(readOnly = true)
	List<ProductDto> getAllProductByKeyword(String keyword);

	/**
	 * Retrieves all products by a category.
	 *
	 * @param category {@link String}
	 * @return {@link List} of {@link ProductDto}
	 * @throws InvalidRequestBodyValue if the category is invalid
	 */
	@Transactional(readOnly = true)
	List<ProductDto> getProductsByCategory(String category);

	/**
	 * Deletes a product by its ID.
	 *
	 * @param productId {@link Long}
	 * @throws ResourceNotFoundException if the product is not found
	 */
	@Transactional
	void hardDeleteProduct(Long productId);

	/**
	 * Deletes all products in the database and all the images related to it.
	 */
	@Transactional
	void hardDeleteAllProducts();

	/**
	 * Soft deletes a product by its ID.
	 *
	 * @param productId {@link Long}
	 */
	@Transactional
	void softDeleteProduct(Long productId);

	/**
	 * Soft deletes all products in the database.
	 */
	@Transactional
	void softDeleteAllProducts();

	/**
	 * Restores a soft-deleted product by its ID. It will not restore if exists a non soft-deleted product 
	 * with the same name.
	 *
	 * @param productId {@link Long}
	 */
	void restoreSoftDeletedProduct(Long productId);

	/**
	 * Patches a product by its ID.
	 *
	 * @param productId {@link Long}
	 * @param productPatchDto {@link ProductPatchDto}
	 * @return {@link ProductDto}
	 * @throws ResourceNotFoundException if the product is not found
	 */
	@Transactional
	ProductDto patchProduct(Long productId, ProductPatchDto productPatchDto);

	/**
	 * Checks if a product exists by its ID.
	 *
	 * @param productId {@link Long}
	 * @return {@link Boolean}
	 */
	boolean existsById(Long productId);
}
