package de.afarber.tlspskclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Scanner;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.crypto.tls.AlertLevel;
import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.CipherSuite;
import org.bouncycastle.crypto.tls.ServerOnlyTlsAuthentication;
import org.bouncycastle.crypto.tls.TlsAuthentication;
import org.bouncycastle.crypto.tls.TlsClientProtocol;
import org.bouncycastle.crypto.tls.TlsPSKIdentity;
import org.bouncycastle.crypto.tls.PSKTlsClient;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

//import org.eclipse.jetty.util.ssl.SslContextFactory;

/*
    # openssl s_server \
            -psk 1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A \
            -psk_hint Client_identity\
            -cipher PSK-AES256-CBC-SHA \
            -debug -state -nocert -accept 12345 -tls1 -www
*/

public class Main {

    static String convertStreamToString(InputStream is) {
        Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    static class Z_PSKIdentity implements TlsPSKIdentity {

        void Z_PSKIdentity() {
        }

        @Override
        public void skipIdentityHint() {
            System.out.println("skipIdentityHint called\n");
        }

        @Override
        public void notifyIdentityHint(byte[] PSK_identity_hint) {
            System.out.println("notifyIdentityHint called\n");
        }

        @Override
        public byte[] getPSKIdentity() {
            return "Client_identity".getBytes();
        }

        @Override
        public byte[] getPSK() {
            return DatatypeConverter.parseHexBinary("1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A");
        }

    }

    public static void main(String[] args) throws Exception {

        Z_PSKIdentity pskIdentity = new Z_PSKIdentity();

        Security.addProvider(new BouncyCastleProvider());

        try (Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), 12345)) {
            
            SecureRandom secureRandom = new SecureRandom();
            
            TlsClientProtocol protocol = new TlsClientProtocol(socket.getInputStream(),
                    socket.getOutputStream(),
                    secureRandom);
            
            MyPSKTlsClient client = new MyPSKTlsClient(pskIdentity);
            protocol.connect(client);
            
            OutputStream output = protocol.getOutputStream();
            output.write("GET / HTTP/1.1\r\n\r\n".getBytes("UTF-8"));
            
            InputStream input = protocol.getInputStream();
            System.out.println(convertStreamToString(input));
            
            protocol.close();
        }
    }

    static class MyPSKTlsClient extends PSKTlsClient {

        public MyPSKTlsClient(TlsPSKIdentity id) {
            super(id);
        }

        public void notifyAlertRaised(short alertLevel, short alertDescription, String message, Exception cause) {
            PrintStream out = (alertLevel == AlertLevel.fatal) ? System.err : System.out;
            out.println("TLS client raised alert (AlertLevel." + alertLevel + ", AlertDescription." + alertDescription + ")");
            if (message != null) {
                out.println(message);
            }
            if (cause != null) {
                cause.printStackTrace(out);
            }
        }

        @Override
        public void notifyAlertReceived(short alertLevel, short alertDescription) {
            PrintStream out = (alertLevel == AlertLevel.fatal) ? System.err : System.out;
            out.println("TLS client received alert (AlertLevel." + alertLevel + ", AlertDescription."
                    + alertDescription + ")");
        }

        @Override
        public TlsAuthentication getAuthentication() throws IOException {
            
            return new ServerOnlyTlsAuthentication() {
                @Override
                public void notifyServerCertificate(Certificate serverCertificate)
                        throws IOException {
                    System.out.println("in getAuthentication");
                }
            };
        }
        
        @Override
        public int[] getCipherSuites()
        {
            return new int[] { 
                CipherSuite.TLS_PSK_WITH_AES_256_CBC_SHA,   // used for openssl test
                CipherSuite.TLS_PSK_WITH_NULL_SHA256,
                CipherSuite.TLS_PSK_WITH_AES_128_CBC_SHA256
            };
        }
    }
}

