#!/bin/sh

ps -ef | awk '/[E]mbeddedWebsocket/ { print $2 }' | xargs kill
