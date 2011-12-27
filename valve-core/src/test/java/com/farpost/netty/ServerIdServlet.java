package com.farpost.netty;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServerIdServlet extends HttpServlet {

	private final String id;

	public ServerIdServlet(int serverId) {
		id = "ServerID#" + serverId;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Content-type", "text/plain; charset=utf8");
		resp.getWriter().write(id);
	}

	public String getId() {
		return id;
	}
}
