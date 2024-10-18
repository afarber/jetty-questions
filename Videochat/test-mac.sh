#!/bin/sh

export TEST='1'

SCRIPT_DIR=`dirname $0`

export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8
export JETTY_HOME=$SCRIPT_DIR/../jetty-home-10.0.24
export JETTY_BASE=$SCRIPT_DIR/jetty.base

mvn -f $SCRIPT_DIR/my-servlet/pom.xml clean package && \
    java -jar "$JETTY_HOME/start.jar" jetty.home=$JETTY_HOME jetty.base="$SCRIPT_DIR/jetty.base"
