package de.afarber;

import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@SuppressWarnings("serial")
public class MyHandler extends WebSocketHandler {
    private final static Pattern PATTERN = Pattern.compile("^([0-9.]+):([0-9]+)$"); 
    
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(MyListener.class);
    }
    
   public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 8080;
        
        if (args.length > 0) {
            Matcher matcher = PATTERN.matcher(args[0]);
            if (matcher.matches()) {
                host = matcher.group(1);
                port = Integer.parseInt(matcher.group(2));
            }
        }
        
        Server server = new Server(new InetSocketAddress(host, port));
        server.setHandler(new MyHandler());
        server.start();
        server.join();
    }
}