package de.afarber.wssclient;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

public class Main {
    public static void main(String[] args) {
        final String WS_URL = (
                args != null && 
                args.length > 0 && 
                args[0] != null && 
                args[0].length() > 10 ? args[0] : "ws://127.0.0.1:8080");
        
        MyListener socket = new MyListener("Hello world");
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setTrustAll(true); 
        WebSocketClient client = new WebSocketClient(sslContextFactory);

        try {
            client.start();
            URI uri = new URI(WS_URL);
            ClientUpgradeRequest cur = new ClientUpgradeRequest();
            client.connect(socket, uri, cur);
            // wait for closed socket connection.
            socket.awaitClose(5, TimeUnit.SECONDS);
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                client.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
