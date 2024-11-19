package dev.realtards.kuenyawz.configurations;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
			.components(new Components()
				.addSecuritySchemes("bearerAuth",
					new SecurityScheme()
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")))
			.info(new Info()
				.title("KuenyaWZ API")
				.version("1.0.0")
				.description("Private API to serve KuenyaWZ website")
				.contact(new Contact()
					.name("Nara")
					.email("vianneynara.github@outlook.com")
					.url("https://github.com/vianneynara"))
				.license(new License()
					.name("MIT")
					.url("https://github.com/vianneynara/kuenyawz-api/blob/main/LICENSE"))
			);
	}
}