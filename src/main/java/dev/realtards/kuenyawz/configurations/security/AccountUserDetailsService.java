package dev.realtards.kuenyawz.configurations.security;

import dev.realtards.kuenyawz.entities.Account;
import dev.realtards.kuenyawz.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountUserDetailsService implements UserDetailsService {

	private final AccountRepository accountRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// username is the email, such that the email is used as the username
		Account account = accountRepository.findByPhone(username)
			.orElseThrow(() -> new UsernameNotFoundException("User not found with phone number: " + username));

		return account;
	}
}