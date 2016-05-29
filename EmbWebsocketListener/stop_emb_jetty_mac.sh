#!/bin/sh

ps -ef | awk '/[E]mbWebsocketListener/ { print $2 }' | xargs kill
