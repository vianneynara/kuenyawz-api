package dev.realtards.kuenyawz.services.entity;

import dev.realtards.kuenyawz.dtos.closeddate.ClosedDateDto;
import dev.realtards.kuenyawz.dtos.closeddate.ClosedDatePatchDto;
import dev.realtards.kuenyawz.dtos.closeddate.ClosedDatePostDto;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface ClosedDateService {

	@Transactional(readOnly = true)
	List<ClosedDateDto> getAll();

	@Transactional(readOnly = true)
	Page<ClosedDateDto> getAll(Integer page, Integer pageSize);

	@Transactional(readOnly = true)
	List<ClosedDateDto> getAllBetween(Date from, Date to);

	@Transactional(readOnly = true)
	Page<ClosedDateDto> getAllBetween(Date from, Date to, Integer page, Integer pageSize);

	@Transactional(readOnly = true)
	ClosedDateDto getById(Long closedDateId);

	@Transactional(readOnly = true)
	ClosedDateDto getByDate(Date date);

	@Transactional
	ClosedDateDto create(Iterable<ClosedDatePostDto> closedDatePostDto);

	@Transactional
	ClosedDateDto update(Long closedDateId, ClosedDatePatchDto closedDatePatchDto);

	@Transactional
	void deleteById(Long closedDateId);

	@Transactional
	void deleteBetween(Date from, Date to);

	@Transactional(readOnly = true)
	boolean isDateAvailable(Date date);
}
