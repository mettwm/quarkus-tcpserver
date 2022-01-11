package org.example.tcpserver;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.micrometer.core.instrument.MeterRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class EchoServer {

	@Inject
	MeterRegistry registry;

	@Inject
	MonitoringRegistryService monitoringRegistryService;

	private ServerBootstrap bootstrap;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private Set<Channel> channels = new HashSet<>();

	public ServerBootstrap createBootstrap() throws Exception {
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new EchoServerHandler(registry, monitoringRegistryService));
					}
				}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

		bootstrap.validate();
		return bootstrap;
	}

	public void start(int port) throws Exception {
		log.debug("Starting server at " + port);
		bootstrap = createBootstrap();
		channels.clear();
		Channel channel = bootstrap.bind(port).sync().channel();
		log.info("At start channel is active:" + channel.isActive() + " and open:" + channel.isOpen());
		channel.closeFuture().addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				log.debug("Server channel was closed at:" + future.channel().localAddress());
				log.info("At closing channel his state is active:" + channel.isActive() + " and open:"
						+ channel.isOpen());
			}
		});
		channels.add(channel);
		log.info("Server channel started and is listening at " + channel.localAddress());
	}

	public void stop() {
		log.info("Stopping server...");
		try {
			for (Channel channel : channels) {
				channel.close().sync().await(5, TimeUnit.SECONDS);
				log.infof("Channel " + channel.localAddress() + " was Stopped.");
			}
			bossGroup.shutdownGracefully().sync().await(5, TimeUnit.SECONDS);
			bossGroup = null;
			log.info("Boos group was Stopped.");
			workerGroup.shutdownGracefully().sync().await(5, TimeUnit.SECONDS);
			workerGroup = null;
			log.info("Worker group was Stopped.");
			log.info("Server was Stopped.");
		} catch (InterruptedException e) {
			log.error("Error while stopping the server", e);
			Thread.currentThread().interrupt();
		}
	}
}
