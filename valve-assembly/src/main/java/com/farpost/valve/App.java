package com.farpost.valve;

import com.farpost.netty.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class App {

	private static final Logger log = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		log.info("Running proxy");

		Proxy proxy = new Proxy();
		proxy.addRemote(new InetSocketAddress("localhost", 8081));
		proxy.addRemote(new InetSocketAddress("localhost", 8082));
		proxy.addRemote(new InetSocketAddress("localhost", 8083));
		proxy.run();
	}
}
