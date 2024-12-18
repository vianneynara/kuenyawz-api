package dev.kons.kuenyawz.boostrappers;

import dev.kons.kuenyawz.configurations.ApplicationProperties;
import dev.kons.kuenyawz.dtos.account.AccountRegistrationDto;
import dev.kons.kuenyawz.entities.Account;
import dev.kons.kuenyawz.repositories.AccountRepository;
import dev.kons.kuenyawz.repositories.ProductRepository;
import dev.kons.kuenyawz.services.entity.AccountService;
import dev.kons.kuenyawz.services.logic.AccountCsvService;
import dev.kons.kuenyawz.services.logic.ProductCsvService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseBootstrapper implements CommandLineRunner {

	private final ApplicationProperties properties;
	private final ProductCsvService productCsvService;
	private final ProductRepository productRepository;
	private final AccountCsvService accountCsvService;
	private final AccountRepository accountRepository;
	private final AccountService accountService;

	private static final String PATH_TO_PRODUCT_SEEDER = "seeders/Products.csv";
	private static final String PATH_TO_ACCOUNT_SEEDER = "seeders/Accounts.csv";

	public void injectAccounts() {
		if (accountRepository.count() >= 5) {
			return;
		}

		try {
			ClassPathResource resource = new ClassPathResource(PATH_TO_ACCOUNT_SEEDER);
			accountCsvService.saveAccountFromStream(resource.getInputStream());
		} catch (IOException e) {
			log.error("File not found: {}", e.getMessage());
		}

		// Set user with the first id as admin
		accountRepository.findFirstByPrivilegeOrderByAccountIdAsc(Account.Privilege.USER).ifPresent(account -> {
			account.setPrivilege(Account.Privilege.ADMIN);
			accountRepository.save(account);
		});
	}

	public void injectProducts() {
		// Change this according to the number of seeds in product CSV
		if (productRepository.count() >= 45) {
			return;
		}

		// Check if path and the file exists
		try {
			ClassPathResource resource = new ClassPathResource(PATH_TO_PRODUCT_SEEDER);
			productCsvService.saveProductFromStream(resource.getInputStream());
		} catch (IOException e) {
			log.error("File not found: {}", e.getMessage());
		}
	}

	@Override
	public void run(String... args) throws Exception {
		start();
	}

	private void start() {
		if (properties.seeder().getSeedAccounts()) {
			log.info("Injecting accounts...");
			injectAccounts();
		} else {
			log.info("Injecting an admin account...");
			final AccountRegistrationDto accountRegistrationDto = AccountRegistrationDto.builder()
				.fullName("Admin")
				.phone("81100001")
				.password("admin")
				.build();
			try {
				accountService.createAccount(accountRegistrationDto);
				log.info("Admin account created (id: {})", accountRegistrationDto.getPhone());
			} catch (Exception e) {
				log.error("Admin account already exists (id: {})", accountRegistrationDto.getPhone());
			}
		}

		if (properties.seeder().getSeedProducts()) {
			log.info("Injecting products...");
			injectProducts();
		}
	}
}
