package org.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * @author Arjen Poutsma
 */
public class Driver {

	public static void main(String[] args) throws Exception {
		Server jettyServer = new Server();
		AsyncIssueServlet servlet = new AsyncIssueServlet();
		ServletHolder servletHolder = new ServletHolder(servlet);

		ServletContextHandler
				contextHandler = new ServletContextHandler(jettyServer, "", false, false);
		contextHandler.addServlet(servletHolder, "/");

		ServerConnector connector = new ServerConnector(jettyServer);
		connector.setPort(8080);
		jettyServer.addConnector(connector);

		jettyServer.start();
	}

}
