// This project uses several source code files (c) Bouncy Castle http://www.bouncycastle.org/java.html

package de.afarber.tlspskserver2;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.eclipse.jetty.server.Server;

/*
    # java -jar target\TlsPskServer2-1.0-SNAPSHOT.jar

    # (while true; do echo blah; sleep 5; done) | openssl s_client \
            -connect 127.0.0.1:23232 \
            -psk 1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A \
            -cipher PSK-AES256-CBC-SHA \
            -debug -state -msg -ign_eof -tls1_2
*/

public class Main implements Runnable {
    private static final int TLS_PSK_PORT = 23232;
    private static final int JETTY_PORT   = 32323;

    public static void main(String[] args) throws IOException {
        ServerSocket server  = new ServerSocket(TLS_PSK_PORT);
        ExecutorService pool = Executors.newFixedThreadPool(10);

        new Thread(new Main()).start();
        
        while (true) {
            Socket socket = server.accept();
            pool.execute(new MyRunnable(socket));
        }
    }
    
    @Override
    public void run() {
        InetSocketAddress localhost = new InetSocketAddress("127.0.0.1", JETTY_PORT);
        Server server = new Server(localhost);
        server.setHandler(new MyHandler());
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            System.err.print(e);
        }
    }
}

