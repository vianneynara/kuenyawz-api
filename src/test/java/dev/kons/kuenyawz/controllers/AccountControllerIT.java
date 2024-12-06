package dev.kons.kuenyawz.controllers;

import dev.kons.kuenyawz.boostrappers.DatabaseBootstrapper;
import dev.kons.kuenyawz.dtos.account.AccountRegistrationDto;
import dev.kons.kuenyawz.dtos.account.AccountSecureDto;
import dev.kons.kuenyawz.services.entity.AccountService;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import({DatabaseBootstrapper.class})
public class AccountControllerIT {

	@Autowired
	AccountService accountService;

	@Autowired
	WebApplicationContext webApplicationContext;

	MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void testGetAllAccounts_DefaultEmpty() {
		List<AccountSecureDto> accounts = accountService.getAllAccounts();

		assertThat(accounts).isEmpty();
	}

	@Test
	void testGetAllAccounts_WithAccounts() {
		// Arrange
		insertTestAccount("012345678901", "Test Account 1");
		insertTestAccount("012345678902", "Test Account 2");

		List<AccountSecureDto> accounts = accountService.getAllAccounts();

		assertThat(accounts).hasSize(2);
	}

	void insertTestAccount(@NotNull String phone, @NotNull String fullName) {
		accountService.createAccount(
			AccountRegistrationDto.builder()
				.phone(phone)
				.fullName(fullName)
				.password("password")
				.build()
		);
	}
}
