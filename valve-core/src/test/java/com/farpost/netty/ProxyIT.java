package com.farpost.netty;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServlet;
import java.net.InetSocketAddress;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isOneOf;

public class ProxyIT {

	Random rnd = new Random();
	private Server bravo;
	private Server charlie;
	private RestTemplate template;
	private Proxy proxy;

	private String bravoId;
	private String charlieId;

	@BeforeClass
	public void setUp() throws Exception {
		ServerIdServlet bravoServlet = new ServerIdServlet(rnd.nextInt());
		bravoId = bravoServlet.getId();
		bravo = createHttpServer(8091, bravoServlet);
		bravo.start();

		ServerIdServlet charlieServlet = new ServerIdServlet(rnd.nextInt());
		charlieId = charlieServlet.getId();
		charlie = createHttpServer(8092, charlieServlet);
		charlie.start();

		proxy = new Proxy();
		proxy.addRemote(new InetSocketAddress("localhost", 8091));
		proxy.addRemote(new InetSocketAddress("localhost", 8092));
		proxy.setPort(8090);
		proxy.run();

		template = new RestTemplate();
	}

	@AfterClass
	public void tearDown() throws Exception {
		bravo.stop();
		bravo.join();
		charlie.stop();
		charlie.join();
	}

	@Test(invocationCount = 1000)
	public void testProxy() throws InterruptedException {
		String result = template.getForObject("http://localhost:8090/", String.class);
		assertThat(result, isOneOf(bravoId, charlieId));
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
