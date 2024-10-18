# DO NOT EDIT THIS FILE - See: https://jetty.org/docs/

[description]
Base configuration for the jetty logging mechanism.
Provides a ${jetty.base}/resources/jetty-logging.properties.

[tags]
logging

[depends]
logging/slf4j
resources

[provides]
logging|default

[files]
basehome:modules/logging/jetty

[lib]
lib/logging/jetty-slf4j-impl-${jetty.version}.jar

[ini]
jetty.webapp.addHiddenClasses+=,org.eclipse.jetty.logging.
jetty.webapp.addHiddenClasses+=,${jetty.home.uri}/lib/logging/
