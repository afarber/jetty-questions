# DO NOT EDIT THIS FILE - See: https://jetty.org/docs/

[description]
Creates the PID file for the Jetty process

[tags]
start

[before]
server
threadpool
jvm

[xml]
etc/jetty-pid.xml

[ini-template]
## PID file path
# jetty.pid=${jetty.base}/jetty.pid