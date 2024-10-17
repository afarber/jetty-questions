package my.servlet;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Client {
    private static Logger LOG = LoggerFactory.getLogger(MyServlet.class);

    // epoch timestamp - when were the old connections cleaned last time
    private static int lastCleanup;
    // multi value map with keys: numeric user id, values: remote address -> Client
    // object
    private static final Map<Integer, Map<String, Client>> CLIENTS = new ConcurrentHashMap<>();

    public static void add(int uid, String address, Session session) {
        Client client = new Client(uid, address, session);
        // if there are no entries for the uid, create a new map, in a thread-safe way
        CLIENTS.computeIfAbsent(uid, (x -> new ConcurrentHashMap<>())).put(client.address, client);
    }

    public static List<Session> getOpenSessions(int uid) {
        Map<String, Client> map = CLIENTS.get(uid);
        return map == null ?
        // the map can be null if another thread has removed the uid from CLIENTS
                Collections.<Session>emptyList()
                : map
                        .values()
                        .stream()
                        .filter(x -> x.session.isOpen())
                        .map(x -> x.session)
                        .collect(Collectors.toList());
    }

    public static void remove(int uid, String address, boolean shouldClose) {
        Map<String, Client> map = CLIENTS.get(uid);
        if (map == null) {
            // the map can be null if another thread has removed the uid from CLIENTS
            return;
        }

        Client client = map.remove(address);
        LOG.info("removing uid={} address={} shouldClose={} client={}", uid, address, shouldClose, client);
        // if there are no entries for the uid, delete the empty map
        if (map.isEmpty()) {
            CLIENTS.remove(uid);
        }

        if (shouldClose && client != null && client.session.isOpen()) {
            client.session.close();
            client.session.disconnect();
        }
    }

    public static void updateStamp(int uid, String address) {
        CLIENTS.get(uid).get(address).human = (int) (System.currentTimeMillis() / 1000);
    }

    public static void disconnectStale() {
        int now = (int) (System.currentTimeMillis() / 1000);
        if (now - lastCleanup < 300) {
            return;
        }

        Iterator<Map.Entry<Integer, Map<String, Client>>> it1 = CLIENTS.entrySet().iterator();
        while (it1.hasNext()) {
            // get the map: remote address -> Client object
            Map<String, Client> map = it1.next().getValue();
            Iterator<Map.Entry<String, Client>> it2 = map.entrySet().iterator();
            while (it2.hasNext()) {
                Client client = it2.next().getValue();
                if (!client.session.isOpen()) {
                    LOG.info("disconnectStale session not open, removing client={}", client);
                    it2.remove();
                } else if (now - client.human > 30) {
                    LOG.info("disconnectStale player afk, removing client={}", client);
                    client.session.close();
                    client.session.disconnect();
                    it2.remove();
                }
            }

            if (map.isEmpty()) {
                it1.remove();
            }
        }

        lastCleanup = now;
    }

    // used by unit tests and when servlet is destroyed
    public static void clear() {
        Iterator<Map.Entry<Integer, Map<String, Client>>> it = CLIENTS.entrySet().iterator();
        while (it.hasNext()) {
            // get the map: remote address -> Client object
            Map<String, Client> map = it.next().getValue();
            map.clear();
            it.remove();
        }
    }

    // 1st key in the CLIENTS map: numeric user id
    private final int uid;
    // 2nd key in the CLIENTS map: remote IP address ":" port (identifies the
    // browser tab)
    private final String address;
    // the WebSocket session
    private final Session session;
    // timestamp in seconds, when the last action by the human player was received
    private int human;

    public Client(int uid, String address, Session session) {
        this.uid = uid;
        this.address = address;
        this.session = session;
        this.human = (int) (System.currentTimeMillis() / 1000);

        if (uid <= 0 ||
                address == null ||
                address.length() < "1.1.1.1:1".length() ||
                session == null ||
                !session.isOpen()) {
            throw new IllegalArgumentException(toString());
        }
    }

    @Override
    public String toString() {
        return Client.class.getSimpleName() +
                ", uid: " + uid +
                ", address: " + address +
                ", session: " + session +
                ", human: " + human;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Client &&
                uid == ((Client) other).uid &&
                address.equals(((Client) other).address);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + this.uid;
        hash = 19 * hash + Objects.hashCode(this.address);
        return hash;
    }
}
