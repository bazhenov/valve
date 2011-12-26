package com.farpost.netty;

import java.net.InetSocketAddress;

/**
 * Hello world!
 */
public class App {

	public static void main(String[] args) {
		Proxy proxy = new Proxy();
		proxy.addRemote(new InetSocketAddress("localhost", 8081));
		proxy.addRemote(new InetSocketAddress("localhost", 8082));
		proxy.addRemote(new InetSocketAddress("localhost", 8083));
		proxy.run();
	}
}
