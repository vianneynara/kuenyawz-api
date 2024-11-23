package dev.realtards.kuenyawz.services.entity;

import dev.realtards.kuenyawz.dtos.closeddate.ClosedDateDto;
import dev.realtards.kuenyawz.dtos.closeddate.ClosedDatePatchDto;
import dev.realtards.kuenyawz.dtos.closeddate.ClosedDatePostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public class ClosedDateServiceImpl implements ClosedDateService {
	@Override
	public List<ClosedDateDto> getAll() {
		return List.of();
	}

	@Override
	public Page<ClosedDateDto> getAll(Pageable pageable) {
		return null;
	}

	@Override
	public List<ClosedDateDto> getAllBetween(LocalDate from, LocalDate to) {
		return List.of();
	}

	@Override
	public Page<ClosedDateDto> getAllBetween(LocalDate from, LocalDate to, Pageable pageable) {
		return null;
	}

	@Override
	public ClosedDateDto getById(Long closedDateId) {
		return null;
	}

	@Override
	public ClosedDateDto getByDate(LocalDate date) {
		return null;
	}

	@Override
	public ClosedDateDto create(Iterable<ClosedDatePostDto> closedDatePostDto) {
		return null;
	}

	@Override
	public ClosedDateDto update(Long closedDateId, ClosedDatePatchDto closedDatePatchDto) {
		return null;
	}

	@Override
	public void deleteById(Long closedDateId) {

	}

	@Override
	public void deleteBetween(LocalDate from, LocalDate to) {

	}

	@Override
	public boolean isDateAvailable(LocalDate date) {
		return false;
	}
}
