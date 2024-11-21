package dev.realtards.kuenyawz.repositories;

import dev.realtards.kuenyawz.entities.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link AccountRepository}. Tested for more complex methods such as derived queries
 * and manual queries.
 */
@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    private Account testAccount;
    private final String TEST_EMAIL = "test@test.com";
    private final String TEST_PHONE = "8120011";
    private final String NON_EXISTENT_EMAIL = "nonexistent@test.com";
    private final String NON_EXISTENT_PHONE = "00000000";

    @BeforeEach
    void setUp() {
        // Clean the repository before each test
        accountRepository.deleteAll();

        // Create and save test account
        testAccount = accountRepository.save(Account.builder()
                .email(TEST_EMAIL)
                .phone(TEST_PHONE)
                .password("password")
                .privilege(Account.Privilege.USER)
                .build());
    }

	// findByEmail

    @Test
    void findByEmail_ShouldReturnAccount_WhenEmailExists() {
        // When
        Optional<Account> found = accountRepository.findByEmail(TEST_EMAIL);

        // Then
        assertThat(found)
                .isPresent()
                .hasValueSatisfying(account -> {
                    assertThat(account.getEmail()).isEqualTo(TEST_EMAIL);
                    assertThat(account.getPrivilege()).isEqualTo(Account.Privilege.USER);
                });
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenEmailDoesNotExist() {
        // When
        Optional<Account> found = accountRepository.findByEmail(NON_EXISTENT_EMAIL);

        // Then
        assertThat(found).isEmpty();
    }

	// existsByEmail

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        // When
        boolean exists = accountRepository.existsByPhone(TEST_PHONE);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {
        // When
        boolean exists = accountRepository.existsByPhone(NON_EXISTENT_PHONE);

        // Then
        assertThat(exists).isFalse();
    }

	// To check id generation

    @Test
    void save_ShouldGenerateId_WhenSavingNewAccount() {
        // Given
        Account newAccount = Account.builder()
                .email("another@test.com")
                .password("password")
                .privilege(Account.Privilege.USER)
                .build();

        // When
        Account saved = accountRepository.save(newAccount);

        // Then
        assertThat(saved.getAccountId()).isNotNull();
    }
}