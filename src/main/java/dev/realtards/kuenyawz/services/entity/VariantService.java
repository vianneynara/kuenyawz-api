package dev.realtards.kuenyawz.services.entity;

import dev.realtards.kuenyawz.dtos.product.VariantDto;
import dev.realtards.kuenyawz.dtos.product.VariantPatchDto;
import dev.realtards.kuenyawz.dtos.product.VariantPostDto;
import dev.realtards.kuenyawz.entities.Variant;
import dev.realtards.kuenyawz.exceptions.IllegalOperationException;
import dev.realtards.kuenyawz.exceptions.ResourceNotFoundException;
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
	 * Gets a variant by its ID.
	 *
	 * @param variantId {@link Long}
	 * @return {@link VariantDto}
	 */
	@Transactional(readOnly = true)
	VariantDto getVariant(long variantId);

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
}
