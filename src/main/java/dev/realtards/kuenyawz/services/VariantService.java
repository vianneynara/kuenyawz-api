package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.dtos.product.VariantDto;
import dev.realtards.kuenyawz.dtos.product.VariantPatchDto;
import dev.realtards.kuenyawz.dtos.product.VariantPostDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface VariantService {

	@Transactional(readOnly = true)
	List<VariantDto> getAllVariants();

	@Transactional
	VariantDto createVariant(Long productId, VariantPostDto variantDtos);

	List<VariantDto> createVariants(Long productId, VariantPostDto... variantDtos);

	@Transactional(readOnly = true)
	VariantDto getVariant(long variantId);

	@Transactional(readOnly = true)
	VariantDto patchVariant(Long variantId, VariantPatchDto variantPatchDto);

	@Transactional
	void deleteVariant(Long variantId, Long productId);
}
