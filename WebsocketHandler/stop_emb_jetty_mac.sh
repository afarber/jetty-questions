#!/bin/sh

ps -ef | awk '/[W]ebsocketHandler/ { print $2 }' | xargs kill

