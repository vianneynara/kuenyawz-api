package dev.kons.kuenyawz.configurations;

import dev.kons.kuenyawz.services.entity.OTPService;
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
	private String timezone = "Asia/Jakarta";

	@Value("${application.otp-format:NUMERIC}")
	private OTPService.OTPType otpFormat = OTPService.OTPType.NUMERIC;

	@Value("${server.port:8081}")
	private String serverPort;

	@Value("#{'${application.accepted-image-extensions}'.split(',')}")
	private List<String> acceptedImageExtensions;

	private Seeder seeder = new Seeder();
	private Vendor vendor = new Vendor();
	private Database database = new Database();
	private Security security = new Security();
	private Midtrans midtrans = new Midtrans();

	// Initializing through dotenv
	@Autowired
	public void initialize(Dotenv dotenv) {
		this.version = dotenv.get("APP_VERSION", "0.0");
		this.repositoryUrl = dotenv.get("APP_REPOSITORY_URL", "https://github.com/vianneynara/*");

		// Override default seeder values if set in .env
		this.seeder.seedAccounts = Boolean.parseBoolean(dotenv.get("SEED_ACCOUNTS", seeder.getSeedAccounts() ? "true" : "false"));
		this.seeder.seedProducts = Boolean.parseBoolean(dotenv.get("SEED_PRODUCTS", seeder.getSeedProducts() ? "true" : "false"));

		this.vendor.instagram = dotenv.get("VENDOR_INSTAGRAM", null);
		this.vendor.email = dotenv.get("VENDOR_EMAIL", null);
		this.vendor.phone = dotenv.get("VENDOR_PHONE", null);
		this.vendor.address = dotenv.get("VENDOR_ADDRESS", null);
		this.vendor.latitude = Double.parseDouble(dotenv.get("VENDOR_LATITUDE", "0"));
		this.vendor.longitude = Double.parseDouble(dotenv.get("VENDOR_LONGITUDE", "0"));
		this.vendor.paymentFee = Double.parseDouble(dotenv.get("VENDOR_PAYMENT_FEE", "4000"));
		this.vendor.feePerKm = Double.parseDouble(dotenv.get("VENDOR_FEE_PER_KM", "3500"));

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

		this.midtrans.merchantId = dotenv.get("MIDTRANS_MERCHANT_ID", null);
		this.midtrans.serverKey = dotenv.get("MIDTRANS_SERVER_KEY", null);
		this.midtrans.baseUrlApp = dotenv.get("MIDTRANS_BASE_URL_APP", "https://app.sandbox.midtrans.com");
		this.midtrans.baseUrlApi = dotenv.get("MIDTRANS_BASE_URL_API", "https://api.sandbox.midtrans.com");
		this.midtrans.notificationUrl = dotenv.get("MIDTRANS_NOTIFICATION_URL", null);
		this.midtrans.finishUrl = dotenv.get("MIDTRANS_FINISH_URL", null);
		this.midtrans.unfinishUrl = dotenv.get("MIDTRANS_UNFINISH_URL", null);
		this.midtrans.errorUrl = dotenv.get("MIDTRANS_ERROR_URL, null");
	}

	public String getFullBaseUrl() {
		return httpProtocol + "://" + publicIp + ":" + serverPort;
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
	public static class Seeder {
		private Boolean seedAccounts = false;
		private Boolean seedProducts = false;
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
}
