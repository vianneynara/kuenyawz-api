package dev.kons.kuenyawz.configurations.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kons.kuenyawz.configurations.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

	private final AuthenticationProvider authenticationProvider;
	private final JWTAuthenticationFilter jwtAuthenticationFilter;
	private final ApplicationProperties properties;

	@Bean
	@Order(1)
	public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity httpSec) throws Exception {
		httpSec.securityMatcher(EndpointRequest.toAnyEndpoint())
			.csrf(csrf -> csrf.ignoringRequestMatchers(
				EndpointRequest.to("shutdown", "refresh", "health", "info")))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(EndpointRequest.to("health", "info")).permitAll()
				.requestMatchers(HttpMethod.POST,
					"/actuator/shutdown",
					"/actuator/refresh").hasRole("ADMIN")
				.requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ADMIN")
			)
			.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authenticationProvider(authenticationProvider)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return httpSec.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSec) throws Exception {
		httpSec
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/api/**"))
			.authorizeHttpRequests(auth -> auth
					// Docs/Swagger access
					.requestMatchers(
						"/api/docs/**",
						"/swagger-ui/**",
						"/favicon.ico").permitAll()

//				// Actuator endpoints
//				.requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
//				.requestMatchers(HttpMethod.GET, "/actuator/**").hasRole("ADMIN")

					// H2 Console access
					.requestMatchers("/h2-console/**").permitAll()

					// Allow preflight requests
					.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

					// Public endpoints
					.requestMatchers(HttpMethod.GET,
						"/static/**",
						"/api",
						"/api/status",
						"/api/images/**",
						"/api/products",
						"/api/products/**",
						"/api/recommender/**",
						"/api/closure",
						"/api/closure/**",
						"/api/static/**").permitAll()

					// Public webhooks
					.requestMatchers(HttpMethod.POST,
						"/api/midtrans/notify"
					).permitAll()

					// Auth endpoints (all public)
					.requestMatchers(HttpMethod.POST,
						"/api/auth/register",
						"/api/auth/login",
						"/api/auth/revoke",
						"/api/auth/refresh",
						"/api/auth/otp/request",
						"/api/auth/otp/verify").permitAll()

					// Simulator endpoints
					.requestMatchers("/api/sim/**").permitAll()

					// TODO: use .denyAll() in production to reject incoming requests
					// Experimental endpoints
					.requestMatchers(HttpMethod.POST, "/api/midtrans/sign").hasRole("ADMIN")

					// Account endpoints
					// Special case: uses master key or authorization to access this endpoint, defined in
					// the controller. Dangerous if not properly secured.
//					.requestMatchers(HttpMethod.GET, "/api/accounts").permitAll()
//					.requestMatchers(HttpMethod.POST, "/api/accounts").permitAll()
//					.requestMatchers(HttpMethod.DELETE, "/api/accounts/**").permitAll()
//					.requestMatchers(HttpMethod.PATCH, "/api/accounts/{accountId:\\d+}/privilege").permitAll()
					.requestMatchers("/api/accounts**").permitAll()

					// Closure endpoints
					.requestMatchers(HttpMethod.POST, "/api/closure").hasRole("ADMIN")
					.requestMatchers(HttpMethod.DELETE, "/api/closure**").hasRole("ADMIN")
					.requestMatchers(HttpMethod.PATCH, "/api/closure").hasRole("ADMIN")

					// Order Processing endpoints
					.requestMatchers(HttpMethod.GET, "/api/orders/**").hasAnyRole("ADMIN", "USER")
					.requestMatchers(HttpMethod.POST, "/api/orders").hasRole("USER")
					.requestMatchers(HttpMethod.POST, "/api/orders/{purchaseId:\\d+}/cancel").hasAnyRole("ADMIN", "USER")
					.requestMatchers(HttpMethod.POST, "/api/orders/{purchaseId:\\d+}/confirm").hasAnyRole("ADMIN")
					.requestMatchers(HttpMethod.POST, "/api/orders/{purchaseId:\\d+}/status").hasAnyRole("ADMIN")
					.requestMatchers(HttpMethod.GET, "/api/orders/{purchaseId:\\d+}/status/next").hasAnyRole("ADMIN", "USER")
					.requestMatchers(HttpMethod.POST, "/api/orders/{purchaseId:\\d+}/status/next").hasAnyRole("ADMIN")
					.requestMatchers(HttpMethod.GET, "/api/orders/{purchaseId:\\d+}/transaction").hasAnyRole("ADMIN", "USER")

					// Transaction endpoints
					.requestMatchers(HttpMethod.GET, "/api/transactions").hasAnyRole("ADMIN", "USER")
					.requestMatchers(HttpMethod.GET, "/api/transactions/{transactionId:\\d+}").hasAnyRole("ADMIN", "USER")

					// Product/Image admin endpoints
					.requestMatchers(HttpMethod.POST, "/api/products/**", "/api/images/**").hasRole("ADMIN")
					.requestMatchers(HttpMethod.PUT, "/api/products/**", "/api/images/**").hasRole("ADMIN")
					.requestMatchers(HttpMethod.DELETE, "/api/products/**", "/api/images/**").hasRole("ADMIN")
					.requestMatchers(HttpMethod.PATCH, "/api/products/**").hasRole("ADMIN")

					// Recommender endpoints
					.requestMatchers(HttpMethod.POST, "/api/recommender/generate").hasRole("ADMIN")

					// Closure endpoints
					.requestMatchers(HttpMethod.POST, "/api/closure").hasRole("ADMIN")
					.requestMatchers(HttpMethod.DELETE, "/api/closure/**").hasRole("ADMIN")

					// Catch-all
					.anyRequest().authenticated()
			)
			.headers(hs -> hs.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
			.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authenticationProvider(authenticationProvider)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

			// Special handler for 403
			.exceptionHandling(exc -> exc
				.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
				.accessDeniedHandler((request, response, ex) -> {
					response.setStatus(HttpStatus.FORBIDDEN.value());
					response.setContentType(MediaType.APPLICATION_JSON_VALUE);

					Map<String, Object> body = new LinkedHashMap<>();
					body.put("message", ex.getMessage());
					body.put("method", request.getMethod());
					body.put("path", request.getServletPath());

					ObjectMapper mapper = new ObjectMapper();
					mapper.writeValue(response.getOutputStream(), body);
				})
			);

		return httpSec.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		String frontEndBaseUrl = properties.getFrontend().getBaseUrl();
		log.info("CORS Origin allowed for: {}", frontEndBaseUrl);

		// Allowing a list if possible ports we'll use lol
		configuration.setAllowedOrigins(List.of(
			frontEndBaseUrl,
			/* hardcoded here, it's the same as what is supposed to be in frontEndBaseUrl */
//			"https://natural-hamster-firstly.ngrok-free.app",
			/* this is pretty much unecessary since it's the current domain being sit by the program*/
			"https://turkey-glad-orca.ngrok-free.app",
			"http://localhost:80",
			"http://localhost:443",
			"http://localhost:5173",
			"http://localhost:8081", // H2/Swagger UI
			"http://localhost:62080",
			"http://localhost:62081" // H2/Swagger UI
		));
		configuration.setAllowedHeaders(List.of("Content-Type", "Authorization", "X-Requested-With", "X-Api-Key", "Ngrok-Skip-Browser-Warning"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
