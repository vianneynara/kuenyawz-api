package dev.kons.kuenyawz.services.entity;

import dev.kons.kuenyawz.configurations.ApplicationProperties;
import dev.kons.kuenyawz.dtos.closeddate.ClosedDateDto;
import dev.kons.kuenyawz.dtos.closeddate.ClosedDatePatchDto;
import dev.kons.kuenyawz.dtos.closeddate.ClosedDatePostDto;
import dev.kons.kuenyawz.entities.ClosedDate;
import dev.kons.kuenyawz.exceptions.InvalidRequestBodyValue;
import dev.kons.kuenyawz.mapper.ClosedDateMapper;
import dev.kons.kuenyawz.repositories.ClosedDateRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static dev.kons.kuenyawz.services.logic.AuthService.validateIsAdmin;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClosedDateServiceImpl implements ClosedDateService {

	private final ClosedDateMapper closedDateMapper;
	private final ClosedDateRepository closedDateRepository;
	private final ApplicationProperties properties;
	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Override
	public List<ClosedDateDto> getAll() {
		List<ClosedDate> closedDates = closedDateRepository.findAll();
		return closedDates.stream()
			.map(this::toDto)
			.toList();
	}

	@Override
	public Page<ClosedDateDto> getAll(Pageable pageable) {
		Page<ClosedDate> closedDates = closedDateRepository.findAll(pageable);
		return closedDates.map(this::toDto);
	}

	@Override
	public Page<ClosedDateDto> getAll(Integer page, Integer pageSize) {
		page = (page == null || page < 1) ? 0 : page;
		pageSize = (pageSize == null || pageSize < 1 || pageSize > 1000) ? 10 : pageSize;
		Pageable pageable = Pageable.ofSize(pageSize).withPage(page);

		Page<ClosedDate> closedDates = closedDateRepository.findAll(pageable);
		return closedDates.map(this::toDto);
	}

	@Override
	public List<ClosedDateDto> getAllBetween(LocalDate from, LocalDate to) {
		from = from == null ? LocalDate.now() : from;
		to = to == null ? LocalDate.now() : to;
		validateDateRange(from, to);
		List<ClosedDate> closedDates = closedDateRepository.findAllByDateBetween(
			from,
			to
		);
		return closedDates.stream()
			.map(this::toDto)
			.toList();
	}

	@Override
	public Page<ClosedDateDto> getAllBetween(LocalDate from, LocalDate to, Pageable pageable) {
		return findAllBetween(from, to, pageable);
	}

	@Override
	public Page<ClosedDateDto> getAllBetween(LocalDate from, LocalDate to, Integer page, Integer pageSize) {
		page = (page == null || page < 1) ? 0 : page;
		pageSize = (pageSize == null || pageSize < 1 || pageSize > 1000) ? 10 : pageSize;
		Sort sort = Sort.by(Sort.Order.asc("date"));
		Pageable pageable = PageRequest.of(page, pageSize, sort);

		return findAllBetween(from, to, pageable);
	}

	/**
	 * Helper class to find all closed dates between two dates.
	 */
	private Page<ClosedDateDto> findAllBetween(LocalDate from, LocalDate to, Pageable pageable) {
		from = from == null ? LocalDate.now() : from;
		to = to == null ? LocalDate.now() : to;
		validateDateRange(from, to);
		Page<ClosedDate> closedDates = closedDateRepository.findAllByDateBetween(from, to, pageable);
		return closedDates.map(this::toDto);
	}

	@Override
	public Page<ClosedDateDto> getAllAfter(LocalDate from, Pageable pageable) {
		from = from == null ? LocalDate.now() : from;
		Page<ClosedDate> closedDates = closedDateRepository.findAllByDateAfter(
			from,
			pageable
		);
		return closedDates.map(this::toDto);
	}

	@Override
	public Page<ClosedDateDto> getAllAfter(LocalDate from, Integer page, Integer pageSize) {
		page = (page == null || page < 1) ? 0 : page;
		pageSize = (pageSize == null || pageSize < 1 || pageSize > 1000) ? 10 : pageSize;
		Pageable pageable = Pageable.ofSize(pageSize).withPage(page);

		from = from == null ? LocalDate.now() : from;
		Page<ClosedDate> closedDates = closedDateRepository.findAllByDateAfter(
			from,
			pageable
		);
		return closedDates.map(this::toDto);
	}

	@Override
	public ClosedDateDto getById(Long closedDateId) {
		ClosedDate closedDate = closedDateRepository.findById(closedDateId)
			.orElseThrow(() -> new EntityNotFoundException("Closed Date Not Found By Id: " + closedDateId));
		return toDto(closedDate);
	}

	@Override
	public ClosedDateDto getByDate(LocalDate date) {
		ClosedDate closedDate = closedDateRepository.findByDate(date)
			.orElseThrow(() -> new EntityNotFoundException("Closed Date Not Found By Date: " + date));
		return toDto(closedDate);
	}

	@Override
	public Iterable<ClosedDateDto> create(Iterable<ClosedDatePostDto> closedDatePostDto) {
		validateIsAdmin();

		for (ClosedDatePostDto dto : closedDatePostDto) {
			validateNoDuplicateDate(LocalDate.from(dateTimeFormatter.parse(dto.getDate())));
		}
		List<ClosedDate> closedDates = new ArrayList<>();
		for (ClosedDatePostDto dto : closedDatePostDto) {
			ClosedDate closedDate = closedDateMapper.toEntity(dto);
			ClosedDate savedClosedDate = closedDateRepository.save(closedDate);
			closedDates.add(savedClosedDate);
		}
		return closedDates.stream()
			.map(this::toDto)
			.toList();
	}

	@Override
	public Iterable<ClosedDate> save(Set<ClosedDate> closedDatePostDto) {
		for (ClosedDate dto : closedDatePostDto) {
			validateNoDuplicateDate(dto.getDate());
		}
		return closedDateRepository.saveAll(closedDatePostDto);
	}

	@Override
	public ClosedDateDto update(Long closedDateId, ClosedDatePatchDto closedDatePatchDto) {
		validateIsAdmin();

		validateNoDuplicateDate(LocalDate.from(dateTimeFormatter.parse(closedDatePatchDto.getDate())));
		ClosedDate closedDate = closedDateRepository.findById(closedDateId)
			.orElseThrow(() -> new EntityNotFoundException("Closed Date Not Found By Id: " + closedDateId));
		closedDate = patchFromDto(closedDate, closedDatePatchDto);
		ClosedDate savedClosedDate = closedDateRepository.save(closedDate);
		return toDto(savedClosedDate);
	}

	@Override
	public void deleteById(Long closedDateId) {
		closedDateRepository.deleteById(closedDateId);
	}

	@Override
	public void deleteBetween(LocalDate from, LocalDate to) {
		closedDateRepository.deleteAllByDateBetween(from, to);
	}

	@Override
	public boolean isDateAvailable(LocalDate date) {
		boolean result = closedDateRepository.existsByDate(date);
		return !result;
	}

	public ClosedDateDto toDto(ClosedDate closedDate) {
		return ClosedDateDto.builder()
			.closedDateId(closedDate.getClosedDateId())
			.date(closedDate.getDate())
			.type(closedDate.getClosureType())
			.reason(closedDate.getReason())
			.build();
	}

	public ClosedDate patchFromDto(ClosedDate closedDate, ClosedDatePatchDto closedDatePatchDto) {
		if (closedDatePatchDto.getType() != null) {
			closedDate.setClosureType(ClosedDate.ClosureType.fromString(closedDatePatchDto.getType()));
		}
		if (closedDatePatchDto.getDate() != null) {
			closedDate.setDate(LocalDate.from(dateTimeFormatter.parse(closedDatePatchDto.getDate())));
		}
		if (closedDatePatchDto.getReason() != null) {
			closedDate.setReason(closedDatePatchDto.getReason());
		}
		return closedDate;
	}

	private void validateDateRange(@NotNull LocalDate from, @NotNull LocalDate to) {
		if (from.isAfter(to)) {
			throw new InvalidRequestBodyValue("From date must be equal or before to date");
		}
	}

	private void validateNoDuplicateDate(LocalDate date) {
		if (closedDateRepository.existsByDate(date)) {
			throw new InvalidRequestBodyValue("Date already exists at " + date.format(dateTimeFormatter));
		}
	}
}
