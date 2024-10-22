package dev.realtards.wzsnacknbites.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UptimeTracker {

	private final LocalDateTime startTime;

	public UptimeTracker() {
		this.startTime = LocalDateTime.now();
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public String hasBeenRunningFor() {
		LocalDateTime now = LocalDateTime.now();
		long seconds = startTime.until(now, java.time.temporal.ChronoUnit.SECONDS);
		long minutes = startTime.until(now, java.time.temporal.ChronoUnit.MINUTES);
		long hours = startTime.until(now, java.time.temporal.ChronoUnit.HOURS);
		long days = startTime.until(now, java.time.temporal.ChronoUnit.DAYS);
		return String.format("%d days, %d hours, %d minutes, %d seconds", days, hours, minutes, seconds);
	}
}