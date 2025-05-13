@echo off
setlocal

:: Demarre Tomcat (CATALINA_HOME) et ouvre l'application dans le navigateur.
:: La configuration de la base (BOULANGERIE_DB_URL / _USER / _PASSWORD) doit
:: etre dans l'environnement de Tomcat, par exemple via %CATALINA_HOME%\bin\setenv.bat.
if not defined CATALINA_HOME (
    echo Definir CATALINA_HOME ^(dossier de Tomcat^).
    exit /b 1
)

call "%CATALINA_HOME%\bin\catalina.bat" start
start "" http://localhost:8080/boulangerie/

endlocal
