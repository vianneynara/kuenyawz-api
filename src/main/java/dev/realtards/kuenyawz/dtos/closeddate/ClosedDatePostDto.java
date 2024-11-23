package dev.realtards.kuenyawz.dtos.closeddate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "Closed Date Creation", description = "Request body to create closed date")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClosedDatePostDto {

	@Schema(description = "Date of closure (ISO-8601)", example = "2021-12-01")
	@NotNull(message = "Date is required")
	@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Invalid date format")
	private String date;

	@Schema(description = "Type of closure", example = "CLOSED")
	@NotNull(message = "Closure type is required")
	@Pattern(regexp = "^(CLOSED|RESERVED)$", message = "Invalid closure type")
	private String type;

	@Schema(description = "Reason for closure", example = "It's rizzmas")
	private String reason;
}
