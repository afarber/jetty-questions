//
//  ========================================================================
//  Copyright (c) 1995-2016 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package de.afarber;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

/**
 * Example EchoSocket using Listener.
 */
public class MyListener implements WebSocketListener {
    private Session mSession;

    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len) {
        /* only interested in text messages */
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        mSession = null;
    }

    @Override
    public void onWebSocketConnect(Session session) {
        System.out.println("onWebSocketConnect: " + session);
        mSession = session;
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        cause.printStackTrace(System.err);
    }

    @Override
    public void onWebSocketText(String message) {
        System.out.println("onWebSocketText: " + message);
        if (mSession != null && mSession.isOpen()) {
            System.out.printf("Echoing back message [%s]%n",message);
            // echo the message back
            mSession.getRemote().sendString(message,null);
        }
    }
}
