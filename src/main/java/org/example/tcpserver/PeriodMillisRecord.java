package org.example.tcpserver;

import java.time.Duration;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeriodMillisRecord {
	// start time in milliseconds
	long startTimeMillis = -1L;
	// end time in milliseconds
	long endTimeMillis = -1L;

	public PeriodMillisRecord(PeriodMillisRecord origin) {
		this.startTimeMillis = origin.getStartTimeMillis();
		this.endTimeMillis = origin.getEndTimeMillis();
	}

	public Duration getDurationMillis() {
		if (endTimeMillis >= startTimeMillis) {
			return Duration.ofMillis(endTimeMillis - startTimeMillis);
		} else {
			return null;
		}
	}

	public PeriodMillisRecord setStartTimeToCurrent() {
		startTimeMillis = Instant.now().toEpochMilli();
		return this;
	}

	public PeriodMillisRecord setEndTimeToCurrent() {
		endTimeMillis = Instant.now().toEpochMilli();
		return this;
	}

	public Duration setEndTimeToCurrentAndGetDurationMillis() {
		setEndTimeToCurrent();
		return getDurationMillis();
	}

}
