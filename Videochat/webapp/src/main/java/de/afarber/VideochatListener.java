package de.afarber;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.eclipse.jetty.util.ajax.JSON;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

public class VideochatListener implements WebSocketListener, Common {

    private static Map<Integer, Set<Session>> sRooms = new HashMap<>();
    private Session mSession;
    private int myRoom = 0;

    private void disconnect() {
        if (mSession != null && 
                mSession.isOpen()) {
            mSession.close();
        }

        mSession = null;
    }

    @Override
    public void onWebSocketConnect(Session session) {
        LOG.info("onWebSocketConnect: session={}", session);
        mSession = session;
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        LOG.info("onWebSocketClose: statusCode={} reason={}", statusCode, reason);
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
        LOG.info("onWebSocketText: str={}", str);

        try {
            Map req = (Map<String, Object>) JSON.parse(str);
            String type = String.valueOf(req.get("type"));
            
            if (type == null || type.length() < 2) {
                throw new Exception("Invalid type = " + type);
            }

            switch(type) {
                case "GETROOM":
                    myRoom = generateRoomNumber();
                    Set <Session> set1 = new HashSet<>();
                    set1.add(mSession);
                    sRooms.put(myRoom, set1);
                    LOG.info("Created a new room={}", myRoom);
                    mSession.getRemote().sendStringByFuture("{\"type\":\"GETROOM\",\"value\":" + myRoom + "}");
                    break;
                case "ENTERROOM":
                    myRoom = ((Long) req.get("value")).intValue();
                    LOG.info("Joined the room={}", myRoom);
                    Set <Session> set2 = sRooms.get(myRoom);
                    set2.add(mSession);
                    sRooms.put(myRoom, set2);
                    break;
                default:
                    sendToAll(str);
                    break;                
            }
        } catch (Exception ex) {
            LOG.warn(ex);
            disconnect();
        }
    }
    
    private void sendToAll(String str) {
        for (Session session : sRooms.get(myRoom)) {
            if (session != mSession) {
                session.getRemote().sendStringByFuture(str);
            }
        }
    }
        
    private int generateRoomNumber() {
        return new Random().nextInt();
    }

}
