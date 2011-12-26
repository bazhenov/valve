package com.farpost.netty;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newCachedThreadPool;
import static org.jboss.netty.channel.Channels.pipeline;

public class Proxy implements Runnable {

	private int port = 8080;
	private final List<SocketAddress> remotes = new LinkedList<SocketAddress>();

	public void setPort(int port) {
		this.port = port;
	}

	public void addRemote(SocketAddress remote) {
		remotes.add(remote);
	}

	@Override
	public void run() {
		ExecutorService executor = newCachedThreadPool();
		ChannelFactory factory = new NioServerSocketChannelFactory(executor, executor);
		final ClientSocketChannelFactory clientFactory = new NioClientSocketChannelFactory(executor, executor);

		final ServerBootstrap bootstrap = new ServerBootstrap(factory);
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return pipeline(new HttpRequestDecoder(), new HttpResponseEncoder(),
					new ClientHandler(clientFactory, remotes));
			}
		});

		bootstrap.bind(new InetSocketAddress(port));
	}
}
