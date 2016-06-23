#!/bin/sh

REPO=/Users/afarber/.m2/repository
VERSION=9.3.10.v20160621

CPATHS=/Users/afarber/jetty-newbie/WssEmbedded/target/classes 
CPATHS=$CPATHS:$REPO/javax/servlet/javax.servlet-api/3.1.0/javax.servlet-api-3.1.0.jar 
CPATHS=$CPATHS:$REPO/org/eclipse/jetty/jetty-http/$VERSION/jetty-http-$VERSION.jar 
CPATHS=$CPATHS:$REPO/org/eclipse/jetty/jetty-io/$VERSION/jetty-io-$VERSION.jar 
CPATHS=$CPATHS:$REPO/org/eclipse/jetty/jetty-security/$VERSION/jetty-security-$VERSION.jar 
CPATHS=$CPATHS:$REPO/org/eclipse/jetty/jetty-server/$VERSION/jetty-server-$VERSION.jar 
CPATHS=$CPATHS:$REPO/org/eclipse/jetty/jetty-servlet/$VERSION/jetty-servlet-$VERSION.jar 
CPATHS=$CPATHS:$REPO/org/eclipse/jetty/jetty-util/$VERSION/jetty-util-$VERSION.jar 
CPATHS=$CPATHS:$REPO/org/eclipse/jetty/jetty-util-ajax/$VERSION/jetty-util-ajax-$VERSION.jar 
CPATHS=$CPATHS:$REPO/org/eclipse/jetty/websocket/websocket-api/$VERSION/websocket-api-$VERSION.jar
CPATHS=$CPATHS:$REPO/org/eclipse/jetty/websocket/websocket-client/$VERSION/websocket-client-$VERSION.jar 
CPATHS=$CPATHS:$REPO/org/eclipse/jetty/websocket/websocket-common/$VERSION/websocket-common-$VERSION.jar 
CPATHS=$CPATHS:$REPO/org/eclipse/jetty/websocket/websocket-server/$VERSION/websocket-server-$VERSION.jar 
CPATHS=$CPATHS:$REPO/org/eclipse/jetty/websocket/websocket-servlet/$VERSION/websocket-servlet-$VERSION.jar 

/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/bin/java \
       -Djavax.net.debug=ssl -classpath $CPATHS de.afarber.wssembedded.MyHandler
