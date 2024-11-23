package dev.realtards.kuenyawz.entities;

import dev.realtards.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClosedDate extends Auditables {

	@Id
	@SnowFlakeIdValue(name = "custom_schedule_id")
	@Column(name = "closed_date_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
	private Long closedDateId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ClosureType type = ClosureType.CLOSED;

	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private Date date;

	@Column
	private String reason;

	public enum ClosureType {
		CLOSED("CLOSED"),
		RESERVED("RESERVED");

		String value;

		ClosureType(String name) {
			this.value = name;
		}

		ClosureType fromString(String name) {
			for (ClosureType type : ClosureType.values()) {
				if (type.value.equals(name)) {
					return type;
				}
			}
			throw new IllegalArgumentException(String.format("Invalid closure type: %s", name));
		}
	}
}
