package dev.realtards.wzsnacknbites.boostrappers;

import dev.realtards.wzsnacknbites.dtos.account.AccountRegistrationDto;
import dev.realtards.wzsnacknbites.dtos.account.PrivilegeUpdateDto;
import dev.realtards.wzsnacknbites.exceptions.AccountExistsException;
import dev.realtards.wzsnacknbites.models.Account;
import dev.realtards.wzsnacknbites.services.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ListIterator;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseBootstrapper implements CommandLineRunner {

	private final AccountService accountService;

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

	@Override
	public void run(String... args) {
		injectAccounts();
	}
}
