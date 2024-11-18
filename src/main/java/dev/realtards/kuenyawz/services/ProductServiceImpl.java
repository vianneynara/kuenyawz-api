package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.dtos.product.ProductDto;
import dev.realtards.kuenyawz.dtos.product.ProductPatchDto;
import dev.realtards.kuenyawz.dtos.product.ProductPostDto;
import dev.realtards.kuenyawz.entities.Product;
import dev.realtards.kuenyawz.entities.Variant;
import dev.realtards.kuenyawz.exceptions.InvalidRequestBodyValue;
import dev.realtards.kuenyawz.exceptions.ResourceExistsException;
import dev.realtards.kuenyawz.exceptions.ResourceNotFoundException;
import dev.realtards.kuenyawz.mapper.ProductMapper;
import dev.realtards.kuenyawz.repositories.ProductRepository;
import dev.realtards.kuenyawz.repositories.ProductSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final ProductMapper productMapper;
	private final ImageStorageService imageStorageService;

	private final static int DEFAULT_PAGE = 0;
	private final static int DEFAULT_PAGE_SIZE = 10;

	@Override
	public List<ProductDto> getAllProducts(String category, String keyword) {
		List<Product> products = findProducts(category, keyword);
		return products.stream()
			.map(this::convertToDto)
			.toList();
	}

	public Page<ProductDto> getAllProductsPaginated(String category, String keyword, Boolean available, Integer page, Integer pageSize) {
		PageRequest pageRequest = buildPageRequest(page, pageSize);
		Specification<Product> specification = ProductSpecification.withFilters(category, keyword, available);
		Page<Product> products = productRepository.findAll(specification, pageRequest);

		Page<ProductDto> productDtos = products.map(this::convertToDto);
		return productDtos;
	}

	private PageRequest buildPageRequest(Integer page, Integer pageSize) {
		if (page != null && page > 0) {
			page = page - 1;
		} else {
			page = DEFAULT_PAGE;
		}

		if (pageSize != null && pageSize > 0) {
			if (pageSize > 100)
				pageSize = 100;
		} else {
			pageSize = DEFAULT_PAGE_SIZE;
		}

		// Persist results using sorting
		Sort sort = Sort.by(
//			Sort.Order.asc("category"),
			Sort.Order.asc("productId")
		);

		return PageRequest.of(page, pageSize, sort);
	}

	private List<Product> findProducts(String category, String keyword) {
		boolean hasCategory = StringUtils.hasText(category);
		boolean hasKeyword = StringUtils.hasText(keyword);

		if (!hasCategory && !hasKeyword) {
			return productRepository.findAll();
		}

		String processedKeyword = hasKeyword ? "%" + keyword.trim() + "%" : null;

		if (hasCategory) {
			Product.Category categoryEnum = parseCategoryOrThrow(category);
			return hasKeyword
				? productRepository.findAllByCategoryIsAndNameLikeIgnoreCase(categoryEnum, processedKeyword)
				: productRepository.findAllByCategory(categoryEnum);
		} else {
			return productRepository.findAllByNameLikeIgnoreCase(processedKeyword);
		}
	}

	@Override
	public ProductDto createProduct(ProductPostDto productPostDto) {
		validateProductPostDto(productPostDto);

		Product product = buildProductFromDto(productPostDto);

		// Convert and return
		Product savedProduct = productRepository.save(product);
		ProductDto productDto = productMapper.fromEntity(savedProduct);
		return productDto;
	}

	@Override
	public ProductDto getProduct(long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ResourceNotFoundException("Product with ID '" + productId + "' not found"));

		ProductDto productDto = this.convertToDto(product);
		return productDto;
	}

	@Override
	public void hardDeleteProduct(Long productId) {
		Product product = productRepository.findByIdUnfiltered(productId)
			.orElseThrow(() -> new ResourceNotFoundException("Product with ID '" + productId + "' not found"));

		productRepository.updateProductDeletedStatusToFalse(productId);
		productRepository.deleteProductPermanently(product.getProductId());
	}

	@Override
	public void hardDeleteAllProducts() {
		productRepository.updateAllDeletedStatusToFalse();
		productRepository.deleteAllProductsPermanently();
	}

	@Override
	public void softDeleteProduct(Long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ResourceNotFoundException("Product with ID '" + productId + "' not found"));

		product.setDeleted(true);
		productRepository.save(product);
	}

	@Override
	public void softDeleteAllProducts() {
		List<Product> products = productRepository.findAll();
		products.forEach(product -> {
			product.setDeleted(true);
			productRepository.save(product);
		});
	}

	@Override
	public void restoreSoftDeletedProduct(Long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ResourceNotFoundException("Product with ID '" + productId + "' not found"));

		List<Product> nameDupeProducts = productRepository.findAllByNameLikeIgnoreCase(product.getName());
		if (!nameDupeProducts.isEmpty()) {
			throw new ResourceExistsException("Product with name '" + product.getName() + "' exists");
		}

		product.setDeleted(false);
		productRepository.save(product);
	}

	@Override
	public ProductDto patchProduct(Long productId, ProductPatchDto productPatchDto) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ResourceNotFoundException("Product with ID '" + productId + "' not found"));

		if (productRepository.existsByNameIgnoreCase(productPatchDto.getName()))
			throw new ResourceExistsException("Product with name '" + productPatchDto.getName() + "' exists");

		Product updatedProduct = productMapper.updateProductFromPatch(productPatchDto, product);
		Product savedProduct = productRepository.save(updatedProduct);

		// Convert and return
		ProductDto productDto = productMapper.fromEntity(savedProduct);
		return productDto;
	}

	@Override
	public ProductDto patchAvailability(Long productId, boolean available) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ResourceNotFoundException("Product with ID '" + productId + "' not found"));

		product.setAvailable(available);
		Product savedProduct = productRepository.save(product);

		// Convert and return
		ProductDto productDto = productMapper.fromEntity(savedProduct);
		return productDto;
	}

	@Override
	public boolean existsById(Long productId) {
		return productRepository.existsById(productId);
	}

	public boolean existsIncludingDeleted(Long productId) {
		return productRepository.findByIdUnfiltered(productId).isPresent();
	}

	/**
	 * Converts a Product entity to a ProductDto using the mapper, and then sets the image URLs using
	 * {@link ImageStorageService#getImageUrls(Product)}.
	 *
	 * @param product {@link Product}
	 * @return {@link ProductDto}
	 */
	private ProductDto convertToDto(Product product) {
		ProductDto productDto = productMapper.fromEntity(product);
		productDto.setImages(imageStorageService.getImageUrls(product));
		return productDto;
	}

	private void validateProductPostDto(ProductPostDto productPostDto) {
		if (productPostDto == null) {
			throw new InvalidRequestBodyValue("ProductPostDto cannot be null");
		}
		if (productPostDto.getVariants() == null || productPostDto.getVariants().isEmpty()) {
			throw new InvalidRequestBodyValue("Variants must not be empty");
		}
		if (productRepository.existsByNameIgnoreCase(productPostDto.getName())) {
			throw new ResourceExistsException("Product with name '" + productPostDto.getName() + "' exists");
		}
	}

	private Product buildProductFromDto(ProductPostDto productPostDto) {
		Product product = Product.builder()
			.name(productPostDto.getName())
			.tagline(productPostDto.getTagline())
			.description(productPostDto.getDescription())
			.category(Product.Category.fromString(productPostDto.getCategory()))
			.available(productPostDto.isAvailable())
			.deleted(false)
			.build();

		Set<Variant> variants = productPostDto.getVariants().stream()
			.map(dto -> {
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

				return variant;
			})
			.collect(Collectors.toSet());

		product.setVariants(variants);
		return product;
	}

	// Get all products methods

	private Product.Category parseCategoryOrThrow(String category) {
		category = category.trim().toUpperCase();
		try {
			return Product.Category.fromString(category);
		} catch (IllegalArgumentException e) {
			throw new InvalidRequestBodyValue("Invalid category: " + category);
		}
	}
}
