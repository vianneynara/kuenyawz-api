package dev.realtards.wzsnacknbites.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().info(
			new Info()
				.title("WZ Snack And Bites API")
				.version("1.0.0")
				.description("Private API to serve the WZ Snack And Bites website")
				.contact(new Contact()
					.name("Nara")
					.email("vianneynara.github@outlook.com")
					.url("https://github.com/vianneynara/wz-snack-n-bites-api"))
				.license(new License()
					.name("MIT")
					.url("https://github.com/vianneynara/wz-snack-n-bites-api/blob/main/LICENSE"))
		);
	}
}