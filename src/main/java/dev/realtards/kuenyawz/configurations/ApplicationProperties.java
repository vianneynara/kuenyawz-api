package dev.realtards.kuenyawz.configurations;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

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
	private String productImagesDir = "product-images";
	private String baseUrl = "http://localhost:8081";

	@Value("#{'${application.accepted-image-extensions}'.split(',')}")
	private List<String> acceptedImageExtensions;

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
