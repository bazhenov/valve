package com.farpost.netty;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServerIdServlet extends HttpServlet {

	private final int serverId;

	public ServerIdServlet(int serverId) {
		this.serverId = serverId;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getWriter().write("ServerID#" + serverId);
	}
}
