package my.servlet;

import java.net.InetSocketAddress;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyListener implements WebSocketListener {
    private static Logger LOG = LoggerFactory.getLogger(MyServlet.class);

    private final MyServlet mServlet;

    private Session mSession;
    private String mAddress;
    private int mUid;

    private void remove(boolean shouldClose) {
        if (mUid > 0 && mAddress != null) {
            Client.remove(mUid, mAddress, shouldClose);
        }

        mSession = null;
        mAddress = null;
        mUid = 0;
    }

    public MyListener(MyServlet servlet) {
        mServlet = servlet;
    }

    @Override
    public void onWebSocketConnect(Session session) {
        mSession = session;
        InetSocketAddress address = (InetSocketAddress) session.getRemoteAddress();
        // use getHostString(), because it does not perform DNS resolving
        mAddress = address.getHostString() + ":" + address.getPort();
        LOG.info("onWebSocketConnect: address={} session={}", mAddress, mSession);
    }

    // onWebSocketClose is ALWAYS called
    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        LOG.info("onWebSocketClose: mUid={} address={} statusCode={} reason={}", mUid, mAddress, statusCode, reason);
        // Jetty ALWAYS sends a CLOSE frame, there is no need to send it explicitly
        remove(false);
    }

    // onWebSocketError might be called before or after onWebSocketClose
    @Override
    public void onWebSocketError(Throwable ex) {
        // don't print full call stack on disconnects, just print 1 line message instead
        LOG.warn("onWebSocketError: mUid={} mAddress={} ex={}", mUid, mAddress, (ex == null ? "" : ex.getMessage()));
    }

    @Override
    public void onWebSocketBinary(byte[] arg0, int arg1, int arg2) {
        LOG.warn("onWebSocketBinary");
        remove(true);
    }

    @Override
    public void onWebSocketText(String str) {
        LOG.info("onWebSocketText: str={}", str);

        // there was some valid action performed by a human,
        // so do not close connection to that browser tab
        Client.updateStamp(mUid, mAddress);
        // iterate through all clients and remove -
        // 1) clients with closed connection
        // 2) clients not doing human-like actions since 5 min
        // (an abandoned browser tab, which just keeps connection open)
        Client.disconnectStale();
    }
}
