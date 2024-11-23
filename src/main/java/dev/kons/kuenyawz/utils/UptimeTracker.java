package dev.kons.kuenyawz.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class UptimeTracker {

	private final LocalDateTime startTime;

	public UptimeTracker() {
		this.startTime = LocalDateTime.now();
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public String hasBeenRunningSince() {
		return getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	public String hasBeenRunningFor() {
		LocalDateTime now = LocalDateTime.now();
		long seconds = startTime.until(now, java.time.temporal.ChronoUnit.SECONDS);
		int minutes = ((int) seconds) / 60;
		int hours = minutes / 60;
		int days = hours / 24;

		seconds = seconds % 60;
		minutes = minutes % 60;
		hours = hours % 24;

		return String.format("%d days, %d hours, %d minutes, %d seconds", days, hours, minutes, seconds);
	}
}