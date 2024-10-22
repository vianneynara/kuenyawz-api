package dev.realtards.wzsnacknbites.boostrappers;

import dev.realtards.wzsnacknbites.dtos.AccountRegistrationDto;
import dev.realtards.wzsnacknbites.models.Account;
import dev.realtards.wzsnacknbites.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ListIterator;

@Component
@RequiredArgsConstructor
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
			.build()
	);

	private void injectAccounts() {
		final ListIterator<AccountRegistrationDto> iterator = BOOTSTRAP_ACCOUNTS.listIterator();

		// setting the first account as ADMIN
		if (iterator.hasNext()) {
			AccountRegistrationDto accountRegistrationDto = iterator.next();
			Account account = accountService.createAccount(accountRegistrationDto);
			accountService.updatePrivilege(account, Account.Privilege.ADMIN);
		}

		while (iterator.hasNext()) {
			AccountRegistrationDto accountRegistrationDto = iterator.next();
			accountService.createAccount(accountRegistrationDto);
		}
	}

	@Override
	public void run(String... args) throws Exception {
		injectAccounts();
	}
}
