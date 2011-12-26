package com.farpost.netty;

import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.netty.buffer.ChannelBuffers.EMPTY_BUFFER;
import static org.jboss.netty.channel.ChannelFutureListener.CLOSE;

public class ServerHandler extends SimpleChannelHandler {

	private final RequestContext requestContext;
	private static final Logger log = LoggerFactory.getLogger(ServerHandler.class);

	public ServerHandler(RequestContext requestContext) {
		this.requestContext = requestContext;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		if (requestContext.isWinnerOrFirst(e.getChannel())) {
			Channel client = requestContext.getClientChannel();
			Object message = e.getMessage();
			log.debug("{} -> {}: {}", new Object[]{e.getRemoteAddress(), client.getRemoteAddress(), message.getClass()});
			client.write(message).addListener(new LogError("Unable to write to client", log));
		}
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		if (requestContext.isWinner(e.getChannel())) {
			log.debug("Closing client socket");
			closeOnFlush(requestContext.getClientChannel());
		}
	}

	private void closeOnFlush(Channel channel) {
		if (channel.isConnected()) {
			channel.write(EMPTY_BUFFER).addListener(CLOSE);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		Throwable cause = e.getCause();
		log.error(cause.getMessage(), cause);
		e.getChannel().close();
	}
}
