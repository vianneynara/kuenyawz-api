package dev.realtards.kuenyawz.services;

import dev.realtards.kuenyawz.configurations.ApplicationProperties;
import dev.realtards.kuenyawz.dtos.auth.OtpRequestDto;
import dev.realtards.kuenyawz.dtos.auth.OtpVerifyDto;
import dev.realtards.kuenyawz.entities.OTP;
import dev.realtards.kuenyawz.exceptions.InvalidCredentialsException;
import dev.realtards.kuenyawz.repositories.OTPRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static dev.realtards.kuenyawz.services.OTPService.OTPGenerator.generateNumeric;

@Service
@RequiredArgsConstructor
@Slf4j
public class OTPServiceImpl implements OTPService {

	private final ApplicationProperties properties;
	private final OTPRepository otpRepository;

	public static final OTPType DEFAULT_OTP_TYPE = OTPType.ALPHA_NUMERIC;

	@Override
	public void sendOTP(OtpRequestDto otpRequestDto) {
		String otp = generateOTP(DEFAULT_OTP_TYPE);
		otpRepository.findByPhone(otpRequestDto.getPhone())
			.ifPresent(otpRepository::delete);
		otpRepository.save(OTP.builder()
			.phone(otpRequestDto.getPhone())
			.otp(otp)
			.ipAddress(otpRequestDto.getIpAddress())
			.expiresAt(LocalDateTime.now().plusSeconds(properties.getSecurity().getOtpExpireSeconds()))
			.build()
		);

		log.info("Simulating sending OTP {} to request: {}", otp, otpRequestDto);
		{
			// TODO: Send OTP to the user's phone number via a WhatsApp message
		}
	}

	@Override
	public boolean verifyOTP(OtpVerifyDto otpVerifyDto) {
		return matchStoredOTP(otpVerifyDto.getPhone(), otpVerifyDto.getOtp());
	}

	@Override
	public String generateOTP(OTPType otpType) {
		return switch (otpType) {
			case NUMERIC -> generateNumeric(properties);
			case ALPHA_NUMERIC -> OTPGenerator.generateAlphaNumeric(properties);
		};
	}

	@Override
	public boolean matchStoredOTP(String phone, String otp) {
		Optional<OTP> storedOtp = otpRepository.findByPhoneAndExpiresAtAfter(phone, LocalDateTime.now());
		System.out.println(storedOtp);
		if (storedOtp.isEmpty() || !storedOtp.get().getOtp().equals(otp)) {
			throw new InvalidCredentialsException("Invalid OTP");
		}
		otpRepository.deleteAllByPhone(phone);
		return true;
	}
}