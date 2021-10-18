@ECHO OFF
SETLOCAL ENABLEDELAYEDEXPANSION

SET CONFIGFILE="conf/options.json"
SET LOG4J2XML="conf/log4j2.xml"

FOR /f %%i in ('dir /b/os "lib\*.jar"') DO SET JAR=%%i

SET LOCALDIR=%~dp0
SET JAVA_HOME=%LOCALDIR%jre
SET JAVA=%JAVA_HOME%\bin\java.exe

CD /D %~dp0

REM START "" /B /I /WAIT 
REM -- https://docs.microsoft.com/en-us/windows-server/administration/windows-commands/start

CALL %JAVA% -Dlog4j2.properties=%LOG4J2XML% -jar "lib\%JAR%" -f %CONFIGFILE% %*
EXIT /B