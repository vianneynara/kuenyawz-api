package dev.kons.kuenyawz.configurations;

import dev.kons.kuenyawz.services.entity.OTPService;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This is not an explicit configuration class, but a necessary component to read
 * the application properties of 'application' in `application.yaml` and helps
 * to initialize the properties through dotenv.
 */
@Component
@RefreshScope
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
@Getter
@Setter
@Slf4j
public class ApplicationProperties {

	// Fields
	private String version;
	private String repositoryUrl;
	private String productImagesDir = "product-images";
	private String baseUrl = "http://localhost:8081";
	private Integer maxVariantQuantity = 250;
	private Boolean isContainerized = false;
	private String httpProtocol = "http";
	private String publicIp = "localhost";
	private String timezone = "Asia/Jakarta";

	@Value("${application.otp-format:NUMERIC}")
	private OTPService.OTPType otpFormat = OTPService.OTPType.NUMERIC;

	@Value("${server.port:8081}")
	private String serverPort;

	@Value("#{'${application.accepted-image-extensions}'.split(',')}")
	private List<String> acceptedImageExtensions;

	private Frontend frontend = new Frontend();
	private Seeder seeder = new Seeder();
	private Vendor vendor = new Vendor();
	private Database database = new Database();
	private Security security = new Security();
	private Midtrans midtrans = new Midtrans();

	// Initializing through dotenv
	@Autowired
	public void initialize(Dotenv dotenv) {
		this.isContainerized = Boolean.parseBoolean(getEnv("APP_CONTAINERIZED", "false", dotenv));
		this.httpProtocol = getEnv("APP_HTTP_PROTOCOL", "http", dotenv);
		this.publicIp = getEnv("APP_SERVER_HOST", "localhost", dotenv);
		this.serverPort = getEnv("APP_SERVER_PORT", "8081", dotenv);

		this.version = getEnv("APP_VERSION", "0.0", dotenv);
		this.repositoryUrl = getEnv("APP_REPOSITORY_URL", "https://github.com/vianneynara/*", dotenv);

		this.frontend.baseUrl = getEnv("FRONTEND_BASE_URL", "http://localhost:5173", dotenv);

		this.seeder.seedAccounts = Boolean.parseBoolean(getEnv("SEED_ACCOUNTS", "true", dotenv));
		this.seeder.seedProducts = Boolean.parseBoolean(getEnv("SEED_PRODUCTS", "true", dotenv));

		this.vendor.instagram = getEnv("VENDOR_INSTAGRAM", null, dotenv);
		this.vendor.email = getEnv("VENDOR_EMAIL", null, dotenv);
		this.vendor.phone = getEnv("VENDOR_PHONE", null, dotenv);
		this.vendor.address = getEnv("VENDOR_ADDRESS", null, dotenv);
		this.vendor.latitude = Double.parseDouble(getEnv("VENDOR_LATITUDE", "0", dotenv));
		this.vendor.longitude = Double.parseDouble(getEnv("VENDOR_LONGITUDE", "0", dotenv));
		this.vendor.paymentFee = Double.parseDouble(getEnv("VENDOR_PAYMENT_FEE", "4000", dotenv));
		this.vendor.feePerKm = Double.parseDouble(getEnv("VENDOR_FEE_PER_KM", "3500", dotenv));

		this.database.url = readDbUrl(dotenv);
		this.database.username = getEnv("DB_USERNAME", "kuenyawz", dotenv);
		this.database.password = getEnv("DB_PASSWORD", "kuenyawz", dotenv);

		this.security.jwtSecret = getEnv("JWT_SECRET", "secret", dotenv);
		this.security.jwtTokenExpSeconds = Long.parseLong(getEnv("JWT_ACCESS_EXP_SECONDS", "3600", dotenv));
		this.security.jwtRefreshDays = Long.parseLong(getEnv("REFRESH_TOKEN_EXP_DAYS", "7", dotenv));
		this.security.otpPhoneNumber = getEnv("OTP_PHONE_NUMBER", null, dotenv);
		this.security.fonnteApiToken = getEnv("FONNTE_API_TOKEN", null, dotenv);
		this.security.otpExpireSeconds = Long.parseLong(getEnv("OTP_EXPIRE_SECONDS", "300", dotenv));
		this.security.otpLength = Integer.parseInt(getEnv("OTP_LENGTH", "6", dotenv));

		this.midtrans.merchantId = getEnv("MIDTRANS_MERCHANT_ID", null, dotenv);
		this.midtrans.serverKey = getEnv("MIDTRANS_SERVER_KEY", null, dotenv);
		this.midtrans.baseUrlApp = getEnv("MIDTRANS_BASE_URL_APP", "https://app.sandbox.midtrans.com", dotenv);
		this.midtrans.baseUrlApi = getEnv("MIDTRANS_BASE_URL_API", "https://api.sandbox.midtrans.com", dotenv);
		this.midtrans.notificationUrl = getEnv("MIDTRANS_NOTIFICATION_URL", null, dotenv);
		this.midtrans.finishUrl = getEnv("MIDTRANS_FINISH_URL", "http://localhost:8081/api", dotenv);
		this.midtrans.unfinishUrl = getEnv("MIDTRANS_UNFINISH_URL", "http://localhost:8081/api", dotenv);
		this.midtrans.errorUrl = getEnv("MIDTRANS_ERROR_URL", "http://localhost:8081/api", dotenv);

		// Print all properties
		printAllProperties();
	}

