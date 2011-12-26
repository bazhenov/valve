package com.farpost.netty;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import static org.jboss.netty.channel.Channels.pipeline;

public class ClientHandler extends SimpleChannelHandler {

	private final ChannelFactory factory;
	private final List<SocketAddress> remotes;
	private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);

	public ClientHandler(ClientSocketChannelFactory factory, List<SocketAddress> remotes) {
		this.factory = factory;
		this.remotes = new ArrayList<SocketAddress>(remotes);
	}

	@Override
	public void channelOpen(final ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		final Channel clientChannel = e.getChannel();

		RequestContext requestContext = new RequestContext(clientChannel, remotes.size());
		requestContext.suspendClientChannel();
		ctx.setAttachment(requestContext);

		log.debug("Inbound connection from: {}", clientChannel.getRemoteAddress());

		for (SocketAddress remote : remotes) {
			createServerChannel(requestContext, remote);
		}
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
