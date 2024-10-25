package dev.realtards.wzsnacknbites.configurations;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * This is not an explicit configuration class, but a necessary component to read
 * the application properties of 'application' in `application.properties` and helps
 * to initialize the properties through dotenv.
 */
@Component
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
@Getter
@Setter
public class ApplicationProperties {

	// Fields
	private String version;
	private String repositoryUrl;
	private Database database = new Database();

	// Initializing through dotenv
	@Autowired
	public void initialize(Dotenv dotenv) {
		this.version = dotenv.get("APP_VERSION", "0.0");
		this.repositoryUrl = dotenv.get("APP_REPOSITORY_URL", "https://github.com/vianneynara/*");

        this.database.url = dotenv.get("DB_URL");
        this.database.username = dotenv.get("DB_USERNAME");
        this.database.password = dotenv.get("DB_PASSWORD");
	}

	@Getter
	@Setter
	public static class Database {
		private String url;
		private String username;
		private String password;
	}
}
