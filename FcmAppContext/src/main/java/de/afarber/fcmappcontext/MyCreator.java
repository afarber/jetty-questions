package de.afarber.fcmappcontext;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

public class MyCreator implements WebSocketCreator {
    private final HttpClient mHttpClient;

    MyCreator(HttpClient httpClient) {
        mHttpClient = httpClient;
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        return new MyListener(mHttpClient);
    }
}
