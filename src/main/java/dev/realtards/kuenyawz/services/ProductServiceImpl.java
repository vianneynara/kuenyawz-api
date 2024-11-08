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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
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

	@Override
	public List<ProductDto> getAllProducts(String category) {
		if (StringUtils.hasText(category)) {
			return getProductsByCategory(category);
		} else {
			return productRepository.findAll()
				.stream()
				.map(this::convertToDto)
				.toList();
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
	public List<ProductDto> getAllProductByKeyword(String keyword) {
		List<Product> products = productRepository.findAllByNameLikeIgnoreCase(keyword);

		List<ProductDto> productDtos = products.stream().map(productMapper::fromEntity).toList();
		return productDtos;
	}

	@Override
	public List<ProductDto> getProductsByCategory(String category) {
		category = category.trim().toUpperCase();
		try {
			Product.Category categoryEnum = Product.Category.fromString(category);
			List<Product> products = productRepository.findAllByCategoryIs(categoryEnum);

			List<ProductDto> productDtos = products.stream().map(productMapper::fromEntity).toList();
			return productDtos;
		} catch (IllegalArgumentException e) {
			throw new InvalidRequestBodyValue("Invalid category: " + category);
		}
	}

	@Override
	public void hardDeleteProduct(Long productId) {
		if (!productRepository.existsById(productId)) {
			throw new ResourceNotFoundException("Product with ID '" + productId + "' not found");
		}

		productRepository.deleteById(productId);
	}

	@Override
	public void hardDeleteAllProducts() {
		productRepository.deleteAll();
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
}
