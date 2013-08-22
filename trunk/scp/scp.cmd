@echo off
setlocal EnableDelayedExpansion
set JAVA_OPTS=-Xms256m -Xmx1024m

for /f %%i in ("%0") do set SCP_HOME=%%~dpi
set SCP_HOME=%SCP_HOME:~0,-1%

set LIB_DIR=%SCP_HOME%\lib
set RES_DIR=%SCP_HOME%\res
set BIN_DIR=%SCP_HOME%\bin

set CPATH=%BIN_DIR%\scp.jar
set CPATH=%RES_DIR%;%CPATH%
for /R %SCP_HOME%\lib %%a in (*.jar) do (
   set CPATH=!CPATH!;%%a
)

:checkJava
set _JAVACMD=%JAVACMD%

if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\java.exe
goto runScp

:noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=java.exe
 
:checkScpConfigFile
if "%SCP_CONFIG_FILE%" == "" set SCP_CONFIG_FILE=-Dscp_config_file="%RES_DIR%\scp_config_file.properties"
 
:runScp
"%_JAVACMD%" %JAVA_OPTS% -classpath "%CPATH%" -Dscp_config_file="%SCP_CONFIG_FILE%" -Dscp.home="%SCP_HOME%" uk.co.marcoratto.scp.Runme %*
endlocal
