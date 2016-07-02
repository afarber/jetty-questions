package de.afarber;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class WsServlet extends WebSocketServlet
{
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(EchoListener.class);
    }
}

