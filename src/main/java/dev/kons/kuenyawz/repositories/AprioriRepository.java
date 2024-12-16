package dev.kons.kuenyawz.repositories;

import dev.kons.kuenyawz.entities.Apriori;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AprioriRepository extends JpaRepository<Apriori, Long> {

	Optional<Apriori> findByProductId(Long productId);
}
