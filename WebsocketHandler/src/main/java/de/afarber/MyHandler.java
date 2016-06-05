package de.afarber;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@SuppressWarnings("serial")
public class MyHandler extends WebSocketHandler {
    @Override
    public void configure(WebSocketServletFactory factory) {
        //factory.getPolicy().setIdleTimeout(10 * 60 * 1000);     // 10 minutes
        //factory.getPolicy().setMaxTextMessageSize(1 * 1024);    // 1 KByte
        //factory.getPolicy().setMaxTextMessageSize(64 * 1024);   // 64 KByte
        
        factory.register(MyListener.class);
    }
    
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        server.setHandler(new MyHandler());
        server.start();
        server.join();
    }
}