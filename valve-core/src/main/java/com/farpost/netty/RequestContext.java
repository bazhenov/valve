package com.farpost.netty;

import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class RequestContext {

	private static final Logger log = LoggerFactory.getLogger(RequestContext.class);

	private final Channel clientChannel;
	private final int serverChannelsNo;
	private final List<Channel> readyServerChannels = new LinkedList<Channel>();
	private List<Channel> failedServerChannels = new LinkedList<Channel>();
	private final AtomicReference<Channel> winnerChannel = new AtomicReference<Channel>();

	public RequestContext(Channel clientChannel, int serverChannelsNo) {
		this.clientChannel = clientChannel;
		this.serverChannelsNo = serverChannelsNo;
	}

	public synchronized void serverChannelReady(Channel serverChannel) {
		this.readyServerChannels.add(serverChannel);
		checkClient();
	}

	public synchronized void serverChannelFailed(Channel channel) {
		this.failedServerChannels.add(channel);
		checkClient();
	}

	private void checkClient() {
		if (readyServerChannels.size() + failedServerChannels.size() >= serverChannelsNo) {
			log.trace("Mark client channel as readable");
			clientChannel.setReadable(true);
		}
	}

	public synchronized void suspendClientChannel() {
		clientChannel.setReadable(false);
	}

	public Channel getClientChannel() {
		return clientChannel;
	}

	public synchronized void write(Object message) {
		for (Channel c : readyServerChannels) {
			c.write(message).addListener(new LogError("Unable to write to server", log));
		}
	}

	public boolean isWinnerOrFirst(Channel channel) {
		return winnerChannel.get() == channel || winnerChannel.compareAndSet(null, channel);
	}

	public boolean isWinner(Channel channel) {
		return winnerChannel.get() == channel;
	}
}
