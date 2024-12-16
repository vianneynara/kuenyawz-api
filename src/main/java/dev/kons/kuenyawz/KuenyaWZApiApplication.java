package dev.kons.kuenyawz;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@OpenAPIDefinition
@EnableJpaAuditing
@EnableConfigurationProperties
@EnableCaching
public class KuenyaWZApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(KuenyaWZApiApplication.class, args);
	}

}
