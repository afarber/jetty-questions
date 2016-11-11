@echo off

chcp 65001

set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8
set JETTY_DEBUG=-Dorg.eclipse.jetty.LEVEL=DEBUG -Djavax.net.debug=ssl
set JETTY_DEBUG=
set REPO="C:\Users\U299FAV\.m2\repository"
set VERSION=9.3.10.v20160621

set CPATHS=C:\Users\U299FAV\jetty-newbie\WssClient\target\classes
set CPATHS=%CPATHS%;%REPO%\org\eclipse\jetty\jetty-io\%VERSION%\jetty-io-%VERSION%.jar
set CPATHS=%CPATHS%;%REPO%\org\eclipse\jetty\jetty-util\%VERSION%\jetty-util-%VERSION%.jar
set CPATHS=%CPATHS%;%REPO%\org\eclipse\jetty\websocket\websocket-api\%VERSION%\websocket-api-%VERSION%.jar
set CPATHS=%CPATHS%;%REPO%\org\eclipse\jetty\websocket\websocket-client\%VERSION%\websocket-client-%VERSION%.jar
set CPATHS=%CPATHS%;%REPO%\org\eclipse\jetty\websocket\websocket-common\%VERSION%\websocket-common-%VERSION%.jar

"C:\Program Files\Java\jdk1.8.0_66\bin\java.exe" %JETTY_DEBUG% -cp %CPATHS% de.afarber.wssclient.Main %*

