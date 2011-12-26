package com.farpost.netty;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static org.jboss.netty.channel.Channels.pipeline;

public class ClientHandler extends SimpleChannelHandler {

	private final ChannelFactory factory;
	private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);

	public ClientHandler(ClientSocketChannelFactory factory) {
		this.factory = factory;
	}

	@Override
	public void channelOpen(final ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		final Channel clientChannel = e.getChannel();

		RequestContext requestContext = new RequestContext(clientChannel, 2);
		requestContext.suspendClientChannel();
		ctx.setAttachment(requestContext);

		log.debug("Inbound connection from: {}", clientChannel.getRemoteAddress());

		createServerChannel(requestContext, new InetSocketAddress("localhost", 8081));
		createServerChannel(requestContext, new InetSocketAddress("localhost", 8082));
	}

	private ChannelFuture createServerChannel(RequestContext context, SocketAddress remote) {
		ClientBootstrap bootstrap = new ClientBootstrap(factory);
		bootstrap.setPipeline(pipeline(new HttpRequestEncoder(), new HttpResponseDecoder(), new ServerHandler(context)));

		ChannelFuture future = bootstrap.connect(remote);
		future.addListener(new ServerConnectedListener(context));
		return future;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		HttpRequest request = (HttpRequest) e.getMessage();

		log.debug("Proxying: {}", request.getUri());
		RequestContext requestContext = (RequestContext) ctx.getAttachment();
		requestContext.write(request);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		Throwable cause = e.getCause();
		log.error(cause.getMessage(), cause);
		e.getChannel().close();
	}
}
