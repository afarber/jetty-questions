package de.afarber;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

public class MyListener implements WebSocketListener {
    private static final String DATABASE_URL = "jdbc:postgresql://127.0.0.1/";
    private static final int UNEXPECTED_CONDITION = 1011;
    private static final int UNSUPPORTED_DATA = 1003;
    private static final Map<String, Session> CLIENTS = new ConcurrentHashMap<>();
    private static final Logger LOG = Log.getLogger(MyListener.class);
    
    private String mName;
    private Session mSession;
    private Connection mDatabase;

    /*
     * Create the file src/main/resources/database.properties containing:
     * database: test
     * user: test
     * password: test
    */

    private void connectToDatabase() throws IOException, SQLException {
        Properties props = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (
            InputStream is = loader.getResourceAsStream("database.properties");
        ) {
            props.load(is);
            mDatabase = DriverManager.getConnection(DATABASE_URL, props);
        }
    }

    private void disconnectFromDatabase() throws SQLException {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    private void disconnectWebsocket() {
        CLIENTS.remove(mName);
        messageAll(mName + " has disconnected from " + getClass().getName());

        mName = null;
        mSession = null;
        
        try {
            disconnectFromDatabase();
        } catch (SQLException ignore) {
        }
    }
    
    @Override
    public void onWebSocketConnect(Session session) {
        LOG.info("onWebSocketConnect: {}", session);

        try {
            connectToDatabase();
        } catch (IOException | SQLException ex) {
            LOG.warn(ex);
            mSession.close(UNSUPPORTED_DATA, null);
            return;
        }

        mName = "Client-" + ThreadLocalRandom.current().nextInt(1, 1000 + 1);
        mSession = session;
        CLIENTS.put(mName, mSession);
        messageAll(mName + " has connected to " + getClass().getName());
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        LOG.info("onWebSocketClose: {} - {}", statusCode, reason);
        disconnectWebsocket();
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        LOG.warn("onWebSocketError", cause);
        disconnectWebsocket();
    }

    @Override
    public void onWebSocketText(String str) {
        LOG.info("onWebSocketText: {}", str);

        if (mSession != null && mDatabase != null) {
            messageAll(mName + ": " + str);
        }
    }

    @Override
    public void onWebSocketBinary(byte[] arg0, int arg1, int arg2) {
        mSession.close(UNSUPPORTED_DATA, null);
    }
    
    private void messageAll(String str) {
        for (Session s: CLIENTS.values()) {
            s.getRemote().sendStringByFuture(str);
        }
    }
}
