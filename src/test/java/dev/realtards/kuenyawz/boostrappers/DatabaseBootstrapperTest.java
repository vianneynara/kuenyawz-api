package dev.realtards.kuenyawz.boostrappers;

import dev.realtards.kuenyawz.repositories.ProductRepository;
import dev.realtards.kuenyawz.services.AccountService;
import dev.realtards.kuenyawz.services.ProductCsvService;
import dev.realtards.kuenyawz.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class DatabaseBootstrapperTest {

	@Autowired
	AccountService accountService;

	@Autowired
	ProductService productService;

	@Autowired
	ProductCsvService productCsvService;

	@Autowired
	ProductRepository productRepository;

	DatabaseBootstrapper databaseBootstrapper;

	@BeforeEach
	void setUp() {
		databaseBootstrapper = new DatabaseBootstrapper(accountService, productCsvService, productRepository);
	}

	@Test
	void testOnApplicationEvent() {
		assertDoesNotThrow(() -> databaseBootstrapper.onApplicationEvent(any(ApplicationReadyEvent.class)));

		assertThat(productRepository.count()).isGreaterThan(1);
	}
}