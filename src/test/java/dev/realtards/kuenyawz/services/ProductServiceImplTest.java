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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private ProductServiceImpl productService;

	@Mock
	private ImageStorageService imageStorageService;

	@Spy
	private ProductMapper productMapper;

	private Product product;
	private ProductDto productDto;
	private Set<Variant> variants;
	private ProductPostDto productPostDto;
	private List<VariantPostDto> variantPostDtos;

	@BeforeEach
	void setUp() {
		// Common test data setup
		variants = new HashSet<>();
		Variant variant = Variant.builder()
			.variantId(1L)
			.price(new BigDecimal("10.00"))
			.type("chocolate")
			.build();
		variants.add(variant);

		product = Product.builder()
			.productId(1L)
			.name("Test Product")
			.tagline("Test Tagline")
			.description("Test Description")
			.category(Product.Category.CAKE)
			.variants(variants)
			.build();

		productDto = ProductDto.builder()
			.name("Test Product")
			.tagline("Test Tagline")
			.description("Test Description")
			.category(Product.Category.fromString("cake"))
			.build();

		variantPostDtos = List.of(
			VariantPostDto.builder()
				.price(new BigDecimal("10.00"))
				.type("chocolate")
				.minQuantity(1)
				.maxQuantity(10)
				.build()
		);

		productPostDto = ProductPostDto.builder()
			.name("Test Product")
			.tagline("Test Tagline")
			.description("Test Description")
			.category("cake")
			.variants(variantPostDtos)
			.build();
	}

	@Test
	void getAllProducts_ShouldReturnListOfProducts() {
		// Arrange
		List<Product> products = List.of(product);
		List<ProductDto> expectedDtos = List.of(productDto);
		when(productRepository.findAll()).thenReturn(products);
		when(productMapper.fromEntity(product)).thenReturn(productDto);

		// Act
		List<ProductDto> result = productService.getAllProducts(null, null);

		// Assert
		assertThat(result).isEqualTo(expectedDtos);
		verify(productRepository).findAll();
		verify(productMapper).fromEntity(product);
	}

	@Test
	void getAllProductsPaginated_ShouldReturnPaginatedListOfProducts() {
		// Arrange
		PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(Sort.Order.asc("productId")));

		List<Product> products = List.of(product);
		Page<Product> productPage = new PageImpl<>(products, pageRequest, products.size());

		// Use any() for the Specification
		when(productRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(productPage);
		when(productMapper.fromEntity(product)).thenReturn(productDto);

		// Act
		Page<ProductDto> result = productService.getAllProductsPaginated(null, null, null, 1, 5);

		// Assert
		assertThat(result.getNumber()).isEqualTo(0);
		assertThat(result.getSize()).isEqualTo(5);
		verify(productRepository).findAll(any(Specification.class), eq(pageRequest));
	}

	@Test
	void createProduct_WithValidData_ShouldReturnProductDto() {
		// Arrange
		when(productRepository.save(any(Product.class))).thenReturn(product);
		when(productMapper.fromEntity(product)).thenReturn(productDto);

		// Act
		ProductDto result = productService.createProduct(productPostDto);

		// Assert
		assertThat(result).isEqualTo(productDto);
		verify(productRepository).save(any(Product.class));
		verify(productMapper).fromEntity(product);
	}

	@Test
	void createProduct_WithNullProductPostDto_ShouldThrowResourceUploadException() {
		// Act & Assert
		assertThatThrownBy(() -> productService.createProduct(null))
			.isInstanceOf(InvalidRequestBodyValue.class)
			.hasMessage("ProductPostDto cannot be null");
	}

	@Test
	void createProduct_WithNullVariants_ShouldThrowResourceUploadException() {
		// Arrange
		productPostDto.setVariants(null);

		// Act & Assert
		assertThatThrownBy(() -> productService.createProduct(productPostDto))
			.isInstanceOf(InvalidRequestBodyValue.class)
			.hasMessage("Variants must not be empty");
	}

	@Test
	void createProduct_WithEmptyVariants_ShouldThrowIllegalArgumentException() {
		// Arrange
		productPostDto.setVariants(new ArrayList<>());

		// Act & Assert
		assertThatThrownBy(() -> productService.createProduct(productPostDto))
			.isInstanceOf(InvalidRequestBodyValue.class)
			.hasMessage("Variants must not be empty");
	}

	@Test
	void getProduct_WithExistingId_ShouldReturnProductDto() {
		// Arrange
		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(productMapper.fromEntity(product)).thenReturn(productDto);

		// Act
		ProductDto result = productService.getProduct(1L);

		// Assert
		assertThat(result).isEqualTo(productDto);
		verify(productRepository).findById(1L);
		verify(productMapper).fromEntity(product);
	}

	@Test
	void getProduct_WithNonExistingId_ShouldThrowResourceNotFoundException() {
		// Arrange
		when(productRepository.findById(1L)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> productService.getProduct(1L))
			.isInstanceOf(ResourceNotFoundException.class)
			.hasMessage("Product with ID '1' not found");
	}

	@Test
	void getAllProductByKeyword_ShouldReturnMatchingProducts() {
		// Arrange
		String keyword = "Test";
		List<Product> products = List.of(product);
		List<ProductDto> expectedDtos = List.of(productDto);
		when(productRepository.findAllByNameLikeIgnoreCase(contains(keyword))).thenReturn(products);
		when(productMapper.fromEntity(product)).thenReturn(productDto);

		// Act
		List<ProductDto> result = productService.getAllProducts(null, keyword);

		// Assert
		assertThat(result).isEqualTo(expectedDtos);
		verify(productRepository).findAllByNameLikeIgnoreCase(contains(keyword));
		verify(productMapper).fromEntity(product);
	}

	@Test
	void getProductsByCategory_WithValidCategory_ShouldReturnProducts() {
		// Arrange
		Product.Category category = Product.Category.CAKE;
		List<Product> products = List.of(product);
		List<ProductDto> expectedDtos = List.of(productDto);
		when(productRepository.findAllByCategory(category)).thenReturn(products);
		when(productMapper.fromEntity(product)).thenReturn(productDto);

		// Act
		List<ProductDto> result = productService.getAllProducts(category.toString(), null);

		// Assert
		assertThat(result).isEqualTo(expectedDtos);
		verify(productRepository).findAllByCategory(Product.Category.CAKE);
		verify(productMapper).fromEntity(product);
	}

	@Test
	void getProductsByCategory_WithInvalidCategory_ShouldThrowInvalidRequestBodyValue() {
		// Arrange
		String invalidCategory = "invalid";

		// Act & Assert
		assertThatThrownBy(() -> productService.getAllProducts(invalidCategory, null))
			.isInstanceOf(InvalidRequestBodyValue.class)
			.hasMessage("Invalid category: INVALID");
	}

	@Test
	@Disabled
	void hardDeleteProduct_WithExistingId_ShouldHardDeleteProduct() {
		// Arrange
		when(productRepository.existsById(1L)).thenReturn(true);

		// Act
		productService.hardDeleteProduct(1L);

		// Assert
		verify(productRepository).existsById(1L);
		verify(productRepository).deleteById(1L);
	}

	@Test
	@Disabled
	void hardDeleteProduct_WithNonExistingId_ShouldThrowResourceNotFoundException() {
		// Arrange
		when(productRepository.existsById(1L)).thenReturn(false);

		// Act & Assert
		assertThatThrownBy(() -> productService.hardDeleteProduct(1L))
			.isInstanceOf(ResourceNotFoundException.class)
			.hasMessage("Product with ID '1' not found");
	}

	@Test
	void softDeleteProduct_WithExistingId_ShouldSoftDeleteProduct() {
		// Arrange
		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(productRepository.save(product)).thenReturn(product);

		// Act
		productService.softDeleteProduct(1L);

		// Assert
		verify(productRepository).findById(1L);
		verify(productRepository).save(product);
		assertThat(product.isDeleted()).isTrue();
	}

	@Test
	void softDeleteProduct_WithNonExistingId_ShouldThrowResourceNotFoundException() {
		// Arrange
		when(productRepository.findById(1L)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> productService.softDeleteProduct(1L))
			.isInstanceOf(ResourceNotFoundException.class)
			.hasMessage("Product with ID '1' not found");
	}

	@Test
	void patchProduct_WithExistingId_ShouldReturnUpdatedProductDto() {
		// Arrange
		ProductPatchDto patchDto = new ProductPatchDto();
		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(productMapper.updateProductFromPatch(patchDto, product)).thenReturn(product);
		when(productRepository.save(product)).thenReturn(product);
		when(productMapper.fromEntity(product)).thenReturn(productDto);

		// Act
		ProductDto result = productService.patchProduct(1L, patchDto);

		// Assert
		assertThat(result).isEqualTo(productDto);
		verify(productRepository).findById(1L);
		verify(productMapper).updateProductFromPatch(patchDto, product);
		verify(productRepository).save(product);
		verify(productMapper).fromEntity(product);
	}

	@Test
	void patchProduct_WithNonExistingId_ShouldThrowResourceNotFoundException() {
		// Arrange
		ProductPatchDto patchDto = new ProductPatchDto();
		when(productRepository.findById(1L)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> productService.patchProduct(1L, patchDto))
			.isInstanceOf(ResourceNotFoundException.class)
			.hasMessage("Product with ID '1' not found");
	}

	@Test
	void existsById_WithExistingId_ShouldReturnTrue() {
		// Arrange
		when(productRepository.existsById(1L)).thenReturn(true);

		// Act
		boolean result = productService.existsById(1L);

		// Assert
		assertThat(result).isTrue();
		verify(productRepository).existsById(1L);
	}

	@Test
	void existsById_WithNonExistingId_ShouldReturnFalse() {
		// Arrange
		when(productRepository.existsById(1L)).thenReturn(false);

		// Act
		boolean result = productService.existsById(1L);

		// Assert
		assertThat(result).isFalse();
		verify(productRepository).existsById(1L);
	}
}