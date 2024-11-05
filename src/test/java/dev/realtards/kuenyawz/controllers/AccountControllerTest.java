package dev.realtards.kuenyawz.controllers;

import dev.realtards.kuenyawz.dtos.account.*;
import dev.realtards.kuenyawz.entities.Account;
import dev.realtards.kuenyawz.services.AccountService;
import dev.realtards.kuenyawz.testBases.BaseWebMvcTest;
import dev.realtards.kuenyawz.testUntils.TestUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AccountControllerTest extends BaseWebMvcTest {

	private final String BASE_URL = TestUtility.BASE_URL + "/account";

	@Autowired
	protected MockMvc mockMvc;

	@MockBean
	private AccountService accountService;

	/**
	 * This captor is used to intercept (capture) arguments passed to a method.
	 * In account context, it is used to capture the accountId (of type {@link Long}) passed
	 * to service methods. More complex methods such as using DTOS as captor's generic type
	 * can be used to capture DTOs passed to service methods for better checking.
 	 */
	@Captor
	private ArgumentCaptor<Long> longArgumentCaptor;

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
		List<AccountSecureDto> accounts = Stream.of(testAccount, testAccount2).map(AccountSecureDto::new).toList();
		given(accountService.getAllAccounts()).willReturn(accounts);

		mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/all"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			// Ensure that the returned accounts size is 2 (testAccount and testAccount2)
			.andExpect(jsonPath("$.accounts", hasSize(2)))
			.andExpect(jsonPath("$.accounts[0].accountId").value(accounts.getFirst().getAccountId().toString()));
	}

	@Test
	void getAccount_ById_ShouldReturnAccount() throws Exception {
		given(accountService.getAccount(testAccount.getAccountId())).willReturn(testAccount);

		mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/{accountId}", testAccount.getAccountId()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.accountId").value(testAccount.getAccountId().toString()))
			.andExpect(jsonPath("$.fullName", is(testAccount.getFullName())));

		verify(accountService).getAccount(testAccount.getAccountId());

		assertThat(testAccount.getAccountId()).isEqualTo(testAccount.getAccountId());
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

		verify(accountService).createAccount(any(AccountRegistrationDto.class));
	}

	@Test
	void updateAccount_ShouldReturnUpdatedAccount() throws Exception {
		Account updatedAccount = Account.builder()
			.fullName(testPutDto.getFullName())
			.email(testPutDto.getEmail())
			.build();

		given(accountService.updateAccount(eq(testAccount.getAccountId()), any(AccountPutDto.class)))
			.willReturn(updatedAccount);

		mockMvc.perform(put(BASE_URL + "/{accountId}", testAccount.getAccountId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtility.asJsonString(testPutDto)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.fullName", is(testPutDto.getFullName())))
			.andExpect(jsonPath("$.email", is(testPutDto.getEmail())));

		ArgumentCaptor<AccountPutDto> putDtoArgumentCaptor = ArgumentCaptor.forClass(AccountPutDto.class);

		verify(accountService).updateAccount(any(Long.class), putDtoArgumentCaptor.capture());

		assertThat(putDtoArgumentCaptor.getValue().getFullName()).isEqualTo(testPutDto.getFullName());
	}

	@Test
	void deleteAccount_ShouldReturnNoContent() throws Exception {
		doNothing().when(accountService).deleteAccount(testAccount.getAccountId());

		mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/{accountId}", testAccount.getAccountId()))
			.andExpect(status().isNoContent());

		verify(accountService).deleteAccount(longArgumentCaptor.capture());

		assertThat(longArgumentCaptor.getValue()).isEqualTo(testAccount.getAccountId());
	}

	@Test
	void patchAccount_ShouldReturnUpdatedAccount() throws Exception {
		Account patchedAccount = Account.builder()
			.fullName(testPatchDto.getFullName())
			.build();

		given(accountService.patchAccount(eq(testAccount.getAccountId()), any(AccountPatchDto.class)))
			.willReturn(patchedAccount);

		mockMvc.perform(patch(BASE_URL + "/{accountId}/account", testAccount.getAccountId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtility.asJsonString(testPatchDto)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.fullName", is(testPatchDto.getFullName())));

		ArgumentCaptor<AccountPatchDto> patchDtoArgumentCaptor = ArgumentCaptor.forClass(AccountPatchDto.class);

		verify(accountService).patchAccount(any(Long.class), patchDtoArgumentCaptor.capture());

		assertThat(patchDtoArgumentCaptor.getValue().getFullName()).isEqualTo(testPatchDto.getFullName());
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

		mockMvc.perform(patch(BASE_URL + "/{accountId}/password", 1L)
				.content(TestUtility.asJsonBytes(passwordDto))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());

		ArgumentCaptor<PasswordUpdateDto> passwordUpdateDtoArgumentCaptor = ArgumentCaptor.forClass(PasswordUpdateDto.class);

		verify(accountService).updatePassword(any(Long.class), passwordUpdateDtoArgumentCaptor.capture());

		assertThat(passwordUpdateDtoArgumentCaptor.getValue().getCurrentPassword()).isEqualTo(passwordDto.getCurrentPassword());
	}

//	@Test
//	void updatePrivilege_ShouldReturnNoContent() throws Exception {
//		when(accountService.updatePrivilege(any(Long.class), any(PrivilegeUpdateDto.class)))
//			.thenReturn(null);
//
//		mockMvc.perform(patch(BASE_URL + "/{accountId}/privilege", 1L)
//				.content(TestUtility.asJsonBytes(Account.Privilege.USER))
//				.contentType(MediaType.APPLICATION_JSON))
//			.andExpect(status().isNoContent());
//
//		ArgumentCaptor<PrivilegeUpdateDto> privilegeUpdateDtoArgumentCaptor = ArgumentCaptor.forClass(PrivilegeUpdateDto.class);
//
//		verify(accountService).updatePrivilege(any(Long.class), privilegeUpdateDtoArgumentCaptor.capture());
//
//		assertThat(privilegeUpdateDtoArgumentCaptor.getValue().getPrivilege().toString()).isEqualTo(String.valueOf(Account.Privilege.USER));
//	}
}
