package com.farpost.netty;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

public class PrintError implements ChannelFutureListener {

	private final String message;

	public PrintError(String message) {
		this.message = message;
	}

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		if (future.getCause() != null) {
			System.err.println(message);
			future.getCause().printStackTrace(System.err);
		}
	}
}