	/**
	 * Reads the database URL from the environment variables or dotenv.
	 * By default, it will use localhost:5432/kuenyawz as the database URL.
	 */
	private String readDbUrl(Dotenv dotenv) {
		var dbHost = getEnv("DB_HOST", "localhost", dotenv);
		var dbPort = getEnv("DB_PORT", "5432", dotenv);
		var dbName = getEnv("DB_NAME", "kuenyawz", dotenv);

		return "jdbc:postgresql:"
			+ "//" + dbHost
			+ ":" + dbPort
			+ "/" + dbName;
	}

	public String getFullBaseUrl() {
		return httpProtocol + "://" + publicIp + ":" + serverPort;
	}

	/**
	 * Gets the environment variable from the system or dotenv.
	 *
	 * @return {@link String} value of the environment variable
	 */
	private String getEnv(String key, String defaultValue, Dotenv dotenv) {
		// Prioritize system environment variables
		String systemEnvValue = System.getenv(key);
		if (systemEnvValue != null) {
			log.info("Using SYSENV for key: {}", key);
			return systemEnvValue;
		}
		// Fallback to Dotenv
		log.info("Using dotenv for key: {}", key);
		return dotenv.get(key, defaultValue);
	}

	public Frontend frontend() {
		return frontend;
	}

	public Seeder seeder() {
		return seeder;
	}

	public Vendor vendor() {
		return vendor;
	}

	public Database database() {
		return database;
	}

	public Security security() {
		return security;
	}

	public Midtrans midtrans() {
		return midtrans;
	}

	@Getter
	@Setter
	public static class Frontend {
		private String baseUrl;
	}

	@Getter
	@Setter
	public static class Seeder {
		private Boolean seedAccounts;
		private Boolean seedProducts;
	}

	@Getter
	@Setter
	public static class Vendor {
		private String instagram;
		private String email;
		private String phone;
		private String address;
		private Double latitude;
		private Double longitude;
		private Double paymentFee;
		private Double feePerKm;
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

	@Getter
	@Setter
	public static class Midtrans {
		private String merchantId;
		private String serverKey;
		private String baseUrlApp;
		private String baseUrlApi;
		private String notificationUrl;

		// Redirect URLs
		private String finishUrl;
		private String unfinishUrl;
		private String errorUrl;
	}

	private void printAllProperties() {
		System.out.println("Properties:");
		System.out.println(" - version: " + version);
		System.out.println(" - repositoryUrl: " + repositoryUrl);
		System.out.println(" - productImagesDir: " + productImagesDir);
		System.out.println(" - baseUrl: " + baseUrl);
		System.out.println(" - maxVariantQuantity: " + maxVariantQuantity);
		System.out.println(" - publicIp: " + publicIp);
		System.out.println(" - httpProtocol: " + httpProtocol);
		System.out.println(" - timezone: " + timezone);
		System.out.println(" - otpFormat: " + otpFormat);
		System.out.println(" - serverPort: " + serverPort);
		System.out.println(" - acceptedImageExtensions: " + acceptedImageExtensions);

		System.out.println("Frontend:");
		System.out.println(" - baseUrl: " + frontend.baseUrl);

		System.out.println("Seeder:");
		System.out.println(" - seedAccounts: " + seeder.seedAccounts);
		System.out.println(" - seedProducts: " + seeder.seedProducts);

		System.out.println("Vendor:");
		System.out.println(" - instagram: " + vendor.instagram);
		System.out.println(" - email: " + vendor.email);
		System.out.println(" - phone: " + vendor.phone);
		System.out.println(" - address: " + vendor.address);
		System.out.println(" - latitude: " + vendor.latitude);
		System.out.println(" - longitude: " + vendor.longitude);
		System.out.println(" - paymentFee: " + vendor.paymentFee);
		System.out.println(" - feePerKm: " + vendor.feePerKm);

		System.out.println("Database:");
		System.out.println(" - url: " + database.url);
		System.out.println(" - username: " + database.username);
		System.out.println(" - password: " + database.password);

		System.out.println("Security:");
		System.out.println(" - jwtSecret: " + security.jwtSecret);
		System.out.println(" - jwtTokenExpSeconds: " + security.jwtTokenExpSeconds);
		System.out.println(" - jwtRefreshDays: " + security.jwtRefreshDays);
		System.out.println(" - otpPhoneNumber: " + security.otpPhoneNumber);
		System.out.println(" - fonnteApiToken: " + security.fonnteApiToken);
		System.out.println(" - otpExpireSeconds: " + security.otpExpireSeconds);
		System.out.println(" - otpLength: " + security.otpLength);

		System.out.println("Midtrans:");
		System.out.println(" - merchantId: " + midtrans.merchantId);
		System.out.println(" - serverKey: " + midtrans.serverKey);
		System.out.println(" - baseUrlApp: " + midtrans.baseUrlApp);
		System.out.println(" - baseUrlApi: " + midtrans.baseUrlApi);
		System.out.println(" - notificationUrl: " + midtrans.notificationUrl);
		System.out.println(" - finishUrl: " + midtrans.finishUrl);
	}
}
