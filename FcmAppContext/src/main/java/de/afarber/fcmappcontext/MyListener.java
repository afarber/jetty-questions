package de.afarber.fcmappcontext;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

public class MyListener implements WebSocketListener, Utils {
    private static final List<Session> SESSIONS = new CopyOnWriteArrayList<>();
    private Session mSession;

    private void disconnect() {
        SESSIONS.remove(mSession);
        if (mSession.isOpen()) {
            mSession.close();
        }
    }
    
    @Override
    public void onWebSocketConnect(Session session) {
        LOG.info("onWebSocketConnect: {}", session);
        mSession = session;
        SESSIONS.add(mSession);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        LOG.info("onWebSocketClose: {} {}", statusCode, reason);
        disconnect();
    }

    @Override
    public void onWebSocketError(Throwable ex) {
        LOG.warn("onWebSocketError: {}", ex);
        disconnect();
    }

    @Override
    public void onWebSocketBinary(byte[] arg0, int arg1, int arg2) {
        LOG.warn("onWebSocketBinary");
        disconnect();
    }
    
    @Override
    public void onWebSocketText(String str) {
        LOG.info("onWebSocketText: {}", str);
        // TODO send FCM notification
    }
}
