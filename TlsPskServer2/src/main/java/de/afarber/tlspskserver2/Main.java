// This project uses several source code files (c) Bouncy Castle http://www.bouncycastle.org/java.html

package de.afarber.tlspskserver2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
    # java -jar target\TlsPskServer2-1.0-SNAPSHOT.jar

    # (while true; do echo blah; sleep 5; done) | openssl s_client \
            -connect 127.0.0.1:12345 \
            -psk 1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A \
            -cipher PSK-AES256-CBC-SHA \
            -debug -state -msg -ign_eof -tls1_2
*/

public class Main {

    public static void main(String[] args) throws IOException {
        ServerSocket server  = new ServerSocket(12345);
        ExecutorService pool = Executors.newFixedThreadPool(10);

        while (true) {
            Socket socket = server.accept();
            pool.execute(new MyRunnable(socket));
        }
    }
}

