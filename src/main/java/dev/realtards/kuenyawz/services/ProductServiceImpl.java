package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.dtos.product.ProductDto;
import dev.realtards.kuenyawz.dtos.product.ProductPatchDto;
import dev.realtards.kuenyawz.dtos.product.ProductPostDto;
import dev.realtards.kuenyawz.dtos.product.VariantPostDto;
import dev.realtards.kuenyawz.entities.Product;
import dev.realtards.kuenyawz.entities.Variant;
import dev.realtards.kuenyawz.exceptions.InvalidRequestBodyValue;
import dev.realtards.kuenyawz.exceptions.ResourceNotFoundException;
import dev.realtards.kuenyawz.mapper.ProductMapper;
import dev.realtards.kuenyawz.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final ProductMapper productMapper;

	/**
	 * Master method to get all products.
	 *
	 * @return {@link List} of {@link ProductDto}
	 */
	@Override
	public List<ProductDto> getAllProducts() {
		return productRepository.findAll()
			.stream()
			.map(productMapper::fromEntity)
			.toList();
	}

	/**
	 * Creates a new product from the DTO with the provided variants' DTOs.
	 *
	 * @param productPostDto {@link ProductPostDto}
	 * @return {@link ProductDto}
	 */
	@Override
	public ProductDto createProduct(ProductPostDto productPostDto) {
		Objects.requireNonNull(productPostDto, "ProductPostDto cannot be null");
		Objects.requireNonNull(productPostDto.getVariants(), "Variants cannot be null");

		if (productPostDto.getVariants().isEmpty()) {
			throw new IllegalArgumentException("Product must have at least one variant");
		}

		Product product = Product.builder()
			.name(productPostDto.getName())
			.tagline(productPostDto.getTagline())
			.description(productPostDto.getDescription())
			.category(Product.Category.fromString(productPostDto.getCategory()))
			.minQuantity(productPostDto.getMinQuantity())
			.maxQuantity(productPostDto.getMaxQuantity())
			.build();

		Set<Variant> variants = new HashSet<>();
		for (VariantPostDto dto : productPostDto.getVariants()) {
			Variant variant = Variant.builder()
				.price(dto.getPrice())
				.type(dto.getType())
				.product(product)
				.build();
			variants.add(variant);
		}
		product.setVariants(variants);

		// Convert and return
		Product savedProduct = productRepository.save(product);
		log.info("CREATED: {}", savedProduct);
		ProductDto productDto = productMapper.fromEntity(savedProduct);
		return productDto;
	}

	/**
	 * Retrieves a product by its ID.
	 *
	 * @param productId {@link Long}
	 * @return {@link ProductDto}
	 * @throws ResourceNotFoundException if the product is not found
	 */
	@Override
	public ProductDto getProduct(long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ResourceNotFoundException("Product with ID '" + productId + "' not found"));

		log.info("RETRIEVED by ID: {}", product);
		ProductDto productDto = productMapper.fromEntity(product);
		return productDto;
	}

	/**
	 * Retrieves a product by a keyword.
	 *
	 * @param keyword {@link String}
	 * @return {@link ProductDto}
	 * @throws ResourceNotFoundException if the product is not found
	 */
	@Override
	public List<ProductDto> getAllProductByKeyword(String keyword) {
		List<Product> products = productRepository.findAllByNameLikeIgnoreCase(keyword);

		List<ProductDto> productDtos = products.stream().map(productMapper::fromEntity).toList();
		return productDtos;
	}

	/**
	 * Retrieves all products by a category.
	 *
	 * @param category {@link String}
	 * @return {@link List} of {@link ProductDto}
	 * @throws InvalidRequestBodyValue if the category is invalid
	 */
	@Override
	public List<ProductDto> getProductsByCategory(String category) {
		try {
			Product.Category categoryEnum = Product.Category.fromString(category);
			List<Product> products = productRepository.findAllByCategoryIs(categoryEnum);
			List<ProductDto> productDtos = products.stream().map(productMapper::fromEntity).toList();
			return productDtos;
		} catch (IllegalArgumentException e) {
			throw new InvalidRequestBodyValue("Invalid category: " + category);
		}
	}

	/**
	 * Deletes a product by its ID.
	 *
	 * @param productId {@link Long}
	 * @throws ResourceNotFoundException if the product is not found
	 */
	@Override
	public void deleteProduct(long productId) {
		if (!productRepository.existsById(productId)) {
			throw new ResourceNotFoundException("Product with ID '" + productId + "' not found");
		}

		productRepository.deleteById(productId);
		log.info("DELETED: {}", productId);
	}

	/**
	 * Patches a product by its ID.
	 *
	 * @param productId {@link Long}
	 * @param productPatchDto {@link ProductPatchDto}
	 * @return {@link ProductDto}
	 * @throws ResourceNotFoundException if the product is not found
	 */
	@Override
	public ProductDto patchProduct(Long productId, ProductPatchDto productPatchDto) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ResourceNotFoundException("Product with ID '" + productId + "' not found"));

		Product updatedProduct = productMapper.updateProductFromPatch(productPatchDto, product);
		Product savedProduct = productRepository.save(updatedProduct);
		log.info("UPDATED: {}", savedProduct);

		// Convert and return
		ProductDto productDto = productMapper.fromEntity(savedProduct);
		return productDto;
	}

	/**
	 * Checks if a product exists by its ID.
	 *
	 * @param productId {@link Long}
	 * @return {@link Boolean}
	 */
	@Override
	public boolean existsById(Long productId) {
		return productRepository.existsById(productId);
	}
}
