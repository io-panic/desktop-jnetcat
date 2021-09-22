@ECHO OFF
SETLOCAL ENABLEDELAYEDEXPANSION

SET CONFIGFILE="conf/AllOptionsInOne.json"
SET LOG4J2XML="conf/log4j2.xml"

SET JAR=jnetcat-1.0.0-SNAPSHOT.jar

SET LOCALDIR=%~dp0
SET JAVA_HOME=%LOCALDIR%jre
SET JAVA=%JAVA_HOME%\bin\java.exe

ECHO ==
ECHO == Using configuration file %CONFIGFILE%
ECHO ==

CD /D %~dp0

CALL %JAVA% -Dlog4j2.properties=%LOG4J2XML% -jar %JAR% -f %CONFIGFILE%
