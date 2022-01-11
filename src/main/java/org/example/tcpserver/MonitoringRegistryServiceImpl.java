package org.example.tcpserver;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class MonitoringRegistryServiceImpl implements MonitoringRegistryService {

	private static final double[] PERCENTILES = { 0.5, 0.75, 0.95, 0.99 };
	private Map<String, Timer> timerMap = new ConcurrentHashMap<>();

	@Inject
	MeterRegistry registry;

	@Override
	public Map<String, Timer> getTimers() {
		return timerMap;
	}

	@Override
	public <T> void updateTimer(String timerName, Duration duration) {

		Timer timer = timerMap.get(timerName);

		if (timer != null) {
			log.debug("Metric key was found as a " + timerName);
			timer.record(duration);
		} else {
			log.debug("Metric key was not found as a " + timerName);
			Timer newTimer = Timer.builder(timerName).publishPercentiles(PERCENTILES)
					.distributionStatisticExpiry(Duration.ofMinutes(5)).register(registry);
			timerMap.put(timerName, newTimer);
			newTimer.record(duration);
		}
	}

}
