package de.afarber.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.WebSocketListener;

public class MyListener implements WebSocketListener {
    private static final Logger LOG = Log.getLogger(MyListener.class);
    private final CountDownLatch mCloseLatch;
    private Session mSession;

    public MyListener() {
        mCloseLatch = new CountDownLatch(1);
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return mCloseLatch.await(duration, unit);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        LOG.info("onWebSocketClose: {} {}", statusCode, reason);
        mSession = null;
        mCloseLatch.countDown(); // trigger latch
    }

    @Override
    public void onWebSocketConnect(Session session) {
        LOG.info("onWebSocketConnect: {}", session);
        mSession = session;
        try {
            Future<Void> fut;
            fut = session.getRemote().sendStringByFuture("Hello");
            fut.get(2, TimeUnit.SECONDS); // wait for send to complete.

            fut = session.getRemote().sendStringByFuture("Thanks for the conversation.");
            fut.get(2, TimeUnit.SECONDS); // wait for send to complete.

            session.close(StatusCode.NORMAL, "I'm done");
        } catch (InterruptedException | ExecutionException | TimeoutException t) {
            t.printStackTrace();
        }
    }

    @Override
    public void onWebSocketText(String str) {
        LOG.info("onWebSocketText: {}", str);
    }
    
    @Override
    public void onWebSocketBinary(byte[] arg0, int arg1, int arg2) {
        LOG.info("onWebSocketBinary: {}", arg0);
        mSession.close();
        mSession = null;
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        LOG.info("onWebSocketError: {}", cause);
        mSession.close();
        mSession = null;
    }

}