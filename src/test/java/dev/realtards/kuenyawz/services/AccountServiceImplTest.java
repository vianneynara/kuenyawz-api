package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.dtos.account.*;
import dev.realtards.kuenyawz.entities.Account;
import dev.realtards.kuenyawz.exceptions.AccountExistsException;
import dev.realtards.kuenyawz.exceptions.AccountNotFoundException;
import dev.realtards.kuenyawz.exceptions.InvalidPasswordException;
import dev.realtards.kuenyawz.exceptions.PasswordMismatchException;
import dev.realtards.kuenyawz.mapper.AccountMapper;
import dev.realtards.kuenyawz.repositories.AccountRepository;
import dev.realtards.kuenyawz.services.entity.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AccountServiceImpl accountServiceImpl;

    @Spy
    private AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    @InjectMocks
    private AccountServiceImpl accountService;

	// To store test data globally

	private Account testAccount;
	private AccountRegistrationDto testRegistrationDto;
	private ListIterator<Long> idIterator;

	@BeforeEach
	void setUp() {
		List<Long> idIterable = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L);
		idIterator = idIterable.listIterator();

		testAccount = Account.builder()
			.accountId(idIterator.next())
			.fullName("Test User")
			.phone("81234567890")
			.password("password123")
			.privilege(Account.Privilege.USER)
			.build();

		testRegistrationDto = new AccountRegistrationDto();
		testRegistrationDto.setFullName("Test User");
		testRegistrationDto.setPhone("81234567890");
		testRegistrationDto.setPassword("password123");
	}

    @Test
    void getAllAccounts_ShouldReturnAllAccounts() {
        // Arrange
        List<Account> accounts = List.of(testAccount);
        when(accountRepository.findAll()).thenReturn(accounts);

        // Act
        List<AccountSecureDto> actualAccounts = accountService.getAllAccounts();

        // Assert
        List<AccountSecureDto> expectedAccounts = accounts.stream()
            .map(accountMapper::fromEntity)
            .toList();
        assertThat(actualAccounts).isEqualTo(expectedAccounts);
        verify(accountRepository).findAll();
    }

	@Test
	void createAccount_WithNewEmail_ShouldCreateAccount() {
		// Arrange
		when(accountRepository.existsByPhone(anyString())).thenReturn(false);
		when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

		// Act
		Account createdAccount = accountServiceImpl.createAccount(testRegistrationDto);

		// Assert
		assertThat(createdAccount).isNotNull();
		assertThat(createdAccount.getPhone()).isEqualTo(testRegistrationDto.getPhone());
		verify(accountRepository).existsByPhone(testRegistrationDto.getPhone());
		verify(accountRepository).save(any(Account.class));
	}

	@Test
	void createAccount_WithExistingEmail_ShouldThrowException() {
		// Arrange
		when(accountRepository.existsByPhone(anyString())).thenReturn(true);

		// Act & Assert
		assertThrows(AccountExistsException.class, () ->
			accountServiceImpl.createAccount(testRegistrationDto)
		);
		verify(accountRepository).existsByPhone(testRegistrationDto.getPhone());
		verify(accountRepository, never()).save(any(Account.class));
	}

	@Test
	void getAccount_WithExistingId_ShouldReturnAccount() {
		// Arrange
		when(accountRepository.findById(anyLong())).thenReturn(Optional.of(testAccount));

		// Act
		Account foundAccount = accountServiceImpl.getAccount(1L);

		// Assert
		assertThat(foundAccount).isEqualTo(testAccount);
		verify(accountRepository).findById(1L);
	}

	@Test
	void getAccount_WithNonExistingId_ShouldThrowException() {
		// Arrange
		when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(AccountNotFoundException.class, () ->
			accountServiceImpl.getAccount(1L)
		);
		verify(accountRepository).findById(1L);
	}

	@Test
	void updateAccount_WithExistingAccount_ShouldUpdate() {
		// Arrange
		Account updatedAccount = Account.builder()
			.accountId(idIterator.next())
			.fullName("Updated Name")
			.build();
		when(accountRepository.findById(anyLong())).thenReturn(Optional.of(testAccount));
		when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

		// Act
		Account result = accountServiceImpl.updateAccount(updatedAccount.getAccountId(), AccountPutDto.fromEntity(updatedAccount));

		// Assert
		assertThat(result.getFullName()).isEqualTo("Updated Name");
		verify(accountRepository).findById(anyLong());
		verify(accountRepository).save(any(Account.class));
	}

	@Test
	void deleteAccount_ShouldDeleteAccount() {
		// Arrange
		final Long persistentId = idIterator.next();
		when(accountRepository.existsById(persistentId)).thenReturn(true);

		// Act
		accountServiceImpl.deleteAccount(persistentId);

		// Assert
		verify(accountRepository).existsById(persistentId);
		verify(accountRepository).deleteById(persistentId);
	}

	@Test
	void patchAccount_WithExistingPhone_ShouldPatchAccount() {
		// Arrange
		final Long persistentId = idIterator.next();

		Account existingAccount = Account.builder()
			.accountId(persistentId)
			.fullName("Test User")
			.email("patch@test.com")
			.build();

		Account updatedAccount = Account.builder()
			.accountId(persistentId)
			.fullName("Test User")
			.email("patched@test.com")
			.build();

		// Mock findById and save
		when(accountRepository.findById(persistentId)).thenReturn(Optional.of(existingAccount));
		when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

		// Act
		Account result = accountServiceImpl.patchAccount(persistentId, AccountPatchDto.fromEntity(updatedAccount));

		// Assert
		assertThat(result.getEmail()).isEqualTo(updatedAccount.getEmail());
		verify(accountRepository).findById(persistentId);
		verify(accountRepository).save(any(Account.class));
	}

	@Test
	void patchAccount_WithExistingPhone_ShouldThrowException() {
		// Arrange
		final Long persistentId = idIterator.next();

		Account existingAccount = Account.builder()
			.accountId(persistentId)
			.fullName("Test User")
			.phone("81100011")
			.build();

		AccountPatchDto patchDto = AccountPatchDto.builder()
			.phone("81100011")
			.build();

		// Mock findById to return our existing account
		when(accountRepository.findById(persistentId)).thenReturn(Optional.of(existingAccount));
		// Mock existsByEmail to return true (email already taken by another account)
		when(accountRepository.existsByPhone(patchDto.getPhone())).thenReturn(true);

		// Act & Assert
		assertThrows(AccountExistsException.class, () ->
			accountServiceImpl.patchAccount(persistentId, patchDto)
		);

		// Verify
		verify(accountRepository).findById(persistentId);
		verify(accountRepository).existsByPhone(patchDto.getPhone());
		verify(accountRepository, never()).save(any(Account.class)); // Should never reach save
	}

	@Test
	void patchAccount_WithNullFields_ShouldNotUpdateNullFields() {
		// Arrange
		final Long persistentId = idIterator.next();

		Account existingAccount = Account.builder()
			.accountId(persistentId)
			.fullName("Original Name")
			.email("original@test.com")
			.phone("123456")
			.build();

		AccountPatchDto patchDto = AccountPatchDto.builder()
			.fullName("New Name")
			.email(null)  // Should not update
			.phone(null)  // Should not update
			.build();

		when(accountRepository.findById(persistentId)).thenReturn(Optional.of(existingAccount));
		when(accountRepository.save(any(Account.class))).thenReturn(existingAccount);

		// Act
		Account result = accountServiceImpl.patchAccount(persistentId, patchDto);

		// Assert
		assertThat(result.getFullName()).isEqualTo("New Name");
		assertThat(result.getEmail()).isEqualTo("original@test.com");
		assertThat(result.getPhone()).isEqualTo("123456");
	}

	@Test
	void updatePassword_ShouldUpdatePassword() {
		// Arrange
		final Long persistentId = idIterator.next();
		String encodedOldPassword = "encodedOldPassword";
		String encodedNewPassword = "encodedNewPassword";

		Account testAccount = Account.builder()
			.accountId(persistentId)
			.password(encodedOldPassword)
			.build();

		when(accountRepository.findById(persistentId)).thenReturn(Optional.of(testAccount));
		when(passwordEncoder.encode(anyString())).thenReturn(encodedNewPassword);
		when(passwordEncoder.matches(anyString(), eq(encodedOldPassword))).thenReturn(true);

		// Act
		accountServiceImpl.updatePassword(persistentId, new PasswordUpdateDto(
			"oldPassword",
			"newPassword",
			"newPassword"
		));

		// Assert
		verify(passwordEncoder).matches(anyString(), eq(encodedOldPassword));
		verify(passwordEncoder).encode(anyString());
		verify(accountRepository).save(argThat(account ->
			account.getPassword().equals(encodedNewPassword)
		));
	}

	@Test
	void updatePassword_WithIncorrectCurrentPassword_ShouldThrowException() {
		// Arrange
		PasswordUpdateDto dto = new PasswordUpdateDto(
			"wrongPassword",
			"newPassword",
			"newPassword"
		);

		when(accountRepository.findById(anyLong())).thenReturn(Optional.of(testAccount));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

		// Act & Assert
		assertThrows(InvalidPasswordException.class, () ->
			accountServiceImpl.updatePassword(1L, dto)
		);
	}

	@Test
	void updatePassword_WithMismatchedPasswords_ShouldThrowException() {
		// Arrange
		String rawCurrentPassword = "currentPassword";
		String encodedCurrentPassword = "encodedPassword";

		Account testAccount = Account.builder()
			.accountId(1L)
			.password(encodedCurrentPassword)
			.build();

		PasswordUpdateDto dto = new PasswordUpdateDto(
			rawCurrentPassword,     // Raw current password
			"newPassword",         // New password
			"differentPassword"    // Different confirm password
		);

		when(accountRepository.findById(anyLong())).thenReturn(Optional.of(testAccount));
		when(passwordEncoder.matches(rawCurrentPassword, encodedCurrentPassword)).thenReturn(true);

		// Act & Assert
		assertThrows(PasswordMismatchException.class, () ->
			accountServiceImpl.updatePassword(testAccount.getAccountId(), dto)
		);
	}

	@Test
	void updatePrivilege_ShouldUpdatePrivilege() {
		// Arrange
		Account testAccount = Account.builder()
			.accountId(1L)
			.privilege(Account.Privilege.USER)  // Starting privilege
			.build();

		Account updatedAccount = Account.builder()
			.accountId(1L)
			.privilege(Account.Privilege.ADMIN)  // New privilege
			.build();

		// Mock findById and save
		when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
		when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

		// Act
		Account result = accountServiceImpl.updatePrivilege(testAccount.getAccountId(), new PrivilegeUpdateDto(Account.Privilege.ADMIN));

		// Assert
		assertThat(result.getPrivilege()).isEqualTo(Account.Privilege.ADMIN);
		verify(accountRepository).findById(1L);
		verify(accountRepository).save(any(Account.class));
	}
}