package dev.realtards.kuenyawz.repositories;

import dev.realtards.kuenyawz.boostrappers.DatabaseBootstrapper;
import dev.realtards.kuenyawz.configurations.ApplicationProperties;
import dev.realtards.kuenyawz.entities.Product;
import dev.realtards.kuenyawz.entities.Variant;
import dev.realtards.kuenyawz.services.entity.AccountService;
import dev.realtards.kuenyawz.services.logic.ProductCsvServiceImpl;
import dev.realtards.kuenyawz.services.entity.ProductService;
import dev.realtards.kuenyawz.utils.idgenerator.SnowFlakeIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Import({DatabaseBootstrapper.class})
public class ProductRepositoryTest {

	@Autowired
	ProductRepository productRepository;

	@Autowired
	ProductService productService;

	@Autowired
	AccountService accountService;

	@Autowired
	ProductCsvServiceImpl productCsvImportService;

	@Autowired
	ApplicationProperties applicationProperties;

	@BeforeEach
	void setUp() {
		productCsvImportService = new ProductCsvServiceImpl(productService);

		productRepository.deleteAll();
		productRepository.flush();

		try {
			productCsvImportService.saveProductFromFile(new ClassPathResource("seeders/Products.csv").getFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void testFindAll() {
		List<Product> productList = productRepository.findAll();

		// Assertions
		assertThat(productList.size()).isEqualTo(45);
	}


	@Test
	void testFindAllByCategory() {
		List<Product> productList = productRepository.findAllByCategory(Product.Category.CAKE);

		// Assertions
		assertThat(productList.size()).isEqualTo(20);
	}

	@Test
	@Disabled
	void testSaveProduct() {
		Product product = Product.builder()
			.productId((new SnowFlakeIdGenerator()).generateId())
			.name("Test Product")
			.tagline("Test Tagline")
			.description("Test Description")
			.category(Product.Category.CAKE)
			.version(0L)
			.build();

		Variant variant = Variant.builder()
			.type("Test Variant")
			.price(BigDecimal.valueOf(1000.0))
			.product(product)
			.build();

		product.setVariants(Set.of(variant));

		Product savedProduct = productRepository.save(product);

		// Assertions
		assertThat(savedProduct.getProductId()).isNotNull();
		assertThat(savedProduct.getName()).isEqualTo(product.getName());
		assertThat(savedProduct.getTagline()).isEqualTo(product.getTagline());
		assertThat(savedProduct.getCategory()).isEqualTo(product.getCategory());
	}
}
