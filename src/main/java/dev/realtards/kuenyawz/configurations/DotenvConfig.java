package dev.realtards.kuenyawz.configurations;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for dotenv. This helps to load the dotenv file and
 * make it available for the application. This uses the dotenv-java library.
 */
@Configuration
public class DotenvConfig {

	@Bean
	public Dotenv dotenv() {
		return Dotenv.configure().ignoreIfMissing().load();
	}
}
