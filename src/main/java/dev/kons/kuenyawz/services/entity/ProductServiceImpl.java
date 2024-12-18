package dev.kons.kuenyawz.services.entity;

import dev.kons.kuenyawz.dtos.product.ProductDto;
import dev.kons.kuenyawz.dtos.product.ProductPatchDto;
import dev.kons.kuenyawz.dtos.product.ProductPostDto;
import dev.kons.kuenyawz.dtos.product.VariantDto;
import dev.kons.kuenyawz.entities.Product;
import dev.kons.kuenyawz.entities.Variant;
import dev.kons.kuenyawz.exceptions.InvalidRequestBodyValue;
import dev.kons.kuenyawz.exceptions.ResourceExistsException;
import dev.kons.kuenyawz.exceptions.ResourceNotFoundException;
import dev.kons.kuenyawz.mapper.ProductMapper;
import dev.kons.kuenyawz.repositories.ProductRepository;
import dev.kons.kuenyawz.repositories.ProductSpec;
import dev.kons.kuenyawz.services.logic.ImageStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.kons.kuenyawz.repositories.ProductSpec.*;

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

	@Override
	@Cacheable(
		value = "productsCache",
		key = "#root.methodName + '_' + " +
			"T(java.util.Objects).hash(" +
			"    #category, " +
			"    #keyword, " +
			"    #available, " +
			"    #page, " +
			"    #pageSize" +
			")",
		condition = "#page != null && #pageSize != null"
	)
	public Page<ProductDto> getAllProductsPaginated(String category, String keyword, Boolean available, Integer page, Integer pageSize) {
		log.info("Fetching products with category: {}, keyword: {}, available: {}, page: {}, pageSize: {}",
			category, keyword, available, page, pageSize);

		PageRequest pageRequest = buildPageRequest(page, pageSize);
		Specification<Product> specification = withFilters(category, keyword, available).and(isNotDeleted());
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
	@CacheEvict(value = "productsCache", allEntries = true)
	public ProductDto createProduct(ProductPostDto productPostDto) {
		validateProductPostDto(productPostDto);

		Product product = buildProductFromDto(productPostDto);

		// Convert and return
		Product savedProduct = productRepository.save(product);
		ProductDto productDto = productMapper.fromEntity(savedProduct);
		return productDto;
	}

	@Override
	@Cacheable(value = "productCache", key = "#productId")
	public ProductDto getProduct(long productId) {
		log.info("Fetching product with ID: {}", productId);

		Product product = productRepository.findOne(withProductId(productId).and(isNotDeleted()))
			.orElseThrow(() -> new ResourceNotFoundException("Product with ID '" + productId + "' not found"));

		ProductDto productDto = this.convertToDto(product);
		return productDto;
	}

	@Override
	@Caching(evict = {
		@CacheEvict(value = "productCache", key = "#productId"),
		@CacheEvict(value = "productsCache", allEntries = true)
	})
	public void hardDeleteProduct(Long productId) {
		Product product = productRepository.findOne(withProductId(productId))
			.orElseThrow(() -> new ResourceNotFoundException("Product with ID '" + productId + "' not found"));

		imageStorageService.deleteAllOfProductId(product.getProductId());
		productRepository.deleteById(productId);
	}

	@Override
	@Caching(evict = {
		@CacheEvict(value = "productCache", allEntries = true),
		@CacheEvict(value = "productsCache", allEntries = true)
	})
	public void hardDeleteAllProducts() {
		imageStorageService.deleteAll();
		productRepository.deleteAll();
	}

	@Override
	@Caching(evict = {
		@CacheEvict(value = "productCache", key = "#productId"),
		@CacheEvict(value = "productsCache", allEntries = true)
	})
	public void softDeleteProduct(Long productId) {
		Product product = productRepository.findOne(withProductId(productId).and(isNotDeleted()))
			.orElseThrow(() -> new ResourceNotFoundException("Product with ID '" + productId + "' not found"));

		product.setDeleted(true);
		product.setAvailable(false);
		productRepository.save(product);
	}

	@Override
	@Caching(evict = {
		@CacheEvict(value = "productCache", allEntries = true),
		@CacheEvict(value = "productsCache", allEntries = true)
	})
	public void softDeleteAllProducts() {
		List<Product> products = productRepository.findAll();
		products.forEach(product -> {
			product.setDeleted(true);
			product.setAvailable(false);
			productRepository.save(product);
		});
	}

	@Override
	@CachePut(value = "productCache", key = "#productId")
	@CacheEvict(value = "productsCache", allEntries = true)
	public ProductDto patchProduct(Long productId, ProductPatchDto productPatchDto) {
		Product product = productRepository.findOne(withProductId(productId).and(isNotDeleted()))
			.orElseThrow(() -> new ResourceNotFoundException("Product with ID '" + productId + "' not found"));

		if (productRepository.findOne(ProductSpec.withName(productPatchDto.getName())
				.and(isNotDeleted())
				.and(withProductIdNot(productId)))
			.isPresent()
		) {
			throw new ResourceExistsException("Product with name '" + productPatchDto.getName() + "' exists");
		}
		if (productPatchDto.getCategory() != null) {
			productPatchDto.setCategory(productPatchDto.getCategory().toUpperCase());
		}

		Product updatedProduct = productMapper.updateProductFromPatch(productPatchDto, product);
		Product savedProduct = productRepository.save(updatedProduct);

		// Convert and return
		ProductDto productDto = productMapper.fromEntity(savedProduct);
		return productDto;
	}

	@Override
	@CachePut(value = "productCache", key = "#productId")
	@CacheEvict(value = "productsCache", allEntries = true)
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

	/**
	 * Converts a Product entity to a ProductDto using the mapper, and then sets the image URLs using
	 * {@link ImageStorageService#getImageUrls(Product)}.
	 *
	 * @param product {@link Product}
	 * @return {@link ProductDto}
	 */
	@Override
	public ProductDto convertToDto(Product product) {
		ProductDto productDto = productMapper.fromEntity(product);
		if (productDto.getVariants() == null) {
			productDto.setVariants(new ArrayList<>());
		}
		productDto.getVariants().sort(Comparator.comparing(VariantDto::getVariantId));

		productDto.setImages(imageStorageService.getImageUrls(product));
		return productDto;
	}

	@Override
	public ProductDto convertToDtoNoVariant(Product product) {
		ProductDto productDto = productMapper.fromEntity(product);
		productDto.setVariants(null);
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
		if (productRepository.findOne(withName(productPostDto.getName()).and(isNotDeleted())).isPresent()) {
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

	private Product.Category parseCategoryOrThrow(String category) {
		category = category.trim().toUpperCase();
		try {
			return Product.Category.fromString(category);
		} catch (IllegalArgumentException e) {
			throw new InvalidRequestBodyValue("Invalid category: " + category);
		}
	}
}
