package dev.kons.kuenyawz.services;

import dev.kons.kuenyawz.repositories.ProductRepository;
import dev.kons.kuenyawz.services.logic.ProductCsvService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductCsvServiceImplTest {

	@Autowired
	ProductCsvService productCsvService;

	@Autowired
	ProductRepository productRepository;

	@BeforeEach
	void setUp() {
		productRepository.deleteAll();
		productRepository.flush();
	}

	@Test
	void testCsvToProductCsvRecord() throws FileNotFoundException {
		File file = ResourceUtils.getFile("classpath:seeders/Products.csv");

		// Act
		productCsvService.saveProductFromFile(file);

		// Assertions
		assertThat(productRepository.count()).isGreaterThan(0);
	}
}