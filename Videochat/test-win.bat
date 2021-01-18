@CHCP 65001

@SET JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8
@SET JETTY_HOME=%~dp0\jetty-distribution-9.4.35.v20201120
@SET JETTY_BASE=%~dp0\jetty.base

@SET TEST=1

REM mvn -f %~dp0webapp\pom.xml clean package
java -jar %JETTY_HOME%\start.jar jetty.home=%JETTY_HOME% jetty.base=%JETTY_BASE%


