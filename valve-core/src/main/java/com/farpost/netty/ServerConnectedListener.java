package com.farpost.netty;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerConnectedListener implements ChannelFutureListener {

	private final RequestContext requestContext;
	private final static Logger log = LoggerFactory.getLogger(ServerConnectedListener.class);

	public ServerConnectedListener(RequestContext requestContext) {
		this.requestContext = requestContext;
	}

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		Channel channel = future.getChannel();
		if (future.isSuccess()) {
			requestContext.serverChannelReady(channel);
			log.trace("Connected to: {}", channel.getRemoteAddress());
		} else {
			requestContext.serverChannelFailed(channel);
			log.warn("Connection failed", future.getCause());
		}
	}
}
