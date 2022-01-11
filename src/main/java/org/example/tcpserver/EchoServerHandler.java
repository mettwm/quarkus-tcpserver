package org.example.tcpserver;

import java.time.Duration;
import java.util.Random;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

	MeterRegistry registry;
	MonitoringRegistryService monitoringRegistryService;
	Timer standardTimer;

	public EchoServerHandler(MeterRegistry registry, MonitoringRegistryService monitoringRegistryService) {
		this.registry = registry;
		this.monitoringRegistryService = monitoringRegistryService;
		this.standardTimer = Timer.builder("tcp.request.standardperiod").publishPercentiles(0.5, 0.75, 0.95, 0.99)
				.distributionStatisticExpiry(Duration.ofMinutes(5)).register(registry);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		byte[] data = new byte[2048];
		final ByteBuf out = ctx.alloc().buffer(2048);

		int i = 0;
		ByteBuf in = (ByteBuf) msg;
		try {
			while (in.isReadable()) {
				data[i] = in.readByte();
				i++;
			}
		} finally {
			ReferenceCountUtil.release(msg);
		}

		log.debug("Received message:" + new String(data));

		Random rand = new Random();
		int x = rand.nextInt(50);

		// some counter
		registry.counter("tcp.requests.count").increment();

		// generate random timer duration
		Duration duration = Duration.ofMillis(x);
		monitoringRegistryService.updateTimer("tcp.request.period", duration);

		// and standard one
		standardTimer.record(duration);

		out.writeBytes(data);
		ctx.write(out);
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
