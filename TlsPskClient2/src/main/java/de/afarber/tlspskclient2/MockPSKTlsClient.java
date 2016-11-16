/*
 * The file has been copied from https://github.com/bcgit/bc-java
 *
 * Copyright (c) 2000-2016 The Legion of the Bouncy Castle Inc. (http://www.bouncycastle.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
 * and associated documentation files (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package de.afarber.tlspskclient2;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;

import org.bouncycastle.crypto.tls.AlertDescription;
import org.bouncycastle.crypto.tls.AlertLevel;
import org.bouncycastle.crypto.tls.BasicTlsPSKIdentity;
import org.bouncycastle.crypto.tls.CipherSuite;
import org.bouncycastle.crypto.tls.PSKTlsClient;
import org.bouncycastle.crypto.tls.ProtocolVersion;
import org.bouncycastle.crypto.tls.TlsExtensionsUtils;
import org.bouncycastle.crypto.tls.TlsPSKIdentity;
import org.bouncycastle.crypto.tls.TlsSession;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

class MockPSKTlsClient
    extends PSKTlsClient
{
    TlsSession session;

    MockPSKTlsClient(TlsSession session)
    {
        this(session, new BasicTlsPSKIdentity("client", new byte[16]));
    }

    MockPSKTlsClient(TlsSession session, TlsPSKIdentity pskIdentity)
    {
        super(pskIdentity);
        this.session = session;
    }

    @Override
    public TlsSession getSessionToResume()
    {
        return this.session;
    }

    @Override
    public void notifyAlertRaised(short alertLevel, short alertDescription, String message, Throwable cause)
    {
        PrintStream out = (alertLevel == AlertLevel.fatal) ? System.err : System.out;
        out.println("TLS-PSK client raised alert: " + AlertLevel.getText(alertLevel) + ", "
            + AlertDescription.getText(alertDescription));
        if (message != null)
        {
            out.println("> " + message);
        }
        if (cause != null)
        {
            cause.printStackTrace(out);
        }
    }

    @Override
    public void notifyAlertReceived(short alertLevel, short alertDescription)
    {
        PrintStream out = (alertLevel == AlertLevel.fatal) ? System.err : System.out;
        out.println("TLS-PSK client received alert: " + AlertLevel.getText(alertLevel) + ", "
            + AlertDescription.getText(alertDescription));
    }

    @Override
    public void notifyHandshakeComplete() throws IOException
    {
        super.notifyHandshakeComplete();

        TlsSession newSession = context.getResumableSession();
        if (newSession != null)
        {
            byte[] newSessionID = newSession.getSessionID();
            String hex = Hex.toHexString(newSessionID);

            if (this.session != null && Arrays.areEqual(this.session.getSessionID(), newSessionID))
            {
                System.out.println("Resumed session: " + hex);
            }
            else
            {
                System.out.println("Established session: " + hex);
            }

            this.session = newSession;
        }
    }

    @Override
    public int[] getCipherSuites()
    {
        return new int[] { 
            CipherSuite.TLS_PSK_WITH_AES_256_CBC_SHA, // for tests with openssl tool
            CipherSuite.TLS_PSK_WITH_AES_128_CBC_SHA256,
            CipherSuite.TLS_PSK_WITH_NULL_SHA256
        };
    }

    @Override
    public ProtocolVersion getMinimumVersion()
    {
        return ProtocolVersion.TLSv12;
    }

    @Override
    public Hashtable getClientExtensions() throws IOException
    {
        Hashtable clientExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(super.getClientExtensions());
        TlsExtensionsUtils.addEncryptThenMACExtension(clientExtensions);
        return clientExtensions;
    }

    @Override
    public void notifyServerVersion(ProtocolVersion serverVersion) throws IOException
    {
        super.notifyServerVersion(serverVersion);
        System.out.println("TLS-PSK client negotiated " + serverVersion);
    }
}
