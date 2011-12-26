package com.farpost.netty;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class RequestContext {

	private final Channel clientChannel;
	private final int serverChannelsNo;
	private final List<Channel> serverChannels = new LinkedList<Channel>();
	private final AtomicReference<Channel> winnerChannel = new AtomicReference<Channel>();

	public RequestContext(Channel clientChannel, int serverChannelsNo) {
		this.clientChannel = clientChannel;
		this.serverChannelsNo = serverChannelsNo;
	}

	public synchronized void addServerChannel(Channel serverChannel) {
		this.serverChannels.add(serverChannel);
		if (serverChannels.size() >= serverChannelsNo) {
			clientChannel.setReadable(true);
		}
	}

	public List<Channel> getServerChannels() {
		return serverChannels;
	}

	public void suspendClientChannel() {
		clientChannel.setReadable(false);
	}

	public Channel getClientChannel() {
		return clientChannel;
	}

	public synchronized void write(Object message) {
		for (Channel c : serverChannels) {
			c.write(message).addListener(new PrintError("Unable to write to server"));
		}
	}

	public boolean isWinnerOrFirst(Channel channel) {
		return winnerChannel.get() == channel || winnerChannel.compareAndSet(null, channel);
	}

	public boolean isWinner(Channel channel) {
		return winnerChannel.get() == channel;
	}
}
