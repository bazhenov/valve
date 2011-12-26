package com.farpost.netty;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;

public class LogError implements ChannelFutureListener {

	private final String message;
	private final Logger log;

	public LogError(String message, Logger log) {
		this.message = message;
		this.log = log;
	}

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		Throwable cause = future.getCause();
		if (cause != null) {
			log.error(message, cause);
		}
	}
}
