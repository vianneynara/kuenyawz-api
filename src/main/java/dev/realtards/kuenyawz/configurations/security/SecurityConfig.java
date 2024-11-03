package dev.realtards.kuenyawz.configurations.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	// TODO: Change this in production

	/**
	 * Configures the security filter chain
	 *
	 * @param hs {@link HttpSecurity} object
	 * @return {@link SecurityFilterChain} object
	 * @throws Exception any exception that occurs during configuration
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity hs) throws Exception {
//		hs
//			// disabling CSRF for h2-console and API endpoints
//			.csrf(csrfConfig -> csrfConfig
//				.ignoringRequestMatchers("/h2-console/**")
//				.ignoringRequestMatchers("/api/v1/**")
//			)
//			// authorize h2-console and API endpoints
//			.authorizeHttpRequests(authRequest -> authRequest
//				.requestMatchers("/h2-console/**").permitAll()
//				.requestMatchers("/api/v1/**").permitAll()
//				.anyRequest().authenticated()
//			)
//			// basic authentication for all other requests
//			.httpBasic(Customizer.withDefaults())
//			// prepare session management for JWT, that is stateless
//			.sessionManagement(session -> session
//				.sessionCreationPolicy(SessionCreationPolicy.STATELESS
//				)
//			);
		hs
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth
				.anyRequest().permitAll()
			)
			// enable frame options for h2-console
			.headers(headers -> headers
				.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable())
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
