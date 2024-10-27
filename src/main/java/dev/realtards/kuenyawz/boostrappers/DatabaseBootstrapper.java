package dev.realtards.kuenyawz.boostrappers;

import dev.realtards.kuenyawz.dtos.account.AccountRegistrationDto;
import dev.realtards.kuenyawz.dtos.account.PrivilegeUpdateDto;
import dev.realtards.kuenyawz.dtos.product.ProductDto;
import dev.realtards.kuenyawz.dtos.product.ProductPostDto;
import dev.realtards.kuenyawz.dtos.product.VariantPostDto;
import dev.realtards.kuenyawz.exceptions.AccountExistsException;
import dev.realtards.kuenyawz.entities.Account;
import dev.realtards.kuenyawz.services.AccountService;
import dev.realtards.kuenyawz.services.ProductService;
import dev.realtards.kuenyawz.services.VariantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.ListIterator;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseBootstrapper implements CommandLineRunner {

	private final AccountService accountService;
	private final ProductService productService;
	private final VariantService variantService;

	private final List<AccountRegistrationDto> BOOTSTRAP_ACCOUNTS = List.of(
		AccountRegistrationDto.builder()
			.password("testadmin")
			.fullName("Test Admin")
			.email("root@wz.com")
			.build(),
		AccountRegistrationDto.builder()
			.password("user")
			.fullName("Nara")
			.email("nara@example.com")
			.build(),
		AccountRegistrationDto.builder()
			.password("user")
			.fullName("Emilia")
			.email("emilia@example.com")
			.build(),
		AccountRegistrationDto.builder()
			.password("user")
			.fullName("Emilia")
			.email("emilia@example.com")
			.build(),
		AccountRegistrationDto.builder()
			.password("user")
			.fullName("Bruh")
			.email("bruh@example.com")
			.build()
	);

	private final List<ProductPostDto> BOOTSTRAP_PRODUCTS = List.of(
		ProductPostDto.builder()
			.name("Chocolate Muffin")
			.tagline("Rich and moist chocolate delight")
			.description("Our signature chocolate muffin made with premium cocoa powder and chocolate chips")
			.category("cake")
			.minQuantity(1)
			.maxQuantity(24)
			.variants(List.of(
				VariantPostDto.builder()
					.type("Regular")
					.price(new BigDecimal("15000.0"))
					.build(),
				VariantPostDto.builder()
					.type("Large")
					.price(new BigDecimal("25000.0"))
					.build()
			))
			.build(),
		ProductPostDto.builder()
			.name("Croissant")
			.tagline("Authentic French pastry")
			.description("Buttery, flaky, and golden-brown croissants made with imported French butter")
			.category("pastry")
			.minQuantity(1)
			.maxQuantity(30)
			.variants(List.of(
				VariantPostDto.builder()
					.type("Plain")
					.price(new BigDecimal("18000.0"))
					.build(),
				VariantPostDto.builder()
					.type("Chocolate")
					.price(new BigDecimal("22000.0"))
					.build(),
				VariantPostDto.builder()
					.type("Almond")
					.price(new BigDecimal("25000.0"))
					.build()
			))
			.build(),
		ProductPostDto.builder()
			.name("Sourdough Bread")
			.tagline("Artisanal naturally leavened bread")
			.description("Traditional sourdough bread made with our 5-year-old starter")
			.category("other")
			.minQuantity(1)
			.maxQuantity(5)
			.variants(List.of(
				VariantPostDto.builder()
					.type("Whole")
					.price(new BigDecimal("45000.0"))
					.build(),
				VariantPostDto.builder()
					.type("Half")
					.price(new BigDecimal("25000.0"))
					.build()
			))
			.build(),
		ProductPostDto.builder()
			.name("Fettuccine")
			.tagline("Fresh homemade pasta")
			.description("Handcrafted fresh pasta made daily with premium durum wheat")
			.category("pasta")
			.minQuantity(1)
			.maxQuantity(10)
			.variants(List.of(
				VariantPostDto.builder()
					.type("Regular (250g)")
					.price(new BigDecimal("35000.0"))
					.build(),
				VariantPostDto.builder()
					.type("Family Pack (500g)")
					.price(new BigDecimal("65000.0"))
					.build()
			))
			.build()
	);

	private void injectAccounts() {
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
				log.warn("Account already exists: {}", iterator.previous().getEmail());
				iterator.next();
			}
		}
	}

	private void injectProducts() {
		final ListIterator<ProductPostDto> iterator = BOOTSTRAP_PRODUCTS.listIterator();

		while (iterator.hasNext()) {
			ProductPostDto product = iterator.next();
			try {
				// Check if product already exists by searching for exact name
				List<ProductDto> existingProducts = productService.getAllProductByKeyword(product.getName());
				boolean exists = existingProducts.stream()
					.anyMatch(p -> p.getName().equals(product.getName()));

				if (!exists) {
					ProductDto createdProduct = productService.createProduct(product);
					log.info("Created product: {}", createdProduct.getName());
				} else {
					log.warn("Product already exists: {}", product.getName());
				}
			} catch (Exception e) {
				log.error("Failed to create product: {}", product.getName(), e);
			}
		}
	}

	@Override
	public void run(String... args) {
		injectAccounts();
		injectProducts();
	}
}
