package dev.realtards.kuenyawz.configurations.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final AuthenticationProvider authenticationProvider;
	private final JWTAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSec) throws Exception {
		httpSec
			// TODO: uncomment security configurations
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/api/**"))
			.authorizeHttpRequests(auth -> auth
//				// H2 Console access
//				.requestMatchers("/h2-console/**").permitAll()
//
//				// Allow preflight requests
//				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//
//				// Public endpoints
//				.requestMatchers(HttpMethod.GET,
//					"/api",
//					"/api/status",
//					"/api/images/**",
//					"/api/products",
//					"/api/products/**",
//					"/api/recommender/**").permitAll()
//
//				// Auth endpoints (all public)
//				.requestMatchers(HttpMethod.POST,
//					"/api/auth/register",
//					"/api/auth/login",
//					"/api/auth/revoke",
//					"/api/auth/refresh",
//					"/api/auth/otp/request",
//					"/api/auth/otp/verify").permitAll()
//
//				// Simulator endpoints
//				.requestMatchers("/api/sim/**").permitAll()
//
//				// Account endpoints
//				.requestMatchers(HttpMethod.GET, "/api/accounts").hasRole("ADMIN")
//				.requestMatchers(HttpMethod.POST, "/api/accounts").hasRole("ADMIN")
//				.requestMatchers(HttpMethod.PATCH, "/api/accounts/{accountId:\\d+}/privilege").hasRole("ADMIN")
//				.requestMatchers("/api/accounts/**").hasAnyRole("ADMIN", "USER")
//
//				// Product/Image admin endpoints
//				.requestMatchers(HttpMethod.POST, "/api/products/**", "/api/images/**").hasRole("ADMIN")
//				.requestMatchers(HttpMethod.PUT, "/api/products/**", "/api/images/**").hasRole("ADMIN")
//				.requestMatchers(HttpMethod.DELETE, "/api/products/**", "/api/images/**").hasRole("ADMIN")
//				.requestMatchers(HttpMethod.PATCH, "/api/products/**").hasRole("ADMIN")
//
//				// Catch-all
//				.anyRequest().authenticated()
					.anyRequest().permitAll()
			)
			.headers(hs -> hs.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
			.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authenticationProvider(authenticationProvider)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

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
				"/api/docs/v3**",
				"/swagger-ui/**",
				"/swagger-ui.html",
				"/favicon.ico"
			);
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:5173"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
