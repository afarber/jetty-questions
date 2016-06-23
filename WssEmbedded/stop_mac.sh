#!/bin/sh

ps -ef | awk '/[W]ssEmbedded/ { print $2 }' | xargs kill

