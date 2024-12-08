package dev.kons.kuenyawz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties
@EnableCaching
public class KuenyaWZApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(KuenyaWZApiApplication.class, args);
	}

}
