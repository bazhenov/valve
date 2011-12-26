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
		if (future.isSuccess()) {
			Channel channel = future.getChannel();
			requestContext.addServerChannel(channel);
			log.debug("Connected to: {}", channel.getRemoteAddress());
		} else {
			log.error("Connection failed", future.getCause());
		}
	}
}
