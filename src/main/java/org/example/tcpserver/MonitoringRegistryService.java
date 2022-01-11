package org.example.tcpserver;

import java.time.Duration;
import java.util.Map;

import io.micrometer.core.instrument.Timer;

public interface MonitoringRegistryService {

	Map<String, Timer> getTimers();

	<T> void updateTimer(String timerName, Duration duration);

}
