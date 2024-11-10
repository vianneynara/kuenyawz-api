package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.dtos.product.ProductDto;
import dev.realtards.kuenyawz.dtos.product.ProductPatchDto;
import dev.realtards.kuenyawz.dtos.product.ProductPostDto;
import dev.realtards.kuenyawz.exceptions.InvalidRequestBodyValue;
import dev.realtards.kuenyawz.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductService {

	/**
	 * Master method to get all products.
	 *
	 * @param category {@link String} optional category filter
	 * @param keyword {@link String} optional keyword filter
	 * @return {@link List} of {@link ProductDto}
	 */
	List<ProductDto> getAllProducts(String category, String keyword);

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
	ProductDto getProduct(long productId);


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
