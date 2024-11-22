package dev.realtards.kuenyawz.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.realtards.kuenyawz.boostrappers.DatabaseBootstrapper;
import dev.realtards.kuenyawz.dtos.account.AccountSecureDto;
import dev.realtards.kuenyawz.repositories.AccountRepository;
import dev.realtards.kuenyawz.services.entity.AccountService;
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
	AccountController accountController;

	@Autowired
	AccountService accountService;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	WebApplicationContext webApplicationContext;

	MockMvc mockMvc;
	@Autowired
	private AccountRepository accountRepository;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void testGetAllAccounts() {
		List<AccountSecureDto> accounts = accountService.getAllAccounts();

		assertThat(accounts).isNotEmpty();
	}

}
