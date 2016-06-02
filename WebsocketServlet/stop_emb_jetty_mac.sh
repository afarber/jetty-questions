#!/bin/sh

ps -ef | awk '/[W]ebsocketServlet/ { print $2 }' | xargs kill

