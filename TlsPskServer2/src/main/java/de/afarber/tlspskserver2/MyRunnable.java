package de.afarber.tlspskserver2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.SecureRandom;
import org.bouncycastle.crypto.tls.TlsServerProtocol;
import org.bouncycastle.util.encoders.Hex;

public class MyRunnable implements Runnable {

    private final Socket mSocket;

    public MyRunnable(Socket socket) {
        mSocket = socket;
    }

    @Override
    public void run() {
        try {
            SecureRandom random       = new SecureRandom();
            BufferedInputStream bis   = new BufferedInputStream(mSocket.getInputStream());
            BufferedOutputStream bos  = new BufferedOutputStream(mSocket.getOutputStream());
            TlsServerProtocol proto   = new TlsServerProtocol(bis, bos, random);
            MockPSKTlsServer server   = new MockPSKTlsServer();
            proto.accept(server);
            pipeAll(proto.getInputStream(), proto.getOutputStream());
            //pipeAll(proto.getInputStream(), System.out);
            proto.close();
        } catch (IOException e) {
            System.err.print(e);
        }
    }
    
    private void pipeAll(InputStream inStr, OutputStream outStr)
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
