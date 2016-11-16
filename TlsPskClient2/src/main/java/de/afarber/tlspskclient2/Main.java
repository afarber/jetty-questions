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
import java.security.Security;
import org.bouncycastle.crypto.tls.BasicTlsPSKIdentity;
import org.bouncycastle.crypto.tls.TlsClientProtocol;
import org.bouncycastle.crypto.tls.TlsPSKIdentity;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.Streams;

/*
    # openssl s_server \
            -psk 1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A \
            -psk_hint Client_identity\
            -cipher PSK-AES256-CBC-SHA \
            -debug -state -nocert -accept 12345 -tls1_2 -www
*/

public class Main {

    public static void main(String[] args) throws IOException {
        SecureRandom random      = new SecureRandom();
        TlsPSKIdentity identity  = new BasicTlsPSKIdentity("Client_identity", Hex.decode("1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A"));
        Socket socket            = new Socket(InetAddress.getLocalHost(), 12345);
        BufferedInputStream bis  = new BufferedInputStream(socket.getInputStream());
        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
        TlsClientProtocol proto  = new TlsClientProtocol(bis, bos, random);
        MockPSKTlsClient client  = new MockPSKTlsClient(null, identity);
        proto.connect(client);

        OutputStream clearOs = proto.getOutputStream();
        InputStream clearIs = proto.getInputStream();
        clearOs.write("GET / HTTP/1.1\r\n\r\n".getBytes("UTF-8"));
        pipeAll(clearIs, System.out);
    }
    
    public static void pipeAll(InputStream inStr, OutputStream outStr)
        throws IOException
    {
        byte[] bs = new byte[4096];
        int numRead;
        while ((numRead = inStr.read(bs, 0, bs.length)) > 0)    // Why is EOFException is thrown?
        {
            outStr.write(bs, 0, numRead);
        }
    }
}

