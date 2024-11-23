package dev.realtards.kuenyawz.entities;

import dev.realtards.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomSchedule extends Auditables {

	@Id
	@SnowFlakeIdValue(name = "custom_schedule_id")
	@Column(name = "custom_schedule_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
	private Long customScheduleId;

	@Column(nullable = false)
	private ScheduleType scheduleType = ScheduleType.CLOSED;

	@Column
	private String scheduleName;

	@Column
	private Date date;

	public enum ScheduleType {
		CLOSED("CLOSED"),
		RESERVED("RESERVED");

		String value;

		ScheduleType(String name) {
			this.value = name;
		}

		ScheduleType fromString(String name) {
			for (ScheduleType type : ScheduleType.values()) {
				if (type.value.equals(name)) {
					return type;
				}
			}
			return null;
		}
	}
}
