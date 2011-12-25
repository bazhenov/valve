package com.farpost.netty;

import org.jboss.netty.channel.*;

public class ReplyResponse extends SimpleChannelHandler{

	private final Channel channel;

	public ReplyResponse(Channel channel) {
		this.channel = channel;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		channel.write(e.getMessage());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getCause().printStackTrace(System.err);
	}
}
