package dev.kons.kuenyawz.boostrappers;

import dev.kons.kuenyawz.configurations.ApplicationProperties;
import dev.kons.kuenyawz.dtos.product.ProductPostDto;
import dev.kons.kuenyawz.dtos.product.VariantPostDto;
import dev.kons.kuenyawz.repositories.AccountRepository;
import dev.kons.kuenyawz.repositories.ProductRepository;
import dev.kons.kuenyawz.services.entity.ProductService;
import dev.kons.kuenyawz.services.logic.AccountCsvService;
import dev.kons.kuenyawz.services.logic.ProductCsvService;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class DatabaseBootstrapperTest {

	@Autowired
	ApplicationProperties properties;

	@Autowired
	ProductCsvService productCsvService;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	private AccountCsvService accountCsvService;

	@Autowired
	private AccountRepository accountRepository;

	DatabaseBootstrapper databaseBootstrapper;
	@Autowired
	private ProductService productService;

	@BeforeEach
	void setUp() {
		databaseBootstrapper = new DatabaseBootstrapper(properties, productCsvService, productRepository, accountCsvService, accountRepository);
	}

	@Test
	void testOnApplicationEvent() {
		assertDoesNotThrow(() -> databaseBootstrapper.run());

		insertNewProduct("Test Product1");
		insertNewProduct("Test Product2");

		assertThat(productRepository.count()).isGreaterThan(1);
	}

	void insertNewProduct(@NotNull String name) {
		ProductPostDto productPostDto = ProductPostDto.builder()
			.name(name)
			.tagline("Test Tagline")
			.description("Test Description")
			.category("cake")
			.build();

		List<VariantPostDto> variantPostDtos = new ArrayList<>(
			List.of(
				VariantPostDto.builder()
					.price(new BigDecimal("10000.00"))
					.type("Test Type")
					.minQuantity(1)
					.maxQuantity(10)
					.build()
			)
		);
		productPostDto.setVariants(variantPostDtos);

		productService.createProduct(productPostDto);
	}
}