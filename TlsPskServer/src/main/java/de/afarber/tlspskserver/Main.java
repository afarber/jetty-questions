// This project uses several source code files (c) Bouncy Castle http://www.bouncycastle.org/java.html

package de.afarber.tlspskserver;

/*
    # openssl s_server \
            -psk 1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A \
            -psk_hint Client_identity\
            -cipher PSK-AES256-CBC-SHA \
            -debug -state -nocert -accept 12345 -tls1 -www
*/

public class Main {

    public static void main(String[] args) throws Exception {
        TlsPSKProtocolTest test = new TlsPSKProtocolTest();
        test.testClientServer();
    }
}

