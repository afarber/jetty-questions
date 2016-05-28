package de.afarber;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class MyServlet extends WebSocketServlet {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler();
        context.addServlet(MyServlet.class, "/");
        server.setHandler(context);
        server.start();
        server.join();
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(MyListener.class);
    }
}

