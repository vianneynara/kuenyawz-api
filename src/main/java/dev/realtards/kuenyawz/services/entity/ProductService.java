package dev.realtards.kuenyawz.services.entity;

import dev.realtards.kuenyawz.dtos.product.ProductDto;
import dev.realtards.kuenyawz.dtos.product.ProductPatchDto;
import dev.realtards.kuenyawz.dtos.product.ProductPostDto;
import dev.realtards.kuenyawz.entities.Product;
import dev.realtards.kuenyawz.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductService {

	/**
	 * Master method to get all products.
	 *
	 * @param category {@link String} optional category filter
	 * @param keyword  {@link String} optional keyword filter
	 * @return {@link List} of {@link ProductDto}
	 */
	@Transactional(readOnly = true)
	List<ProductDto> getAllProducts(String category, String keyword);

	/**
	 * Master method to get all products with paginated result.
	 *
	 * @param category  {@link String} optional category filter
	 * @param keyword   {@link String} optional keyword filter
	 * @param available
	 * @param page      {@link Integer} optional page number
	 * @param pageSize  {@link Integer} optional page size
	 * @return {@link Page} of {@link ProductDto}
	 */
	@Transactional(readOnly = true)
	Page<ProductDto> getAllProductsPaginated(String category, String keyword, Boolean available, Integer page, Integer pageSize);

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
	 * Patches the availability of a product by its ID.
	 *
	 * @param productId {@link Long}
	 * @param available {@link Boolean}
	 * @return {@link ProductDto}
	 */
	@Transactional
	ProductDto patchAvailability(Long productId, boolean available);

	/**
	 * Checks if a product exists by its ID.
	 *
	 * @param productId {@link Long}
	 * @return {@link Boolean}
	 */
	@Transactional(readOnly = true)
	boolean existsById(Long productId);

	ProductDto convertToDto(Product product);
}
