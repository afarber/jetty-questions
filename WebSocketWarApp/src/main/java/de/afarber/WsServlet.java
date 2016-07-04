package de.afarber;

import javax.servlet.annotation.WebServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@SuppressWarnings("serial")
@WebServlet(name = "WsServlet", urlPatterns = { "/ws" })
public class WsServlet extends WebSocketServlet
{
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(EchoListener.class);
    }
}

