package dev.kons.kuenyawz.dtos.closeddate;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.kons.kuenyawz.entities.ClosedDate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Schema(name = "Closed Date Transfer Object", description = "Response containing closed date information")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClosedDateDto {

	@Schema(description = "Identifier of the closed date", example = "12345")
	private Long closedDateId;

	@Schema(description = "Date of closure (ISO-8601)", example = "2021-12-01")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	@Schema(description = "Type of closure", example = "CLOSED")
	private ClosedDate.ClosureType type;

	@Schema(description = "Reason for closure", example = "It's rizzmas")
	private String reason;
}
