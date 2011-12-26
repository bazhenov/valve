package com.farpost.netty;

import org.jboss.netty.channel.Channel;

public class RequestContext {

	private final Channel clientChannel;
	private volatile Channel serverChannel;

	public RequestContext(Channel clientChannel) {
		this.clientChannel = clientChannel;
	}

	public void setServerChannel(Channel serverChannel) {
		this.serverChannel = serverChannel;
		clientChannel.setReadable(true);
	}

	public Channel getServerChannel() {
		return serverChannel;
	}

	public void suspendClientChannel() {
		clientChannel.setReadable(false);
	}

	public Channel getClientChannel() {
		return clientChannel;
	}
}
