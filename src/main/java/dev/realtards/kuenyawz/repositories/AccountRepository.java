package dev.realtards.kuenyawz.repositories;

import dev.realtards.kuenyawz.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

	Optional<Account> findByPhone(String phone);

	Optional<Account> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByPhone(String phone);
}
