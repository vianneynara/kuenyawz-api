package dev.kons.kuenyawz.services.entity;

import dev.kons.kuenyawz.dtos.closeddate.ClosedDateDto;
import dev.kons.kuenyawz.dtos.closeddate.ClosedDatePatchDto;
import dev.kons.kuenyawz.dtos.closeddate.ClosedDatePostDto;
import dev.kons.kuenyawz.entities.ClosedDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface ClosedDateService {
	/**
	 * Gets all closed dates as a list.
	 *
	 * @return {@link List} of {@link ClosedDateDto}
	 */
	@Transactional(readOnly = true)
	List<ClosedDateDto> getAll();

	/**
	 * Gets all closed dates as a page.
	 *
	 * @param pageable {@link Pageable} object
	 * @return {@link Page} of {@link ClosedDateDto}
	 */
	@Transactional(readOnly = true)
	Page<ClosedDateDto> getAll(Pageable pageable);

	/**
	 * Gets all closed dates as a page.
	 *
	 * @param page     page number
	 * @param pageSize number of items per page
	 * @return {@link Page} of {@link ClosedDateDto}
	 */
	@Transactional(readOnly = true)
	Page<ClosedDateDto> getAll(Integer page, Integer pageSize);

	/**
	 * Gets all closed dates between two dates.
	 *
	 * @param from start date
	 * @param to   end date
	 * @return {@link List} of {@link ClosedDateDto}
	 */
	@Transactional(readOnly = true)
	List<ClosedDateDto> getAllBetween(LocalDate from, LocalDate to);

	/**
	 * Gets all closed dates between two dates as a page.
	 *
	 * @param from     start date
	 * @param to       end date
	 * @param pageable {@link Pageable} object
	 * @return {@link Page} of {@link ClosedDateDto}
	 */
	@Transactional(readOnly = true)
	Page<ClosedDateDto> getAllBetween(LocalDate from, LocalDate to, Pageable pageable);

	/**
	 * Gets all closed dates between two dates as a page. (with page number and page size)
	 *
	 * @param from     start date
	 * @param to       end date
	 * @param page     page number
	 * @param pageSize number of items per page
	 * @return {@link Page} of {@link ClosedDateDto}
	 */
	@Transactional(readOnly = true)
	Page<ClosedDateDto> getAllBetween(LocalDate from, LocalDate to, Integer page, Integer pageSize);

	/**
	 * Gets all closed dates after a date.
	 *
	 * @param from start date
	 * @return {@link List} of {@link ClosedDateDto}
	 */
	@Transactional(readOnly = true)
	Page<ClosedDateDto> getAllAfter(LocalDate from, Pageable pageable);

	/**
	 * Gets all closed dates after a date as a page.
	 *
	 * @param from     start date
	 * @param page     page number
	 * @param pageSize number of items per page
	 * @return {@link Page} of {@link ClosedDateDto}
	 */
	@Transactional(readOnly = true)
	Page<ClosedDateDto> getAllAfter(LocalDate from, Integer page, Integer pageSize);

	/**
	 * Gets a closed date by its ID.
	 *
	 * @param closedDateId ID of the closed date
	 * @return {@link ClosedDateDto}
	 */
	@Transactional(readOnly = true)
	ClosedDateDto getById(Long closedDateId);

	/**
	 * Gets a closed date by its date.
	 *
	 * @param date date of the closed date
	 * @return {@link ClosedDateDto}
	 */
	@Transactional(readOnly = true)
	ClosedDateDto getByDate(LocalDate date);

	/**
	 * Creates new closed dates.
	 *
	 * @param closedDatePostDto {@link ClosedDatePostDto} object
	 * @return {@link ClosedDateDto}
	 */
	@Transactional
	Iterable<ClosedDateDto> create(Iterable<ClosedDatePostDto> closedDatePostDto);

	/**
	 * Creates new closed dates.
	 *
	 * @param closedDatePostDto {@link ClosedDate} object
	 * @return {@link ClosedDateDto}
	 */
	@Transactional
	Iterable<ClosedDate> save(Set<ClosedDate> closedDatePostDto);

	/**
	 * Updates a closed date.
	 *
	 * @param closedDateId       ID of the closed date
	 * @param closedDatePatchDto {@link ClosedDatePatchDto} object
	 * @return {@link ClosedDateDto}
	 */
	@Transactional
	ClosedDateDto update(Long closedDateId, ClosedDatePatchDto closedDatePatchDto);

	/**
	 * Deletes a closed date by its ID.
	 *
	 * @param closedDateId ID of the closed date
	 */
	@Transactional
	void deleteById(Long closedDateId);

	/**
	 * Deletes all closed dates between two dates.
	 *
	 * @param from start date
	 * @param to   end date
	 */
	@Transactional
	void deleteBetween(LocalDate from, LocalDate to);

	/**
	 * Checks if a date is available.
	 *
	 * @param date date to check
	 * @return {@code true} if the date is available, {@code false} otherwise
	 */
	@Transactional(readOnly = true)
	boolean isDateAvailable(LocalDate date);
}
