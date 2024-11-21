package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.dtos.product.VariantDto;
import dev.realtards.kuenyawz.dtos.product.VariantPatchDto;
import dev.realtards.kuenyawz.dtos.product.VariantPostDto;
import dev.realtards.kuenyawz.entities.Product;
import dev.realtards.kuenyawz.entities.Variant;
import dev.realtards.kuenyawz.exceptions.IllegalOperationException;
import dev.realtards.kuenyawz.exceptions.ResourceNotFoundException;
import dev.realtards.kuenyawz.mapper.VariantMapper;
import dev.realtards.kuenyawz.repositories.ProductRepository;
import dev.realtards.kuenyawz.repositories.VariantRepository;
import dev.realtards.kuenyawz.services.entity.VariantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class VariantServiceImplTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private VariantRepository variantRepository;

	@Mock
	private VariantMapper variantMapper;

	@InjectMocks
	private VariantServiceImpl variantService;

	private Product product;
	private Variant variant1;
	private Variant variant2;
	private VariantDto variantDto;
	private VariantPostDto variantPostDto;
	private Set<Variant> variants;

	@BeforeEach
	void setUp() {
		productRepository.deleteAll();

		// Common test data setup
		product = Product.builder()
			.productId(1L)
			.name("Test Product")
			.tagline("Test Tagline")
			.description("Test Description")
			.category(Product.Category.CAKE)
			.build();

		variant1 = Variant.builder()
			.variantId(1L)
			.price(new BigDecimal("10.00"))
			.type("chocolate")
			.product(product)
			.build();

		variant2 = Variant.builder()
			.variantId(2L)
			.price(new BigDecimal("10.00"))
			.type("chocolate")
			.product(product)
			.build();

		variantDto = VariantDto.builder()
			.variantId(1L)
			.price(new BigDecimal("10.00"))
			.type("chocolate")
			.build();

		variantPostDto = VariantPostDto.builder()
			.price(new BigDecimal("10.00"))
			.type("chocolate")
			.build();

		variants = new HashSet<>();
		variants.add(variant1);
		variants.add(variant2);
		product.setVariants(variants);

		productRepository.save(product);
	}

	@Test
	void getAllVariants_ShouldReturnListOfVariants() {
		// Arrange
		List<Variant> variantList = List.of(variant1);
		List<VariantDto> expectedDtos = List.of(variantDto);
		when(variantRepository.findAll()).thenReturn(variantList);
		when(variantMapper.fromEntity(variant1)).thenReturn(variantDto);

		// Act
		List<VariantDto> result = variantService.getAllVariants();

		// Assert
		assertThat(result).isEqualTo(expectedDtos);
		verify(variantRepository).findAll();
		verify(variantMapper).fromEntity(variant1);
	}

	@Test
	void createVariant_WithValidData_ShouldReturnVariantDto() {
		// Arrange
		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(variantRepository.save(any(Variant.class))).thenReturn(variant1);
		when(variantMapper.fromEntity(variant1)).thenReturn(variantDto);

		// Act
		VariantDto result = variantService.createVariant(1L, variantPostDto);

		// Assert
		assertThat(result).isEqualTo(variantDto);
		verify(productRepository).findById(1L);
		verify(variantRepository).save(any(Variant.class));
		verify(variantMapper).fromEntity(variant1);
	}

	@Test
	void createVariant_WithNonExistingProduct_ShouldThrowResourceNotFoundException() {
		// Arrange
		when(productRepository.findById(1L)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> variantService.createVariant(1L, variantPostDto))
			.isInstanceOf(ResourceNotFoundException.class)
			.hasMessage("Product with ID '1' not found");
	}

	@Test
	void createVariants_WithValidData_ShouldReturnVariantDtoList() {
		// Arrange
		VariantPostDto[] postDtos = {variantPostDto, variantPostDto};
		List<Variant> savedVariants = List.of(variant1, variant1);
		List<VariantDto> expectedDtos = List.of(variantDto, variantDto);

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(variantRepository.saveAll(any())).thenReturn(savedVariants);
		when(variantMapper.fromEntity(variant1)).thenReturn(variantDto);

		// Act
		List<VariantDto> result = variantService.createVariants(1L, postDtos);

		// Assert
		assertThat(result).isEqualTo(expectedDtos);
		verify(productRepository).findById(1L);
		verify(variantRepository).saveAll(any());
		verify(variantMapper, times(2)).fromEntity(variant1);
	}

	@Test
	void createVariants_WithNonExistingProduct_ShouldThrowResourceNotFoundException() {
		// Arrange
		VariantPostDto[] postDtos = {variantPostDto};
		when(productRepository.findById(1L)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> variantService.createVariants(1L, postDtos))
			.isInstanceOf(ResourceNotFoundException.class)
			.hasMessage("Product with ID '1' not found");
	}

	@Test
	void getVariant_WithExistingId_ShouldReturnVariantDto() {
		// Arrange
		when(variantRepository.findById(1L)).thenReturn(Optional.of(variant1));
		when(variantMapper.fromEntity(variant1)).thenReturn(variantDto);

		// Act
		VariantDto result = variantService.getVariant(1L);

		// Assert
		assertThat(result).isEqualTo(variantDto);
		verify(variantRepository).findById(1L);
		verify(variantMapper).fromEntity(variant1);
	}

	@Test
	void getVariant_WithNonExistingId_ShouldThrowResourceNotFoundException() {
		// Arrange
		when(variantRepository.findById(1L)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> variantService.getVariant(1L))
			.isInstanceOf(ResourceNotFoundException.class)
			.hasMessage("Variant with ID '1' not found");
	}

	@Test
	void getVariantsOfProductId_ShouldReturnVariantDtoList() {
		// Arrange
		List<Variant> variantList = List.of(variant1);
		List<VariantDto> expectedDtos = List.of(variantDto);
		when(variantRepository.findAllByProduct_ProductId(1L)).thenReturn(variantList);
		when(variantMapper.fromEntity(variant1)).thenReturn(variantDto);

		// Act
		List<VariantDto> result = variantService.getVariantsOfProductId(1L);

		// Assert
		assertThat(result).isEqualTo(expectedDtos);
		verify(variantRepository).findAllByProduct_ProductId(1L);
		verify(variantMapper).fromEntity(variant1);
	}

	@Test
	void patchVariant_WithValidData_ShouldReturnUpdatedVariantDto() {
		// Arrange
		VariantPatchDto patchDto = new VariantPatchDto();
		patchDto.setMinQuantity(5);
		patchDto.setMaxQuantity(10);

		Variant variant1 = Variant.builder()
			.minQuantity(1)
			.maxQuantity(10)
			.build();

		when(productRepository.existsById(1L)).thenReturn(true);
		when(variantRepository.findById(1L)).thenReturn(Optional.of(variant1));
		when(variantMapper.updateVariantFromPatch(patchDto, variant1)).thenReturn(variant1);
		when(variantRepository.save(variant1)).thenReturn(variant1);
		when(variantMapper.fromEntity(variant1)).thenReturn(variantDto);

		// Act
		VariantDto result = variantService.patchVariant(1L, 1L, patchDto);

		// Assert
		assertThat(result).isEqualTo(variantDto);
		verify(productRepository).existsById(1L);
		verify(variantRepository).findById(1L);
		verify(variantMapper).updateVariantFromPatch(patchDto, variant1);
		verify(variantRepository).save(variant1);
		verify(variantMapper).fromEntity(variant1);
	}

	@Test
	void patchVariant_WithNonExistingProduct_ShouldThrowResourceNotFoundException() {
		// Arrange
		VariantPatchDto patchDto = new VariantPatchDto();
		when(productRepository.existsById(1L)).thenReturn(false);

		// Act & Assert
		assertThatThrownBy(() -> variantService.patchVariant(1L, 1L, patchDto))
			.isInstanceOf(ResourceNotFoundException.class)
			.hasMessage("Product with ID '1' not found");
	}

	@Test
	void patchVariant_WithNonExistingVariant_ShouldThrowResourceNotFoundException() {
		// Arrange
		VariantPatchDto patchDto = new VariantPatchDto();
		when(productRepository.existsById(1L)).thenReturn(true);
		when(variantRepository.findById(1L)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> variantService.patchVariant(1L, 1L, patchDto))
			.isInstanceOf(ResourceNotFoundException.class)
			.hasMessage("Variant with ID '1' not found");
	}

	@Test
	void deleteVariant_WithMultipleVariants_ShouldDeleteVariant() {
		// Arrange
		when(variantRepository.countVariantsByProduct_ProductId(1L)).thenReturn(2);
		when(variantRepository.deleteByVariantIdAndProduct_ProductId(1L, 1L)).thenReturn(1);

		// Act
		variantService.deleteVariant(1L, 1L);

		// Assert
		verify(variantRepository).countVariantsByProduct_ProductId(1L);
		verify(variantRepository).deleteByVariantIdAndProduct_ProductId(1L, 1L);
	}

	@Test
	void deleteVariant_WithSingleVariant_ShouldThrowIllegalOperationException() {
		// Arrange
		when(variantRepository.countVariantsByProduct_ProductId(1L)).thenReturn(1);

		// Act & Assert
		assertThatThrownBy(() -> variantService.deleteVariant(1L, 1L))
			.isInstanceOf(IllegalOperationException.class)
			.hasMessage("Cannot delete the last variant of a product");
	}

	@Test
	void deleteVariant_WithNonExistingVariant_ShouldThrowResourceNotFoundException() {
		// Arrange
		when(variantRepository.countVariantsByProduct_ProductId(1L)).thenReturn(2);
		when(variantRepository.deleteByVariantIdAndProduct_ProductId(1L, 1L)).thenReturn(0);

		// Act & Assert
		assertThatThrownBy(() -> variantService.deleteVariant(1L, 1L))
			.isInstanceOf(ResourceNotFoundException.class)
			.hasMessage("Variant with ID '1' not found in Product with ID '1'");
	}
}