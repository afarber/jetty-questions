# DO NOT EDIT THIS FILE - See: https://jetty.org/docs/

[description]
Adds Jetty EE10 utility servlets and filters available to a webapp.
Puts org.eclipse.jetty.ee10.servlets on the server classpath
(CrossOriginFilter, DosFilter, MultiPartFilter, QoSFilter, etc.)
for use by all web applications.

[environment]
ee10

[depend]
ee10-servlet

[lib]
lib/jetty-ee10-servlets-${jetty.version}.jar

