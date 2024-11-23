package dev.realtards.kuenyawz.services.entity;

import dev.realtards.kuenyawz.dtos.product.VariantDto;
import dev.realtards.kuenyawz.dtos.product.VariantPatchDto;
import dev.realtards.kuenyawz.dtos.product.VariantPostDto;
import dev.realtards.kuenyawz.entities.Product;
import dev.realtards.kuenyawz.entities.Variant;
import dev.realtards.kuenyawz.exceptions.IllegalOperationException;
import dev.realtards.kuenyawz.exceptions.InvalidRequestBodyValue;
import dev.realtards.kuenyawz.exceptions.ResourceNotFoundException;
import dev.realtards.kuenyawz.mapper.VariantMapper;
import dev.realtards.kuenyawz.repositories.ProductRepository;
import dev.realtards.kuenyawz.repositories.VariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class VariantServiceImpl implements VariantService {

	private final ProductRepository productRepository;
	private final VariantRepository variantRepository;
	private final VariantMapper variantMapper;

	@Override
	public List<VariantDto> getAllVariants() {
		List<Variant> variants = variantRepository.findAll();

		List<VariantDto> variantDtos = variants.stream().map(variantMapper::fromEntity).toList();
		return variantDtos;
	}

	@Override
	public VariantDto createVariant(Long productId, VariantPostDto variantPostDto) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ResourceNotFoundException("Product with ID '" + productId + "' not found"));

		Variant variant = Variant.builder()
			.price(variantPostDto.getPrice())
			.type(variantPostDto.getType())
			.product(product)
			.build();

		if (variantPostDto.isQuantityConsistent()) {
			variant.setMinQuantity(variantPostDto.getMinQuantity());
			variant.setMaxQuantity(variantPostDto.getMaxQuantity());
		} else {
			throw new InvalidRequestBodyValue("Minimum quantity and maximum quantity must be consistent");
		}

		product.getVariants().add(variant);

		Variant savedVariant = variantRepository.save(variant);

		// Convert and return
		VariantDto variantDto = variantMapper.fromEntity(savedVariant);
		return variantDto;
	}

	@Override
	public List<VariantDto> createVariants(Long productId, VariantPostDto... variantPostDtos) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ResourceNotFoundException("Product with ID '" + productId + "' not found"));

		Set<Variant> variants = new HashSet<>();
		for (VariantPostDto dto : variantPostDtos) {
			Variant variant = Variant.builder()
				.price(dto.getPrice())
				.type(dto.getType())
				.product(product)
				.build();

			if (dto.isQuantityConsistent()) {
				variant.setMinQuantity(dto.getMinQuantity());
				variant.setMaxQuantity(dto.getMaxQuantity());
			} else {
				throw new InvalidRequestBodyValue("Minimum quantity and maximum quantity must be consistent");
			}

			variants.add(variant);
		}
		product.getVariants().addAll(variants);

		List<Variant> savedVariants = variantRepository.saveAll(variants);
		log.info("CREATED MULTIPLE: {}", savedVariants);

		// Convert and return
		List<VariantDto> variantDtos = savedVariants.stream().map(variantMapper::fromEntity).toList();
		return variantDtos;
	}

	@Override
	public VariantDto getVariant(long variantId) {
		Variant variant = variantRepository.findById(variantId)
			.orElseThrow(() -> new ResourceNotFoundException("Variant with ID '" + variantId + "' not found"));

		// Convert and return
		VariantDto variantDto = variantMapper.fromEntity(variant);
		return variantDto;
	}

	@Override
	public List<VariantDto> getVariantsOfProductId(Long productId) {
		List<Variant> variants = variantRepository.findAllByProduct_ProductId(productId);
		List<VariantDto> variantDtos = variants.stream().map(variantMapper::fromEntity).toList();
		return variantDtos;
	}

	@Override
	public VariantDto patchVariant(Long productId, Long variantId, VariantPatchDto variantPatchDto) {
		if (!productRepository.existsById(productId)) {
			throw new ResourceNotFoundException("Product with ID '" + productId + "' not found");
		}

		Variant variant = variantRepository.findById(variantId)
			.orElseThrow(() -> new ResourceNotFoundException("Variant with ID '" + variantId + "' not found"));

		checkQuantityConsistency(variant, variantPatchDto);

		Variant updatedVariant = variantMapper.updateVariantFromPatch(variantPatchDto, variant);
		Variant savedVariant = variantRepository.save(updatedVariant);
		log.info("UPDATED: {}", savedVariant);

		// Convert and return
		VariantDto variantDto = variantMapper.fromEntity(savedVariant);
		return variantDto;
	}

	@Override
	public void deleteVariant(Long productId, Long variantId) {
		long variantCount = variantRepository.countVariantsByProduct_ProductId(productId);
		if (variantCount <= 1) {
			throw new IllegalOperationException("Cannot delete the last variant of a product");
		}

		int deleted = variantRepository.deleteByVariantIdAndProduct_ProductId(variantId, productId);
		if (deleted == 0) {
			throw new ResourceNotFoundException("Variant with ID '" + variantId + "' not found in Product with ID '" + productId + "'");
		}
		log.info("DELETED: {}", variantId);
	}

	private void checkQuantityConsistency(Variant variant, VariantPatchDto variantPatchDto) {
		Integer minQuantity = variantPatchDto.getMinQuantity();
		Integer maxQuantity = variantPatchDto.getMaxQuantity();

		/*  */ if (minQuantity != null && maxQuantity != null && minQuantity > maxQuantity) {
			throw new InvalidRequestBodyValue("Minimum quantity must be less than or equal to maximum quantity");
		} else if (minQuantity == null && maxQuantity != null && maxQuantity < variant.getMinQuantity()) {
			throw new InvalidRequestBodyValue("Maximum quantity must be greater than or equal to minimum quantity");
		} else if (minQuantity != null && maxQuantity == null && minQuantity > variant.getMaxQuantity()) {
			throw new InvalidRequestBodyValue("Minimum quantity must be less than or equal to maximum quantity");
		} else if (minQuantity == null && maxQuantity == null && variant.getMinQuantity() > variant.getMaxQuantity()) {
			throw new InvalidRequestBodyValue("Minimum quantity must be less than or equal to maximum quantity");
		}
	}
}
