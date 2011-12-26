package com.farpost.netty;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;

import java.net.InetSocketAddress;

import static org.jboss.netty.channel.Channels.pipeline;

public class ServerHandler extends SimpleChannelHandler {

	private final ChannelFactory factory;
	private int port = 8081;
	private String hostname = "localhost";

	public ServerHandler(ClientSocketChannelFactory factory) {
		this.factory = factory;
	}

	@Override
	public void channelOpen(final ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		final Channel clientChannel = e.getChannel();

		RequestContext requestContext = new RequestContext(clientChannel);
		requestContext.suspendClientChannel();
		ctx.setAttachment(requestContext);

		System.out.println("Inbound connection from: " + clientChannel.getRemoteAddress());

		ChannelFuture future = createServerChannel(requestContext, hostname, port);
		future.addListener(new ServerConnectedListener(requestContext));
	}

	private ChannelFuture createServerChannel(RequestContext context, String hostname, int port) {
		ClientBootstrap bootstrap = new ClientBootstrap(factory);
		bootstrap.setPipeline(pipeline(new HttpRequestEncoder(), new HttpResponseDecoder(), new ReplyResponse(context)));

		return bootstrap.connect(new InetSocketAddress(hostname, port));
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		HttpRequest request = (HttpRequest) e.getMessage();
		System.out.println("Proxying: " + request.getUri());
		RequestContext requestContext = (RequestContext) ctx.getAttachment();
		requestContext.getServerChannel().write(request);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getCause().printStackTrace(System.err);

		e.getChannel().close();
	}
}
