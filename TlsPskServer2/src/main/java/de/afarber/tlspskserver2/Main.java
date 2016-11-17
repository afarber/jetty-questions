// This project uses several source code files (c) Bouncy Castle http://www.bouncycastle.org/java.html

package de.afarber.tlspskserver2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import org.bouncycastle.crypto.tls.TlsServerProtocol;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.Streams;

/*
    # java -jar target\TlsPskServer2-1.0-SNAPSHOT.jar

    # echo blah | openssl s_client \
            -connect 127.0.0.1:12345 \
            -psk 1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A \
            -cipher PSK-AES256-CBC-SHA \
            -debug -state -msg -ign_eof -tls1_2
*/

public class Main {

    public static void main(String[] args) throws IOException {
        SecureRandom random       = new SecureRandom();
        ServerSocket serverSocket = new ServerSocket(12345);
        Socket socket             = serverSocket.accept();
        BufferedInputStream bis   = new BufferedInputStream(socket.getInputStream());
        BufferedOutputStream bos  = new BufferedOutputStream(socket.getOutputStream());
        TlsServerProtocol proto   = new TlsServerProtocol(bis, bos, random);
        MockPSKTlsServer server   = new MockPSKTlsServer();
        
        proto.accept(server);
        pipeAll(proto.getInputStream(), proto.getOutputStream());
        //pipeAll(proto.getInputStream(), System.out);
        proto.close();
        
        /*
        ServerThread serverThread = new ServerThread(proto);
        serverThread.start();
        serverThread.join();
        */
    }
    
    public static void pipeAll(InputStream inStr, OutputStream outStr)
        throws IOException
    {
        byte[] bs = new byte[4096];
        int numRead;
        while ((numRead = inStr.read(bs, 0, bs.length)) >= 0)
        {
            System.out.println("XXX " + Hex.toHexString(bs, 0, numRead));
            System.out.println("XXX " + new String(bs, 0, numRead));
            outStr.write(bs, 0, numRead);
        }
    }

    static class ServerThread
        extends Thread
    {
        private final TlsServerProtocol proto;

        ServerThread(TlsServerProtocol proto)
        {
            this.proto = proto;
        }

        @Override
        public void run()
        {
            try
            {
                MockPSKTlsServer server = new MockPSKTlsServer();
                proto.accept(server);
                Streams.pipeAll(proto.getInputStream(), proto.getOutputStream());
                proto.close();
            }
            catch (IOException e)
            {
                System.err.println(e);
            }
        }
    }
}

