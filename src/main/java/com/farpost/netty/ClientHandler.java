package com.farpost.netty;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;

import java.net.InetSocketAddress;

import static org.jboss.netty.channel.Channels.pipeline;

public class ClientHandler extends SimpleChannelHandler {

	private final ChannelFactory factory;

	public ClientHandler(ClientSocketChannelFactory factory) {
		this.factory = factory;
	}

	@Override
	public void channelOpen(final ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		final Channel clientChannel = e.getChannel();

		RequestContext requestContext = new RequestContext(clientChannel, 2);
		requestContext.suspendClientChannel();
		ctx.setAttachment(requestContext);

		System.out.println("Inbound connection from: " + clientChannel.getRemoteAddress());

		createServerChannel(requestContext, "localhost", 8081);
		createServerChannel(requestContext, "localhost", 8082);
	}

	private ChannelFuture createServerChannel(RequestContext context, String hostname, int port) {
		ClientBootstrap bootstrap = new ClientBootstrap(factory);
		bootstrap.setPipeline(pipeline(new HttpRequestEncoder(), new HttpResponseDecoder(), new ServerHandler(context)));

		ChannelFuture future = bootstrap.connect(new InetSocketAddress(hostname, port));
		future.addListener(new ServerConnectedListener(context));
		return future;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		HttpRequest request = (HttpRequest) e.getMessage();
		System.out.println("Proxying: " + request.getUri());
		RequestContext requestContext = (RequestContext) ctx.getAttachment();
		requestContext.write(request);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getCause().printStackTrace(System.err);

		e.getChannel().close();
	}
}
