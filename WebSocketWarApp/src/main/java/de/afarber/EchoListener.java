package de.afarber;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class EchoListener implements WebSocketListener {
    private static final Logger LOG = Log.getLogger(EchoListener.class);
    private Session mSession;

    @Override
    @OnWebSocketMessage
    public void onWebSocketBinary(byte[] payload, int offset, int len) {
        LOG.info("onWebSocketBinary");
    }

    @Override
    @OnWebSocketClose
    public void onWebSocketClose(int statusCode, String reason) {
        LOG.info("onWebSocketClose {} {}", statusCode, reason);
        mSession = null;
    }

    @Override
    @OnWebSocketConnect
    public void onWebSocketConnect(Session session) {
        LOG.info("onWebSocketConnect {}", session);
        mSession = session;
    }

    @Override
    @OnWebSocketError
    public void onWebSocketError(Throwable cause) {
        LOG.warn("onWebSocketError {}", cause);
    }

    @Override
    @OnWebSocketMessage
    public void onWebSocketText(String message) {
        LOG.info("onWebSocketText {}", message);
        if (mSession != null && mSession.isOpen()) {
            mSession.getRemote().sendString("ECHO: " + message, null);
        }
    }
}
