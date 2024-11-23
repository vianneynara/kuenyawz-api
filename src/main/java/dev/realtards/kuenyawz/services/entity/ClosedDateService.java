package dev.realtards.kuenyawz.services.entity;

import dev.realtards.kuenyawz.dtos.closeddate.ClosedDateDto;
import dev.realtards.kuenyawz.dtos.closeddate.ClosedDatePatchDto;
import dev.realtards.kuenyawz.dtos.closeddate.ClosedDatePostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface ClosedDateService {

	@Transactional(readOnly = true)
	List<ClosedDateDto> getAll();

	@Transactional(readOnly = true)
	Page<ClosedDateDto> getAll(Pageable pageable);

	@Transactional(readOnly = true)
	List<ClosedDateDto> getAllBetween(LocalDate from, LocalDate to);

	@Transactional(readOnly = true)
	Page<ClosedDateDto> getAllBetween(LocalDate from, LocalDate to, Pageable pageable);

	@Transactional(readOnly = true)
	ClosedDateDto getById(Long closedDateId);

	@Transactional(readOnly = true)
	ClosedDateDto getByDate(LocalDate date);

	@Transactional
	ClosedDateDto create(Iterable<ClosedDatePostDto> closedDatePostDto);

	@Transactional
	ClosedDateDto update(Long closedDateId, ClosedDatePatchDto closedDatePatchDto);

	@Transactional
	void deleteById(Long closedDateId);

	@Transactional
	void deleteBetween(LocalDate from, LocalDate to);

	@Transactional(readOnly = true)
	boolean isDateAvailable(LocalDate date);
}
