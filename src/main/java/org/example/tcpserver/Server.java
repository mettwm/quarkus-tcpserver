package org.example.tcpserver;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class Server {

	@Inject
	EchoServer echoServer;

	void onStart(@Observes StartupEvent startupEvent) throws Exception {
		log.info("StartupEvent - starting tcp service ...");
		echoServer.start(8080);
	}

	void onStop(@Observes ShutdownEvent shutdownEvent) {
		log.info("ShutdownEvent - stoping tcp service ...");
		echoServer.stop();
	}
}
