package com.farpost.netty;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

public class ServerConnectedListener implements ChannelFutureListener {

	private final RequestContext requestContext;

	public ServerConnectedListener(RequestContext requestContext) {
		this.requestContext = requestContext;
	}

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		if (future.isSuccess()) {
			System.out.println("Connected to: " + future.getChannel().getRemoteAddress());
			requestContext.setServerChannel(future.getChannel());
		} else {
			System.out.println("Connection failed");
			requestContext.getClientChannel().close();
		}
	}
}
