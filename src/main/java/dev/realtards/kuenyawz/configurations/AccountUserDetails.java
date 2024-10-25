package dev.realtards.kuenyawz.configurations;

import dev.realtards.kuenyawz.models.Account;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class AccountUserDetails implements UserDetails {
	private final Long accountId;
	private final String email;
	private final String password;
	private final Collection<? extends GrantedAuthority> authorities;

	public AccountUserDetails(Account account) {
		this.accountId = account.getAccountId();
		this.email = account.getEmail();
		this.password = account.getPassword();
		this.authorities = Collections.singletonList(
			new SimpleGrantedAuthority("ROLE_" + account.getPrivilege().name())
		);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	/**
	 * This specific method returns the username of the account. But as the username
	 * is not present within {@link Account}, the email is used as the username instead.
	 */
	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}