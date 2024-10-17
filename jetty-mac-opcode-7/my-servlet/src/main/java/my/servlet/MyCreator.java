package my.servlet;

import org.eclipse.jetty.websocket.server.JettyServerUpgradeRequest;
import org.eclipse.jetty.websocket.server.JettyServerUpgradeResponse;
import org.eclipse.jetty.websocket.server.JettyWebSocketCreator;

public class MyCreator implements JettyWebSocketCreator {
    private final MyServlet mServlet;

    public MyCreator(MyServlet servlet) {
        mServlet = servlet;
    }

    @Override
    public Object createWebSocket(JettyServerUpgradeRequest req, JettyServerUpgradeResponse resp) {
        return new MyListener(mServlet);
    }
}
