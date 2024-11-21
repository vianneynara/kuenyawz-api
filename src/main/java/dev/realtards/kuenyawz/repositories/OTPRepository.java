package dev.realtards.kuenyawz.repositories;

import dev.realtards.kuenyawz.entities.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTP, Long> {

	Optional<OTP> findByPhone(String phone);

	Optional<OTP> findByPhoneAndExpiresAtAfter(String phone, LocalDateTime expiresAt);

	@Transactional
	void deleteByPhone(String phone);

	@Transactional
	int deleteAllByPhone(String phone);
}
