package dev.kons.kuenyawz.boostrappers;

import dev.kons.kuenyawz.configurations.ApplicationProperties;
import dev.kons.kuenyawz.repositories.AccountRepository;
import dev.kons.kuenyawz.repositories.ProductRepository;
import dev.kons.kuenyawz.services.logic.AccountCsvService;
import dev.kons.kuenyawz.services.logic.ProductCsvService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

	@BeforeEach
	void setUp() {
		databaseBootstrapper = new DatabaseBootstrapper(properties, productCsvService, productRepository, accountCsvService, accountRepository);
	}

	@Test
	void testOnApplicationEvent() {
		assertDoesNotThrow(() -> databaseBootstrapper.run());
		assertThat(productRepository.count()).isGreaterThan(1);
	}
}