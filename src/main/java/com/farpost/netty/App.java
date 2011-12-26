package com.farpost.netty;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newCachedThreadPool;

/**
 * Hello world!
 */
public class App {

	public static void main(String[] args) {
		ExecutorService executor = newCachedThreadPool();
		ChannelFactory factory = new NioServerSocketChannelFactory(executor, executor);
		final ClientSocketChannelFactory clientFactory = new NioClientSocketChannelFactory(executor, executor);

		final ServerBootstrap bootstrap = new ServerBootstrap(factory);
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new HttpRequestDecoder(), new HttpResponseEncoder(), new ClientHandler(clientFactory));
			}
		});

		bootstrap.bind(new InetSocketAddress(8080));
	}
}
