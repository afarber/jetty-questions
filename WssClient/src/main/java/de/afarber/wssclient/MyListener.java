package de.afarber.wssclient;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

public class MyListener implements WebSocketListener {
    private Session mSession;
    private final String mRequest;
    private final CountDownLatch mCloseLatch;

    public MyListener(String str) {
        mCloseLatch = new CountDownLatch(1);
        mRequest = str;
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return mCloseLatch.await(duration, unit);
    }
    
    private void disconnect() {
        if (mSession != null && mSession.isOpen()) {
            mSession.close();
        }
        
        mCloseLatch.countDown();
    }
    
    @Override
    public void onWebSocketConnect(Session session) {
        System.out.printf("onWebSocketConnect: %s%n", session.getRemoteAddress());
        
        try {
            System.out.printf("REQUEST: %n%s%n", mRequest);

            session.getRemote().sendString(mRequest);
        } catch (IOException ex) {
            System.err.println(ex);
            return;
        }
        
        mSession = session;
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        System.out.printf("onWebSocketClose: %d %s%n", statusCode, reason);
    }

    @Override
    public void onWebSocketText(String str) {
        System.out.printf("RESPONSE: %n%s%n", str);
        disconnect();
    }
    
    @Override
    public void onWebSocketBinary(byte[] arg0, int arg1, int arg2) {
        System.out.println("onWebSocketBinary");
        disconnect();
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        System.out.printf("onWebSocketError: %s%n", cause);
        disconnect();
    }

}