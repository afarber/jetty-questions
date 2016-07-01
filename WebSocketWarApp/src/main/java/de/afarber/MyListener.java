package de.afarber;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

public class MyListener implements WebSocketListener {
    private Session mSession;

    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len) {
        /* ignore */
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        mSession = null;
    }

    @Override
    public void onWebSocketConnect(Session session) {
        System.out.println("onWebSocketConnect: " + session);
        mSession = session;
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        cause.printStackTrace(System.err);
    }

    @Override
    public void onWebSocketText(String message) {
        System.out.println("onWebSocketText: " + message);
        if (mSession != null && mSession.isOpen()) {
            System.out.printf("Echoing back message [%s]%n", message);
            mSession.getRemote().sendString("ECHO: " + message, null);
        }
    }
}
