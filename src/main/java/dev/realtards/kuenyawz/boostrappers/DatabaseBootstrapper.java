package dev.realtards.kuenyawz.boostrappers;

import dev.realtards.kuenyawz.dtos.account.AccountRegistrationDto;
import dev.realtards.kuenyawz.dtos.account.PrivilegeUpdateDto;
import dev.realtards.kuenyawz.entities.Account;
import dev.realtards.kuenyawz.exceptions.AccountExistsException;
import dev.realtards.kuenyawz.repositories.ProductRepository;
import dev.realtards.kuenyawz.services.entity.AccountService;
import dev.realtards.kuenyawz.services.logic.ProductCsvService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseBootstrapper implements ApplicationListener<ApplicationReadyEvent>, CommandLineRunner {

	private final AccountService accountService;
	private final ProductCsvService productCsvService;
	private final ProductRepository productRepository;

	private static final String PATH_TO_PRODUCT_SEEDER = "seeders/Products.csv";

	private final List<AccountRegistrationDto> BOOTSTRAP_ACCOUNTS = List.of(
		AccountRegistrationDto.builder()
			.password("testadmin")
			.fullName("Test Admin")
			.phone("81100001")
			.build(),
		AccountRegistrationDto.builder()
			.password("user")
			.fullName("Nara")
			.phone("81100002")
			.build(),
		AccountRegistrationDto.builder()
			.password("user")
			.fullName("Emilia")
			.phone("81100003")
			.build(),
		AccountRegistrationDto.builder()
			.password("user")
			.fullName("Emilia")
			.phone("81100004")
			.build(),
		AccountRegistrationDto.builder()
			.password("user")
			.fullName("Bruh")
			.phone("81100005")
			.build()
	);

	public void injectAccounts() {
		final ListIterator<AccountRegistrationDto> iterator = BOOTSTRAP_ACCOUNTS.listIterator();

		while (iterator.hasNext()) {
			try {
				if (!iterator.hasPrevious()) {
					Account account = accountService.createAccount(iterator.next());
					accountService.updatePrivilege(account.getAccountId(), new PrivilegeUpdateDto(Account.Privilege.ADMIN));
				} else {
					accountService.createAccount(iterator.next());
				}
			} catch (AccountExistsException e) {
				log.warn("Account already exists: {}", iterator.previous().getPhone());
				iterator.next();
			}
		}

		log.info("Database has {} accounts", accountService.getAllAccounts().size());
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
