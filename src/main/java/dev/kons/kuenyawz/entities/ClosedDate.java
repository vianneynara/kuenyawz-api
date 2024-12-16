package dev.kons.kuenyawz.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import dev.kons.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ClosedDate extends Auditables {

	@Id
	@SnowFlakeIdValue(name = "custom_schedule_id")
	@Column(name = "closed_date_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
	private Long closedDateId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ClosureType closureType = ClosureType.CLOSED;

	@Column(nullable = false, unique = true)
	@Temporal(TemporalType.DATE)
	private LocalDate date;

	@Column
	private String reason;

	public enum ClosureType {
		@JsonProperty("CLOSED")
		CLOSED("CLOSED"),

		@JsonProperty("RESERVED")
		RESERVED("RESERVED"),

		@JsonProperty("PREP")
		PREP("PREP");

		final String closureType;

		ClosureType(String closureType) {
			this.closureType = closureType;
		}

		@JsonValue
		public String getClosureType() {
			return closureType;
		}

		@JsonCreator
		public static ClosureType fromString(String name) {
			if (name == null) return null;
			for (ClosureType type : ClosureType.values()) {
				if (type.closureType.equalsIgnoreCase(name) || type.name().equalsIgnoreCase(name)) {
					return type;
				}
			}
			throw new IllegalArgumentException(String.format("Invalid closure type: %s", name));
		}
	}
}
