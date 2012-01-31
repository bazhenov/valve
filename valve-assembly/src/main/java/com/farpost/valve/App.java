package com.farpost.valve;

import com.farpost.netty.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import static java.lang.Integer.parseInt;

public class App {

	private static final Logger log = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		int port = 8080;

		Proxy proxy = new Proxy();
		proxy.setPort(port);
		for (String host : args) {
			String parts[] = host.split(":");
			log.info("Running proxy on port {}", port);
			proxy.addRemote(new InetSocketAddress(parts[0], parseInt(parts[1])));
		}
		log.info("Running proxy on port {}", port);
		proxy.run();
	}
}
