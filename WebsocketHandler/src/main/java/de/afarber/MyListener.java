package de.afarber;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.ajax.JSON;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

public class MyListener implements WebSocketListener {
    private static final int UNSUPPORTED_DATA = 1003;
    private static final Map<String, Session> sClients = new ConcurrentHashMap<>();
    private static final Logger LOG = Log.getLogger(MyListener.class);
    
    private String mName;
    private Session mSession;

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        sClients.remove(mName);
        
        LOG.info("onWebSocketClose: {} - {}", statusCode, reason);
        messageAll(mName + " has disconnected from " + getClass().getName());

        mName = null;
        mSession = null;
    }

    @Override
    public void onWebSocketConnect(Session session) {
        mName = "Client-" + ThreadLocalRandom.current().nextInt(1, 1000 + 1);
        mSession = session;
        sClients.put(mName,mSession);
        
        LOG.info("onWebSocketConnect: {}", session);
        messageAll(mName + " has connected to " + getClass().getName());
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        LOG.warn("onWebSocketError", cause);
    }

    @Override
    public void onWebSocketText(String str) {
        if (mSession != null && mSession.isOpen()) {
            LOG.info("onWebSocketText: {}", str);
            messageAll(mName + ": " + str);
        }
    }

    @Override
    public void onWebSocketBinary(byte[] arg0, int arg1, int arg2) {
        mSession.close(UNSUPPORTED_DATA, null);
    }
    
    private void messageAll(String str) {
        for (Session s: sClients.values()) {
            s.getRemote().sendStringByFuture(str);
        }
    }
}
