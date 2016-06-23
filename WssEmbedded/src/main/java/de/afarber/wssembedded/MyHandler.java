package de.afarber.wssembedded;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@SuppressWarnings("serial")
public class MyHandler extends WebSocketHandler {
    private final static String HOST = "127.0.0.1";
    private final static int WS_PORT = 8080;
    private final static int WSS_PORT = 8443;
    
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(MyListener.class);
    }
    
   public static void main(String[] args) throws Exception {
       
        Server server = new Server();
        server.setHandler(new MyHandler());
        
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(WSS_PORT);
        http_config.setOutputBufferSize(32768);
        http_config.setRequestHeaderSize(8192);
        http_config.setResponseHeaderSize(8192);
        http_config.setSendServerVersion(true);
        http_config.setSendDateHeader(false);
        
        HttpConfiguration https_config = new HttpConfiguration(http_config);
        https_config.addCustomizer(new SecureRequestCustomizer());
        
       // keytool -v -genkeypair -alias key1 -keyalg RSA -keysize 2048 -validity 3650 -keypass changeit -keystore keystore.jks -storepass changeit
       // java -cp "jetty-util\9.3.10.v20160621\*" org.eclipse.jetty.util.security.Password changeit
       
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath("keystore.jks");
        sslContextFactory.setKeyStorePassword("OBF:1vn21ugu1saj1v9i1v941sar1ugw1vo0"); // changeit
        
        ServerConnector wsConnector = new ServerConnector(server);
        wsConnector.setHost(HOST);
        wsConnector.setPort(WS_PORT);
        server.addConnector(wsConnector);
        
        ServerConnector wssConnector = new ServerConnector(server,
            new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
            new HttpConnectionFactory(https_config));
        
        wssConnector.setHost(HOST);
        wssConnector.setPort(WSS_PORT);
        server.addConnector(wssConnector);
        
        server.start();
        server.join();
        
    }
}