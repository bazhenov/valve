package com.farpost.netty;

import org.jboss.netty.channel.*;

public class ReplyResponse extends SimpleChannelHandler{

	private final RequestContext requestContext;

	public ReplyResponse(RequestContext requestContext) {
		this.requestContext = requestContext;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		requestContext.getClientChannel().write(e.getMessage());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getCause().printStackTrace(System.err);
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		requestContext.getClientChannel().close();
	}
}
