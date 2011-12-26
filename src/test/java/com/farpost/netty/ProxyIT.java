package com.farpost.netty;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServlet;
import java.util.Random;

public class ProxyIT {

	Random rnd = new Random();
	private Server bravo;
	private Server charlie;

	@BeforeMethod
	public void setUp() throws Exception {
		bravo = createHttpServer(8091, new ServerIdServlet(rnd.nextInt()));
		charlie = createHttpServer(8092, new ServerIdServlet(rnd.nextInt()));
		charlie.start();
		bravo.start();
	}

	@AfterMethod
	public void tearDown() throws Exception {
		bravo.stop();
		bravo.join();
		charlie.stop();
		charlie.join();
	}

	@Test
	public void testProxy() throws InterruptedException {
		Thread.sleep(10000);
	}

	private static Server createHttpServer(int port, HttpServlet servlet) {
		Server server = new Server(port);
		ServletHolder holder = new ServletHolder();
		holder.setServlet(servlet);
		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(holder, "/");
		server.addHandler(handler);
		return server;
	}
}
