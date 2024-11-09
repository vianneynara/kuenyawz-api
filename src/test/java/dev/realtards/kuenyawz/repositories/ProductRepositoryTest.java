package dev.realtards.kuenyawz.repositories;

import dev.realtards.kuenyawz.entities.Product;
import dev.realtards.kuenyawz.entities.Variant;
import dev.realtards.kuenyawz.services.ProductCsvImportServiceImpl;
import dev.realtards.kuenyawz.services.ProductService;
import dev.realtards.kuenyawz.utils.parser.CSVParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest
public class ProductRepositoryTest {

	@Autowired
	private ProductRepository productRepository;

	@MockBean
	private ProductService productService;

	@MockBean
	private ProductCsvImportServiceImpl productCsvImportService;

	@BeforeEach
	void setUp() {
		productRepository.deleteAll();
		productRepository.flush();

		CSVParser csvParser = new CSVParser(productService, productCsvImportService);
		csvParser.saveProductsFromCsv("seeders/ProductsSeeder.csv");
	}

	@Test
	void testFindAll() {
		List<Product> productList = productRepository.findAll();

		// Assertions
		assertThat(productList.size()).isEqualTo(45);
	}


	@Test
	void testFindAllByCategoryIs() {
		List<Product> productList = productRepository.findAllByCategoryIs(Product.Category.CAKE);

		// Assertions
		assertThat(productList.size()).isEqualTo(20);
	}

	@Test
	void testSaveProduct() {
		Product product = Product.builder()
			.name("Test Product")
			.tagline("Test Tagline")
			.description("Test Description")
			.category(Product.Category.CAKE)
			.variants(Set.of(
				Variant.builder()
					.type("Test Variant")
					.price(BigDecimal.valueOf(1000.0))
					.build()
			))
			.build();

		Product savedProduct = productRepository.save(product);

		// Assertions
		assertThat(savedProduct.getProductId()).isNotNull();
		assertThat(savedProduct.getName()).isEqualTo(product.getName());
		assertThat(savedProduct.getTagline()).isEqualTo(product.getTagline());
		assertThat(savedProduct.getCategory()).isEqualTo(product.getCategory());
	}

}
