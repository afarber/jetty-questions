#!/bin/sh

export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8
REPO=/Users/user1/.m2/repository
VERSION=9.3.9.v20160517

CPATHS=/Users/user1/jetty-newbie/WssClient/target/classes 
CPATHS=$CPATHS:$REPO/org/eclipse/jetty/jetty-io/$VERSION/jetty-io-$VERSION.jar 
CPATHS=$CPATHS:$REPO/org/eclipse/jetty/jetty-util/$VERSION/jetty-util-$VERSION.jar 
CPATHS=$CPATHS:$REPO/org/eclipse/jetty/websocket/websocket-api/$VERSION/websocket-api-$VERSION.jar
CPATHS=$CPATHS:$REPO/org/eclipse/jetty/websocket/websocket-client/$VERSION/websocket-client-$VERSION.jar 
CPATHS=$CPATHS:$REPO/org/eclipse/jetty/websocket/websocket-common/$VERSION/websocket-common-$VERSION.jar 

/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/bin/java \
        -classpath $CPATHS de.afarber.wssclient.Main "$@"
