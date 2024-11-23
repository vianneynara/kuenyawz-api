package dev.realtards.kuenyawz.services.entity;

import dev.realtards.kuenyawz.configurations.ApplicationProperties;
import dev.realtards.kuenyawz.dtos.auth.OtpRequestDto;
import dev.realtards.kuenyawz.dtos.auth.OtpVerifyDto;

import java.security.SecureRandom;

public interface OTPService {

	/**
	 * Send an OTP to the user's phone number.
	 *
	 * @param otpRequestDto {@link String} User's phone number
	 */
	void sendOTP(OtpRequestDto otpRequestDto);

	/**
	 * Verify the OTP sent to the user's phone number.
	 *
	 * @param otpVerifyDto {@link OtpVerifyDto} User's phone number and OTP
	 */
	boolean verifyOTP(OtpVerifyDto otpVerifyDto);

	/**
	 * Generate a random 6-digit OTP that expires in 5 minutes.
	 *
	 * @param otpType {@link OTPServiceImpl.OTPType} OTP type
	 * @return {@link String} The generated OTP
	 */
	String generateOTP(OTPServiceImpl.OTPType otpType);

	/**
	 * Verify the OTP sent to the user's phone number in the Redis.
	 *
	 * @param phone {@link String} User's phone number
	 * @param otp   {@link String} OTP code
	 */
	boolean matchStoredOTP(String phone, String otp);

	/**
	 * Helper inner class to separate OTP generation logic from the service class.
	 */
	class OTPGenerator {

		private static final String ALPHA_NUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
		private static final SecureRandom secureRandom = new SecureRandom();

		static String generateAlphaNumeric(ApplicationProperties properties) {
			int otpLength = properties.getSecurity().getOtpLength();
			StringBuilder rawOtp = new StringBuilder(otpLength);
			for (int i = 0; i < otpLength; i++)
				rawOtp.append(ALPHA_NUMERIC.charAt(secureRandom.nextInt(ALPHA_NUMERIC.length())));
			return rawOtp.toString();
		}

		static String generateNumeric(ApplicationProperties properties) {
			int otpLength = properties.getSecurity().getOtpLength();
			StringBuilder otp = new StringBuilder(otpLength);
			for (int i = 0; i < otpLength; i++)
				otp.append(secureRandom.nextInt(10));
			return otp.toString();
		}
	}

	enum OTPType {
		NUMERIC,
		ALPHA_NUMERIC;

		OTPType fromString(String type) {
			if (type == null) {
				return NUMERIC;
			}
			return switch (type.toUpperCase()) {
				case "NUMERIC" -> NUMERIC;
				case "ALPHA_NUMERIC" -> ALPHA_NUMERIC;
				default -> throw new IllegalArgumentException("Invalid OTP type: " + type);
			};
		}
	}
}
