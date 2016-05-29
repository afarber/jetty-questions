package org.eclipse.jetty.demo.listener;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

public class MyListener implements WebSocketListener {
    private static final int UNSUPPORTED_DATA = 1003;
    private static final Logger LOG = Log.getLogger(MyListener.class);
    private Session mSession;

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        mSession = null;
        LOG.info("WebSocket Close: {} - {}",statusCode,reason);
    }

    @Override
    public void onWebSocketConnect(Session session) {
        mSession = session;
        LOG.info("WebSocket Connect: {}",session);
        mSession.getRemote().sendString("You are now connected to " + this.getClass().getName(),null);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        LOG.warn("WebSocket Error",cause);
    }

    @Override
    public void onWebSocketText(String message) {
        if (mSession != null && mSession.isOpen()) {
            LOG.info("Echoing back text message [{}]",message);
            mSession.getRemote().sendString(message,null);
        }
    }

    @Override
    public void onWebSocketBinary(byte[] arg0, int arg1, int arg2) {
        mSession.close(UNSUPPORTED_DATA, null);
    }
}
