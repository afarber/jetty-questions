#!/bin/sh

REPO=/Users/afarber/.m2/repository

CPATHS=(
        /Users/afarber/src/jetty-newbie/EmbWebsocketListener/target/classes 
        $REPO/javax/servlet/javax.servlet-api/3.1.0/javax.servlet-api-3.1.0.jar 
        $REPO/org/eclipse/jetty/jetty-http/9.3.9.v20160517/jetty-http-9.3.9.v20160517.jar 
        $REPO/org/eclipse/jetty/jetty-io/9.3.9.v20160517/jetty-io-9.3.9.v20160517.jar 
        $REPO/org/eclipse/jetty/jetty-security/9.3.9.v20160517/jetty-security-9.3.9.v20160517.jar 
        $REPO/org/eclipse/jetty/jetty-server/9.3.9.v20160517/jetty-server-9.3.9.v20160517.jar 
        $REPO/org/eclipse/jetty/jetty-servlet/9.3.9.v20160517/jetty-servlet-9.3.9.v20160517.jar 
        $REPO/org/eclipse/jetty/jetty-util/9.3.9.v20160517/jetty-util-9.3.9.v20160517.jar 
        $REPO/org/eclipse/jetty/websocket/websocket-api/9.3.9.v20160517/websocket-api-9.3.9.v20160517.jar
        $REPO/org/eclipse/jetty/websocket/websocket-client/9.3.9.v20160517/websocket-client-9.3.9.v20160517.jar 
        $REPO/org/eclipse/jetty/websocket/websocket-common/9.3.9.v20160517/websocket-common-9.3.9.v20160517.jar 
        $REPO/org/eclipse/jetty/websocket/websocket-server/9.3.9.v20160517/websocket-server-9.3.9.v20160517.jar 
        $REPO/org/eclipse/jetty/websocket/websocket-servlet/9.3.9.v20160517/websocket-servlet-9.3.9.v20160517.jar 
) 

/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/bin/java \
        -classpath "$(IFS=:; echo "${CPATHS[*]}")" org.eclipse.jetty.demo.listener.Main

