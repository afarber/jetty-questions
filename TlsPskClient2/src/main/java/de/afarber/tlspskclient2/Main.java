// This project uses several source code files (c) Bouncy Castle http://www.bouncycastle.org/java.html

package de.afarber.tlspskclient2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.SecureRandom;
import org.bouncycastle.crypto.tls.BasicTlsPSKIdentity;
import org.bouncycastle.crypto.tls.TlsClientProtocol;
import org.bouncycastle.crypto.tls.TlsPSKIdentity;
import org.bouncycastle.util.encoders.Hex;

/*
    # openssl s_server \
            -psk 1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A \
            -psk_hint Client_identity\
            -cipher PSK-AES256-CBC-SHA \
            -debug -state -msg -nocert -accept 23232 -tls1_2 -www

    # java -jar target\TlsPskClient2-1.0-SNAPSHOT.jar
*/

public class Main {
    private static final int TLS_PSK_PORT    = 23232;
    private static final String TLS_PSK_HINT = "Client_identity";
    private static final String TLS_PSK_KEY  = "1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A";

    public static void main(String[] args) throws IOException {
        SecureRandom random      = new SecureRandom();
        TlsPSKIdentity identity  = new BasicTlsPSKIdentity(TLS_PSK_HINT, Hex.decode(TLS_PSK_KEY));
        Socket socket            = new Socket(InetAddress.getLocalHost(), TLS_PSK_PORT);
        BufferedInputStream bis  = new BufferedInputStream(socket.getInputStream());
        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
        TlsClientProtocol proto  = new TlsClientProtocol(bis, bos, random);
        MockPSKTlsClient client  = new MockPSKTlsClient(null, identity);
        proto.connect(client);

        OutputStream clearOs = proto.getOutputStream();
        InputStream clearIs = proto.getInputStream();
        clearOs.write("GET / HTTP/1.0\r\n\r\n".getBytes("UTF-8"));
        pipeAll(clearIs, System.out);
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
}

