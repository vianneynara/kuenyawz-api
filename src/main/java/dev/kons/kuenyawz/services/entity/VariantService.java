package dev.kons.kuenyawz.services.entity;

import dev.kons.kuenyawz.dtos.product.VariantDto;
import dev.kons.kuenyawz.dtos.product.VariantPatchDto;
import dev.kons.kuenyawz.dtos.product.VariantPostDto;
import dev.kons.kuenyawz.entities.Variant;
import dev.kons.kuenyawz.exceptions.IllegalOperationException;
import dev.kons.kuenyawz.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface VariantService {

	/**
	 * Master method to get all variants.
	 *
	 * @return {@link List} of {@link VariantDto}
	 */
	@Transactional(readOnly = true)
	List<VariantDto> getAllVariants();

	/**
	 * Creates a variant and connect it to an existing product.
	 *
	 * @param productId      {@link Long}
	 * @param variantPostDto {@link VariantPostDto}
	 * @return {@link Variant}
	 * @throws ResourceNotFoundException if the product is not found
	 */
	@Transactional
	VariantDto createVariant(Long productId, VariantPostDto variantPostDto);

	/**
	 * Creates multiple variants and connect it to an existing product.
	 *
	 * @param productId       {@link Long}
	 * @param variantPostDtos {@link Iterable} of {@link VariantPostDto}
	 * @return {@link List} of {@link VariantDto}
	 * @throws ResourceNotFoundException if the product is not found
	 */
	@Transactional
	List<VariantDto> createVariants(Long productId, VariantPostDto... variantPostDtos);

	/**
	 * Gets a variant by its ID as DTO.
	 *
	 * @param variantId {@link Long}
	 * @return {@link VariantDto}
	 */
	@Transactional(readOnly = true)
	VariantDto getVariant(long variantId);

	/**
	 * Gets a variant by its ID.
	 *
	 * @param variantId {@link Long}
	 * @return {@link Variant}
	 */
	@Transactional(readOnly = true)
	Variant getVariantById(long variantId);

	/**
	 * Gets all variants of a product by its ID.
	 *
	 * @param productId {@link Long}
	 * @return {@link List} of {@link VariantDto}
	 */
	@Transactional(readOnly = true)
	List<VariantDto> getVariantsOfProductId(Long productId);

	/**
	 * Patches the variant using Mapper.
	 *
	 * @param productId {@link Long}
	 * @param variantId {@link Long}
	 * @param variantPatchDto {@link VariantPatchDto}
	 * @return {@link VariantDto}
	 * @throws ResourceNotFoundException if the variant is not found
	 */
	@Transactional(readOnly = true)
	VariantDto patchVariant(Long productId, Long variantId, VariantPatchDto variantPatchDto);

	/**
	 * Deletes a variant from a product. Checks the variant, then deletes it.
	 *
	 * @param productId {@link Long}
	 * @param variantId {@link Long}
	 * @throws IllegalOperationException if the product only has one variant
	 * @throws ResourceNotFoundException if the variant is not found
	 */
	@Transactional
	void deleteVariant(Long productId, Long variantId);

	static void validateQuantityConsistent(Variant variant, int quantity) {
		if (quantity < 0) {
			throw new IllegalArgumentException("Quantity cannot be negative");
		}
		if (!(quantity >= variant.getMinQuantity() && quantity <= variant.getMaxQuantity())) {
			throw new IllegalArgumentException(String.format("Quantity for id %s must be between %d and %d",
				variant.getVariantId(), variant.getMinQuantity(), variant.getMaxQuantity()));
		}
	}
}
