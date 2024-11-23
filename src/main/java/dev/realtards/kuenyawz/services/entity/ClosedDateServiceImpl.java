package dev.realtards.kuenyawz.services.entity;

import dev.realtards.kuenyawz.dtos.closeddate.ClosedDateDto;
import dev.realtards.kuenyawz.dtos.closeddate.ClosedDatePatchDto;
import dev.realtards.kuenyawz.dtos.closeddate.ClosedDatePostDto;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public class ClosedDateServiceImpl implements ClosedDateService {
	@Override
	public List<ClosedDateDto> getAll() {
		return List.of();
	}

	@Override
	public Page<ClosedDateDto> getAll(Integer page, Integer pageSize) {
		return null;
	}

	@Override
	public List<ClosedDateDto> getAllBetween(Date from, Date to) {
		return List.of();
	}

	@Override
	public Page<ClosedDateDto> getAllBetween(Date from, Date to, Integer page, Integer pageSize) {
		return null;
	}

	@Override
	public ClosedDateDto getById(Long closedDateId) {
		return null;
	}

	@Override
	public ClosedDateDto getByDate(Date date) {
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
	public void deleteBetween(Date from, Date to) {

	}

	@Override
	public boolean isDateAvailable(Date date) {
		return false;
	}
}
