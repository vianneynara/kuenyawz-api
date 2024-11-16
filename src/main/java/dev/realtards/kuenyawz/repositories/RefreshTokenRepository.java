package dev.realtards.kuenyawz.repositories;

import dev.realtards.kuenyawz.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByToken(String token);
	Optional<RefreshToken> findByAccountId(Long accountId);
	Optional<RefreshToken> findByUsername(String username);
}
