package dev.realtards.kuenyawz.configurations.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSec) throws Exception {
		httpSec
			.csrf(csrf -> csrf.ignoringRequestMatchers(
				"/h2-console/**",
				"/api/v1/**"
			))
			.authorizeHttpRequests(auth -> auth
//					.requestMatchers(HttpMethod.POST).permitAll()
//					.requestMatchers(HttpMethod.GET).permitAll()
//					.requestMatchers(HttpMethod.PUT).permitAll()
//					.requestMatchers(HttpMethod.DELETE).permitAll()
//					.requestMatchers(HttpMethod.PATCH).permitAll()
					.anyRequest().permitAll()
//				.requestMatchers("/h2-console/**").hasRole("ADMIN")
//				.requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
//				.requestMatchers("/api/v1/sim/**").hasRole("ADMIN")
//				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").hasRole("ADMIN")
//				.requestMatchers("/api/v1/**").hasAnyRole("ADMIN", "USER")
//				.requestMatchers("/api/v1/auth/**").permitAll()
			)
			.headers(headers -> headers
				.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
			)
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.httpBasic(Customizer.withDefaults());

		return httpSec.build();
	}


	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring()
			.requestMatchers(
				"/v3/api-docs/**",
				"/swagger-ui/**",
				"/swagger-ui.html"
			);
	}

	@Bean
	public AuthenticationProvider authenticationProvider(AccountUserDetailsService userDetailsService) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
