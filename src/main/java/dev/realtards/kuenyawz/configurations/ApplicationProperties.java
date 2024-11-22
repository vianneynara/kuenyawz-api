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
	private Integer maxVariantQuantity = 250;
	private String publicIp = "localhost";
	private String httpProtocol = "http";

	@Value("${server.port:8081}")
	private String serverPort;

	@Value("#{'${application.accepted-image-extensions}'.split(',')}")
	private List<String> acceptedImageExtensions;

	private Database database = new Database();
	private Security security = new Security();

	// Initializing through dotenv
	@Autowired
	public void initialize(Dotenv dotenv) {
		this.version = dotenv.get("APP_VERSION", "0.0");
		this.repositoryUrl = dotenv.get("APP_REPOSITORY_URL", "https://github.com/vianneynara/*");

		this.database.url = dotenv.get("DB_URL", "jdbc:postgresql://localhost:5432/kuenyawz");
		this.database.username = dotenv.get("DB_USERNAME", "kuenyawz");
		this.database.password = dotenv.get("DB_PASSWORD", "kuenyawz");

		this.security.jwtSecret = dotenv.get("JWT_SECRET", "secret");
		this.security.jwtTokenExpSeconds = Long.parseLong(dotenv.get("JWT_ACCESS_EXP_SECONDS", "3600"));
		this.security.jwtRefreshDays = Long.parseLong(dotenv.get("REFRESH_TOKEN_EXP_DAYS", "7"));
		this.security.otpPhoneNumber = dotenv.get("OTP_PHONE_NUMBER", null);
		this.security.fonnteApiToken = dotenv.get("FONNTE_API_TOKEN", null);
		this.security.otpExpireSeconds = Long.parseLong(dotenv.get("OTP_EXPIRE_SECONDS", "300"));
		this.security.otpLength = Integer.parseInt(dotenv.get("OTP_LENGTH", "6"));
	}

	public String getFullBaseUrl() {
		return httpProtocol + "://" + publicIp + ":" + serverPort;
	}

	public Database database() {
		return database;
	}

	public Security security() {
		return security;
	}

	@Getter
	@Setter
	public static class Database {
		private String url;
		private String username;
		private String password;
	}

	@Getter
	@Setter
	public static class Security {
		private String jwtSecret;
		private long jwtTokenExpSeconds;
		private long jwtRefreshDays;
		private String otpPhoneNumber;
		private String fonnteApiToken;
		private long otpExpireSeconds;
		private int otpLength;
	}
}
