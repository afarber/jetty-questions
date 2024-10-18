CHCP 65001

@REM Run once on Windows (without proxy-protocol,gzip):
@REM java -jar jetty-home-12.0.14\start.jar jetty.home=..\jetty-home-12.0.14 jetty.base=jetty.base --add-module=http,webapp,deploy,resources,websocket

SET JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8
SET JETTY_HOME=%~dp0\..\jetty-home-12.0.14
SET JETTY_BASE=%~dp0\jetty.base

@REM Maven 3.9.5 does not work correctly on Windows,
@REM so comment it out for now and build with VS Code
@REM CALL mvn -f %~dp0\pom.xml clean package 

CALL java -jar %JETTY_HOME%\start.jar jetty.home=%JETTY_HOME% jetty.base=%JETTY_BASE%
