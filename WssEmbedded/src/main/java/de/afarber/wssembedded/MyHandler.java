package de.afarber.wssembedded;

import org.eclipse.jetty.http.HttpVersion;
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
       
       // keytool  -genkey -alias key1 -keyalg RSA -keypass password1 -keystore keystore.jks
       // java -cp "jetty-util\9.3.9.v20160517\*" org.eclipse.jetty.util.security.Password password1
       
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath("keystore.jks");
        sslContextFactory.setKeyStorePassword("OBF:1l1a1s3g1yf41xtv20731xtn1yf21s3m1kxs"); // password1
        
        /*
        sslContextFactory.setKeyManagerPassword("OBF:1l1a1s3g1yf41xtv20731xtn1yf21s3m1kxs");
        sslContextFactory.setTrustStorePath("keystore.jks");
        sslContextFactory.setTrustStorePassword("OBF:1l1a1s3g1yf41xtv20731xtn1yf21s3m1kxs");
        sslContextFactory.setExcludeCipherSuites(
                "SSL_RSA_WITH_DES_CBC_SHA",
                "SSL_DHE_RSA_WITH_DES_CBC_SHA", 
                "SSL_DHE_DSS_WITH_DES_CBC_SHA",
                "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
                "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
                "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
                "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA"
        );
        */
        
        Server server = new Server();
        server.setHandler(new MyHandler());
        
        ServerConnector wsConnector = new ServerConnector(server);
        wsConnector.setHost(HOST);
        wsConnector.setPort(WS_PORT);
        server.addConnector(wsConnector);
        
        ServerConnector wssConnector = new ServerConnector(server,
            new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()));
        wssConnector.setHost(HOST);
        wssConnector.setPort(WSS_PORT);
        server.addConnector(wssConnector);
        
        server.start();
        server.join();
        
    }
}