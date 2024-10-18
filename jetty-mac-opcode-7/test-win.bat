CHCP 65001

@REM Run once on Windows (without proxy-protocol,gzip):
@REM java -jar jetty-home-10.0.24\start.jar jetty.home=..\jetty-home-10.0.24 jetty.base=jetty.base --add-module=http,webapp,deploy,resources,websocket

SET JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8
SET JETTY_HOME=%~dp0\..\jetty-home-10.0.24
SET JETTY_BASE=%~dp0\jetty.base

@REM Maven 3.9.5 does not work correctly on Windows,
@REM so comment it out for now and build with VS Code
@REM CALL mvn -f %~dp0my-servlet\pom.xml clean package 

CALL java -jar %JETTY_HOME%\start.jar jetty.home=%JETTY_HOME% jetty.base=%JETTY_BASE%
