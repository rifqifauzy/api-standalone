package com.api.standalone;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import com.api.standalone.util.JerseyConfiguration;

public class App {

	private static final String JERSEY_SERVLET_NAME = "jersey-container-servlet";
	
	public static void main(String[] args) {
		launch();
	}
	
	public static void launch() {
		/*setting port*/
		String port = System.getenv("PORT");
        if (port == null || port.isEmpty()) {
            port = "8080";
        }

        Server server = new Server(Integer.valueOf(port));
        ServletContextHandler context = new ServletContextHandler(server, "/");

        /*setting prefix api*/
        ServletHolder servlet = new ServletHolder(JERSEY_SERVLET_NAME,
                new ServletContainer(new JerseyConfiguration()));
        context.addServlet(servlet, "/api/*");

        try {
            server.start();
            server.join();
        } catch (Exception e) {
			e.printStackTrace();
		} finally {
            server.destroy();
        }
	}
}
