package dev.kons.kuenyawz.services.entity;

import dev.kons.kuenyawz.configurations.ApplicationProperties;
import dev.kons.kuenyawz.dtos.auth.OtpRequestDto;
import dev.kons.kuenyawz.dtos.auth.OtpVerifyDto;
import dev.kons.kuenyawz.entities.OTP;
import dev.kons.kuenyawz.exceptions.InvalidCredentialsException;
import dev.kons.kuenyawz.repositories.OTPRepository;
import dev.kons.kuenyawz.services.logic.WhatsappApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static dev.kons.kuenyawz.services.entity.OTPService.OTPGenerator.generateNumeric;

@Service
@RequiredArgsConstructor
@Slf4j
public class OTPServiceImpl implements OTPService {

	private final ApplicationProperties properties;
	private final OTPRepository otpRepository;
	private final WhatsappApiService whatsappApiService;

	private final AccountService accountService;

	@Override
	public void sendOTP(OtpRequestDto otpRequestDto) {
		accountService.validatePhoneNoDuplicate(otpRequestDto.getPhone());

		String newOtp = generateOTP(properties.getOtpFormat());
		otpRepository.findByPhone(otpRequestDto.getPhone())
			.ifPresent(otpRepository::delete);
		otpRepository.save(OTP.builder()
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

		log.info("OTP {} sent to {}", newOtp, otpRequestDto.getPhone());
		whatsappApiService.send(otpRequestDto.getPhone(), otpMessage, "62");
	}

	@Override
	public boolean verifyOTP(OtpVerifyDto otpVerifyDto) {
		accountService.validatePhoneNoDuplicate(otpVerifyDto.getPhone());

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
		if (storedOtp.isEmpty() || !storedOtp.get().getOtp().equals(otp)) {
			throw new InvalidCredentialsException("Invalid OTP");
		}
		otpRepository.deleteAllByPhone(phone);
		return true;
	}
}