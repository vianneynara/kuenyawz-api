package dev.kons.kuenyawz.boostrappers;

import dev.kons.kuenyawz.repositories.AccountRepository;
import dev.kons.kuenyawz.repositories.ProductRepository;
import dev.kons.kuenyawz.services.entity.AccountService;
import dev.kons.kuenyawz.services.logic.AccountCsvService;
import dev.kons.kuenyawz.services.logic.ProductCsvService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseBootstrapper implements ApplicationListener<ApplicationReadyEvent>, CommandLineRunner {

	private final AccountService accountService;
	private final ProductCsvService productCsvService;
	private final ProductRepository productRepository;

	private static final String PATH_TO_PRODUCT_SEEDER = "seeders/Products.csv";
	private static final String PATH_TO_ACCOUNT_SEEDER = "seeders/Accounts.csv";

	private final AccountRepository accountRepository;
	private final AccountCsvService accountCsvService;

	public void injectAccounts() {
		if (accountRepository.count() >= 5) {
			return;
		}

		try {
			ClassPathResource resource = new ClassPathResource(PATH_TO_ACCOUNT_SEEDER);

			File file = resource.getFile();
			accountCsvService.saveAccountFromFile(file);
		} catch (IOException e) {
			log.error("File not found: {}", e.getMessage());
		}
	}

	public void injectProducts() {
		// Change this according to the number of seeds in product CSV
		if (productRepository.count() >= 45) {
			return;
		}

		// Check if path and the file exists
		try {
			ClassPathResource resource = new ClassPathResource(PATH_TO_PRODUCT_SEEDER);

			File file = resource.getFile();
			productCsvService.saveProductFromFile(file);
		} catch (IOException e) {
			log.error("File not found: {}", e.getMessage());
		}
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
//		start();
	}

	@Override
	public void run(String... args) throws Exception {
		start();
	}

	private void start() {
		log.info("Bootstrapping database...");
		injectAccounts();
		injectProducts();
		log.info("Database bootstrapping complete");
	}
}
