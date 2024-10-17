#!/bin/sh

# Run once on Mac/Linux (without proxy-protocol,gzip):
# java -jar jetty-home-10.0.24/start.jar jetty.home=jetty-home-10.0.24 jetty.base=jetty.base --add-module=http,webapp,deploy,resources,websocket

SCRIPT_DIR=`dirname $0`

export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8
export JETTY_HOME=$SCRIPT_DIR/jetty-home-10.0.24
export JETTY_BASE=$SCRIPT_DIR/jetty.base

mvn -f $SCRIPT_DIR/my-servlet/pom.xml clean package && \
    java -jar "$JETTY_HOME/start.jar" jetty.home=$JETTY_HOME jetty.base="$SCRIPT_DIR/jetty.base"
