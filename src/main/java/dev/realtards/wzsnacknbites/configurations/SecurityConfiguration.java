package dev.realtards.wzsnacknbites.configurations;

import dev.realtards.wzsnacknbites.models.Account;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	// TODO: Change this in production
	/**
	 * Configures the security filter chain
	 * @param hs {@link HttpSecurity} object
	 * @return {@link SecurityFilterChain} object
	 * @throws Exception any exception that occurs during configuration
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity hs) throws Exception {
		hs
			// disabling CSRF for h2-console and API endpoints
			.csrf(csrfConfig -> csrfConfig
				.ignoringRequestMatchers("/h2-console/**")
				.ignoringRequestMatchers("/api/v1/**")
			)
			// authorize h2-console and API endpoints
			.authorizeHttpRequests(authRequest -> authRequest
				.requestMatchers("/h2-console/**").permitAll()
				.requestMatchers("/api/v1/**").permitAll()
			)
			// enable frame options for h2-console
			.headers(headers -> headers
				.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable())
			)
			// basic authentication for all other requests
			.httpBasic(httpBasic -> {
			})
			// prepare session management for JWT, that is stateless
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS
				)
			);
		return hs.build();
	}

    @Bean
    public AuthenticationProvider authenticationProvider(AccountUserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
