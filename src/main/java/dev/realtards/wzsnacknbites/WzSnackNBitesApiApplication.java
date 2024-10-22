package dev.realtards.wzsnacknbites;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties
public class WzSnackNBitesApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WzSnackNBitesApiApplication.class, args);
	}

}
