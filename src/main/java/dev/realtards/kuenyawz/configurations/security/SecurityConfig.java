package dev.realtards.kuenyawz.configurations.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSec) throws Exception {
		httpSec
			.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/api/**"))
			.authorizeHttpRequests(auth -> auth
					.requestMatchers(
						"api/accounts/**").permitAll()
//					.requestMatchers(HttpMethod.PATCH,
//						"/api/accounts/**").hasAnyRole("ADMIN", "USER")
					.requestMatchers(
						"/api/sim/**").permitAll()
					.requestMatchers(HttpMethod.GET,
						"/api/products",
						"/api/products/**").permitAll()
					.requestMatchers(
						"/api/products",
						"/api/products/**").hasRole("ADMIN")
					.requestMatchers(HttpMethod.GET,
						"/api/images").permitAll()
					.requestMatchers(
						"/api/images").hasRole("ADMIN")
					.requestMatchers(HttpMethod.GET,
						"/api",
						"/api/status").permitAll()
					.requestMatchers(HttpMethod.GET,
						"/api/images/**").permitAll()
//					.anyRequest().permitAll()
			)
			.headers(headers -> headers
				.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
			)
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.httpBasic(Customizer.withDefaults())

			// Special handler for 401 and 403
			.exceptionHandling(exc -> exc
				.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
				.accessDeniedHandler((request, response, ex) -> {
					response.setStatus(HttpStatus.FORBIDDEN.value());
					response.setContentType(MediaType.APPLICATION_JSON_VALUE);

					Map<String, Object> body = new HashMap<>();
					body.put("message", ex.getMessage());

					ObjectMapper mapper = new ObjectMapper();
					mapper.writeValue(response.getOutputStream(), body);
				})
			);

		return httpSec.build();
	}


	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring()
			.requestMatchers(
				"/api/docs/v3/**",
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
