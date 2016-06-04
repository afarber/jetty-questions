package de.afarber;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
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
    
    /*
     * Create the file src/main/resources/database.properties containing:
     * database: test
     * user: test
     * password: test
    */
    
    public static void main(String[] args) throws Exception {
        final String url = "jdbc:postgresql://127.0.0.1/";
        Properties props = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = loader.getResourceAsStream("database.properties")) {
            props.load(is);
        }
        Connection conn = DriverManager.getConnection(url, props);

        Server server = new Server(8080);
        server.setHandler(new MyHandler());
        server.start();
        server.join();
    }
}