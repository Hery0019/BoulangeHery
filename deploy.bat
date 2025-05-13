@echo off
setlocal enabledelayedexpansion

:: Construit le WAR et le dépose dans le dossier webapps de Tomcat.
:: Aucun chemin personnel : le projet est le dossier de ce script, Tomcat est
:: donné par CATALINA_HOME (ou surchargé par WEB_APPS).
set "work_dir=%~dp0"
set "work_dir=%work_dir:~0,-1%"
set "temp=%work_dir%\temp"
set "web=%work_dir%\web"
set "web_xml=%work_dir%\web.xml"
set "lib=%work_dir%\lib"
set "src=%work_dir%\src"
set "war_name=boulangerie"
if not defined WEB_APPS (
    if not defined CATALINA_HOME (
        echo Definir CATALINA_HOME ^(dossier de Tomcat^) ou WEB_APPS ^(dossier webapps^).
        exit /b 1
    )
    set "WEB_APPS=%CATALINA_HOME%\webapps"
)

:: Dossier de travail propre
if exist "%temp%" rd /s /q "%temp%"
mkdir "%temp%\WEB-INF\lib"
mkdir "%temp%\WEB-INF\classes"

:: Ressources web et descripteur
xcopy /s /y /q "%web%\*.*" "%temp%" > nul
copy /y "%web_xml%" "%temp%\WEB-INF" > nul

:: Bibliothèques : tout sauf l'API servlet, fournie par Tomcat (Tomcat ignore
:: un servlet-api.jar embarque, mais il n'a rien a faire dans le WAR).
for %%j in ("%lib%\*.jar") do (
    if /I not "%%~nxj"=="servlet-api.jar" copy /y "%%j" "%temp%\WEB-INF\lib" > nul
)

:: Compilation
dir /s /B "%src%\*.java" > "%temp%\sources.txt"
javac -d "%temp%\WEB-INF\classes" -cp "%lib%\*" @"%temp%\sources.txt"
if errorlevel 1 (
    echo Compilation echouee.
    exit /b 1
)
del "%temp%\sources.txt"

:: Archive et deploiement
pushd "%temp%"
jar cf "%work_dir%\%war_name%.war" *
popd
if exist "%WEB_APPS%\%war_name%.war" del /f /q "%WEB_APPS%\%war_name%.war"
copy /y "%work_dir%\%war_name%.war" "%WEB_APPS%" > nul
del "%work_dir%\%war_name%.war"

echo Deploiement termine dans "%WEB_APPS%".
endlocal
