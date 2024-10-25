package dev.realtards.wzsnacknbites.controllers;

import dev.realtards.wzsnacknbites.dtos.account.*;
import dev.realtards.wzsnacknbites.models.Account;
import dev.realtards.wzsnacknbites.services.AccountService;
import dev.realtards.wzsnacknbites.services.AccountServiceImpl;
import dev.realtards.wzsnacknbites.testBases.BaseWebMvcTest;
import dev.realtards.wzsnacknbites.testUntils.TestUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest extends BaseWebMvcTest {

	private final String BASE_URL = TestUtility.BASE_URL + "account";

	@MockBean
	private AccountService accountService;

	@InjectMocks
	private AccountServiceImpl accountServiceImpl;

	private Account testAccount;
	private Account testAccount2;
	private AccountRegistrationDto testRegistrationDto;
	private AccountPutDto testPutDto;
	private AccountPatchDto testPatchDto;
	private PasswordUpdateDto testPasswordDto;
	private PrivilegeUpdateDto testPrivilegeDto;
	private List<Long> idIterable;
	private ListIterator<Long> idIterator;

	@BeforeEach
	void setUp() {
		idIterable = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L);
		idIterator = idIterable.listIterator();

		testAccount = Account.builder()
			.accountId(idIterator.next())
			.fullName("Test User")
			.email("test@example.com")
			.password("password123")
			.privilege(Account.Privilege.USER)
			.build();

		testAccount2 = Account.builder()
			.accountId(idIterator.next())
			.fullName("Test User 2")
			.email("test2@example.com")
			.password("password123")
			.privilege(Account.Privilege.USER)
			.build();

		testRegistrationDto = AccountRegistrationDto.builder()
			.fullName("Test User")
			.email("test@example.com")
			.password("password123")
			.build();

		testPutDto = AccountPutDto.builder()
			.fullName("Updated User")
			.email("updated@example.com")
			.build();

		testPatchDto = AccountPatchDto.builder()
			.fullName("Patched User")
			.build();

		testPasswordDto = PasswordUpdateDto.builder()
			.currentPassword("password123")
			.newPassword("newpassword123")
			.build();

		testPrivilegeDto = PrivilegeUpdateDto.builder()
			.privilege(String.valueOf(Account.Privilege.ADMIN))
			.build();
	}

	@Test
	void testGetAllAccounts() throws Exception {
		List<Account> accounts = Arrays.asList(testAccount, testAccount2);
		given(accountService.getAllAccounts()).willReturn(accounts);

		mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/all"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			// Ensure that the returned accounts size is 2 (testAccount and testAccount2)
			.andExpect(jsonPath("$.accounts", hasSize(2)))
			.andExpect(jsonPath("$.accounts[0].accountId").value(testAccount.getAccountId().toString()));
	}

	@Test
	void getAccount_ById_ShouldReturnAccount() throws Exception {
		given(accountService.getAccount(testAccount.getAccountId())).willReturn(testAccount);

		mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/" + testAccount.getAccountId()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.accountId").value(testAccount.getAccountId().toString()))
			.andExpect(jsonPath("$.fullName", is(testAccount.getFullName())));
	}

	@Test
	void createAccount_ShouldReturnCreatedAccount() throws Exception {
		given(accountService.createAccount(any(AccountRegistrationDto.class))).willReturn(testAccount);

		mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtility.asJsonString(testRegistrationDto)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.accountId").value(testAccount.getAccountId().toString()))
			.andExpect(jsonPath("$.fullName", is(testAccount.getFullName())));
	}

	@Test
	void updateAccount_ShouldReturnUpdatedAccount() throws Exception {
		Account updatedAccount = Account.builder()
			.fullName(testPutDto.getFullName())
			.email(testPutDto.getEmail())
			.build();

		given(accountService.updateAccount(eq(testAccount.getAccountId()), any(AccountPutDto.class)))
			.willReturn(updatedAccount);

		mockMvc.perform(put(BASE_URL + "/" + testAccount.getAccountId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtility.asJsonString(testPutDto)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.fullName", is(testPutDto.getFullName())))
			.andExpect(jsonPath("$.email", is(testPutDto.getEmail())));
	}

	@Test
	void deleteAccount_ShouldReturnNoContent() throws Exception {
		doNothing().when(accountService).deleteAccount(testAccount.getAccountId());

		mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/" + testAccount.getAccountId()))
			.andExpect(status().isNoContent());

		verify(accountService).deleteAccount(testAccount.getAccountId());
	}

	@Test
	void patchAccount_ShouldReturnUpdatedAccount() throws Exception {
		Account patchedAccount = Account.builder()
			.fullName(testPatchDto.getFullName())
			.build();

		given(accountService.patchAccount(eq(testAccount.getAccountId()), any(AccountPatchDto.class)))
			.willReturn(patchedAccount);

		mockMvc.perform(patch(BASE_URL + "/" + testAccount.getAccountId() + "/account")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtility.asJsonString(testPatchDto)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.fullName", is(testPatchDto.getFullName())));
	}

	@Test
	void updatePassword_ShouldReturnNoContent() throws Exception {
		PasswordUpdateDto passwordDto = PasswordUpdateDto.builder()
			.currentPassword("oldPassword")
			.newPassword("newPassword")
			.confirmPassword("newPassword")
			.build();

		when(accountService.updatePassword(any(Long.class), any(PasswordUpdateDto.class)))
			.thenReturn(null);

		mockMvc.perform(patch(BASE_URL + "/" + "{id}/password", 1L)
				.content(TestUtility.asJsonBytes(passwordDto))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());
	}

	@Test
	void updatePrivilege_ShouldReturnNoContent() throws Exception {
		when(accountService.updatePrivilege(any(Long.class), any(PrivilegeUpdateDto.class)))
			.thenReturn(null);

		mockMvc.perform(patch(BASE_URL + "/" + "{id}/privilege", 1L)
				.content(TestUtility.asJsonBytes(Account.Privilege.USER))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());
	}
}
