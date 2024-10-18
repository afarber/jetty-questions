#!/bin/sh

# Run once on Mac/Linux (without proxy-protocol,gzip):
# java -jar jetty-home-12.0.14/start.jar jetty.home=..\jetty-home-12.0.14 jetty.base=jetty.base --add-module=http,webapp,deploy,resources,websocket

SCRIPT_DIR=`dirname $0`

export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8
export JETTY_HOME=$SCRIPT_DIR/../jetty-home-12.0.14
export JETTY_BASE=$SCRIPT_DIR/jetty.base

mvn -f $SCRIPT_DIR/pom.xml clean package && \
    java -jar "$JETTY_HOME/start.jar" jetty.home=$JETTY_HOME jetty.base="$SCRIPT_DIR/jetty.base"
