#!/bin/sh

SCRIPT_DIR=`dirname $0`

export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8
export JETTY_HOME=$SCRIPT_DIR/jetty-distribution-9.4.35.v20201120
export JETTY_BASE=$SCRIPT_DIR/jetty.base

export TEST='1'

mvn -f $SCRIPT_DIR/webapp/pom.xml clean package
java -jar $SCRIPT_DIR/$JETTY_HOME/start.jar jetty.home=$JETTY_HOME jetty.base=$SCRIPT_DIR/jetty.base

