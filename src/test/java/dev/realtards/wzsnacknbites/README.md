# Testing directory

This directory contains tests that covers the API program.
We do tests such as unit tests, integration tests, and end-to-end tests.

## AAA Testing Pattern

AAA stands for Arrange, Act, Assert.
This pattern is commonly used to structure the test code.
Utilizing this pattern improves readability and maintainability of the test codes.

Breaking it down:

1. **Arrange: (setting up the test case)**
    - Create the test data
    - Configuring mocks
    - Creating expected results
2. **Act: (executing the codes/methods that are being tested)**
    - Done by calling the methods that are being tested
3. **Assert: (verifying results)**
    - Check if the results are as expected
    - Verify mock interactions

## Test Example

Code:

```java
public Account updatePrivilege(Account account, Account.Privilege privilege) {
	// Step 1: Method calls findById to check if account exists
	Account existingAccount = accountRepository.findById(account.getAccountId())
		.orElseThrow(AccountNotFoundException::new);

	// Step 2: Updates the privilege
	existingAccount.setPrivilege(privilege);

	// Step 3: Saves the updated account
	existingAccount = accountRepository.save(existingAccount);

	return existingAccount;
}
```

Test using AAA pattern:

```java

@Test
void updatePrivilege_Simplified() {
	// Arrange
	Account inputAccount = Account.builder()
		.accountId(1L)
		.privilege(Account.Privilege.USER)
		.build();

	// We must mock findById because the service checks if account exists
	when(accountRepository.findById(1L))
		.thenReturn(Optional.of(inputAccount));

	// We must mock save because the service saves the updated account
	when(accountRepository.save(any(Account.class)))
		.thenReturn(inputAccount.toBuilder()
			.privilege(Account.Privilege.ADMIN)
			.build());

	// Act
	Account result = accountService.updatePrivilege(inputAccount, Account.Privilege.ADMIN);

	// Assert
	assertThat(result.getPrivilege()).isEqualTo(Account.Privilege.ADMIN);
}
```

Remember:

- Unit tests should test in isolation - we don't want to hit a real database
- Mocks help us control the behavior of dependencies (like repositories)
- The AAA pattern helps keep tests organized and readable
- By mocking both operations, we can test both successful cases and error cases (like account not found)

## Honorable Mentions

**Claude AI** that has helped me discover and learn this pattern.