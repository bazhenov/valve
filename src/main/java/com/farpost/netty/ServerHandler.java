package com.farpost.netty;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;

public class ServerHandler extends SimpleChannelHandler {

	private final RequestContext requestContext;

	public ServerHandler(RequestContext requestContext) {
		this.requestContext = requestContext;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		if (requestContext.isWinnerOrFirst(e.getChannel())) {
			System.out.println(e.getRemoteAddress() + " response " + e.getMessage().getClass());
			requestContext.getClientChannel().write(e.getMessage()).addListener(new PrintError("Unable to write to client"));
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getCause().printStackTrace(System.err);
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		if (requestContext.isWinner(e.getChannel())) {
			System.out.println("Closing client socket");
			closeOnFlush(requestContext.getClientChannel());
		}
	}

	private void closeOnFlush(Channel channel) {
		if (channel.isConnected()) {
			channel.write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		}
	}
}
