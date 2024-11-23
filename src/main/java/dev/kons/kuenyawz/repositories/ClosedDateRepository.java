package dev.kons.kuenyawz.repositories;

import dev.kons.kuenyawz.entities.ClosedDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClosedDateRepository extends JpaRepository<ClosedDate, Long> {

	List<ClosedDate> findAllByDateBetween(LocalDate date, LocalDate date2);

	Page<ClosedDate> findAllByDateBetween(LocalDate from, LocalDate to, Pageable pageable);

	Page<ClosedDate> findAllByDateAfter(LocalDate from, Pageable pageable);

	Optional<ClosedDate> findByDate(LocalDate date);

	int deleteAllByDateBetween(LocalDate from, LocalDate to);

	boolean existsByDate(LocalDate date);
}
