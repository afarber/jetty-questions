#!/bin/sh

REPO=/Users/afarber/.m2/repository
VERSION=9.3.9.v20160517

CPATHS=(
        /Users/afarber/src/jetty-newbie/WebsocketServlet/target/classes 
        $REPO/javax/servlet/javax.servlet-api/3.1.0/javax.servlet-api-3.1.0.jar 
        $REPO/org/eclipse/jetty/jetty-http/$VERSION/jetty-http-$VERSION.jar 
        $REPO/org/eclipse/jetty/jetty-io/$VERSION/jetty-io-$VERSION.jar 
        $REPO/org/eclipse/jetty/jetty-security/$VERSION/jetty-security-$VERSION.jar 
        $REPO/org/eclipse/jetty/jetty-server/$VERSION/jetty-server-$VERSION.jar 
        $REPO/org/eclipse/jetty/jetty-servlet/$VERSION/jetty-servlet-$VERSION.jar 
        $REPO/org/eclipse/jetty/jetty-util/$VERSION/jetty-util-$VERSION.jar 
        $REPO/org/eclipse/jetty/websocket/websocket-api/$VERSION/websocket-api-$VERSION.jar
        $REPO/org/eclipse/jetty/websocket/websocket-client/$VERSION/websocket-client-$VERSION.jar 
        $REPO/org/eclipse/jetty/websocket/websocket-common/$VERSION/websocket-common-$VERSION.jar 
        $REPO/org/eclipse/jetty/websocket/websocket-server/$VERSION/websocket-server-$VERSION.jar 
        $REPO/org/eclipse/jetty/websocket/websocket-servlet/$VERSION/websocket-servlet-$VERSION.jar 
) 

/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/bin/java \
        -classpath "$(IFS=:; echo "${CPATHS[*]}")" de.afarber.MyServlet
