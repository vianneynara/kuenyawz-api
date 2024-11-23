package dev.realtards.kuenyawz.services.entity;

import dev.realtards.kuenyawz.configurations.ApplicationProperties;
import dev.realtards.kuenyawz.dtos.auth.OtpRequestDto;
import dev.realtards.kuenyawz.dtos.auth.OtpVerifyDto;
import dev.realtards.kuenyawz.entities.OTP;
import dev.realtards.kuenyawz.exceptions.InvalidCredentialsException;
import dev.realtards.kuenyawz.repositories.OTPRepository;
import dev.realtards.kuenyawz.services.logic.WhatsappApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static dev.realtards.kuenyawz.services.entity.OTPService.OTPGenerator.generateNumeric;

@Service
@RequiredArgsConstructor
@Slf4j
public class OTPServiceImpl implements OTPService {

	private final ApplicationProperties properties;
	private final OTPRepository otpRepository;
	private final WhatsappApiService whatsappApiService;

	public static final OTPType DEFAULT_OTP_TYPE = OTPType.ALPHA_NUMERIC;

	@Override
	public void sendOTP(OtpRequestDto otpRequestDto) {
		String newOtp = generateOTP(DEFAULT_OTP_TYPE);
		otpRepository.findByPhone(otpRequestDto.getPhone())
			.ifPresent(otpRepository::delete);
		OTP otp = otpRepository.save(OTP.builder()
			.phone(otpRequestDto.getPhone())
			.otp(newOtp)
			.ipAddress(otpRequestDto.getIpAddress())
			.expiresAt(LocalDateTime.now().plusSeconds(properties.getSecurity().getOtpExpireSeconds()))
			.build()
		);

		String otpMessage = String.format(
			"Kode verifikasi %s anda adalah:\n %s\n\nKode ini berlaku selama %s menit.",
			"https://kuenyawz.com",
			newOtp,
			properties.getSecurity().getOtpExpireSeconds() / 60
		);


		whatsappApiService.send(otpRequestDto.getPhone(), otpMessage, "62");
//		String response = whatsappApiService.send(otpRequestDto.getPhone(), otpMessage, "62");
//		log.warn("Response from WhatsApp API: {}", response);
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