package dev.realtards.kuenyawz.configurations.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final AuthenticationProvider authenticationProvider;
	private final JwtAuthorizationFilter jwtAuthorizationFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSec) throws Exception {
		httpSec
			.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/api/**"))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/accounts/**").permitAll()
				.requestMatchers("/api/sim/**").permitAll()
				.requestMatchers(HttpMethod.GET,
					"/api/products",
					"/api/products/**",
					"/api/images/**",
					"/api",
					"/api/status").permitAll()
				.requestMatchers(HttpMethod.PATCH, "/api/accounts/**").hasAnyRole("ADMIN", "USER")
				.requestMatchers("/api/products", "/api/products/**", "/api/images").hasRole("ADMIN")
				.anyRequest().authenticated()
			)
			.headers(hs -> hs.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
			.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authenticationProvider(authenticationProvider)
			.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)

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
}
