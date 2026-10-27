@echo off
REM Hotel Room Reservation System - start file
REM 1 = Build web application (WAR file for Tomcat)
REM 2 = Run desktop application (Swing GUI)
REM Before running: set your MySQL password in both DBConnect.java files
REM and run database\hotel_db.sql in MySQL once.

cd /d "%~dp0"
title Hotel Room Reservation System

set "MYSQL_JAR=%USERPROFILE%\.m2\repository\com\mysql\mysql-connector-j\9.4.0\mysql-connector-j-9.4.0.jar"
set "SERVLET_JAR=%USERPROFILE%\.m2\repository\jakarta\servlet\jakarta.servlet-api\6.0.0\jakarta.servlet-api-6.0.0.jar"

echo.
echo  Hotel Room Reservation System
echo  -----------------------------
echo  1. Build web application (WAR)
echo  2. Run desktop application
echo  3. Exit
echo.
set /p choice=Enter choice (1-3):

if "%choice%"=="1" goto web
if "%choice%"=="2" goto desktop
goto end

:web
echo.
echo Building web application...
where mvn >nul 2>nul
if %errorlevel%==0 (
    call mvn -q package -DskipTests
    if %errorlevel%==0 goto webdone
    echo Maven build failed. Trying simple build...
)

REM Simple build without Maven: compile and pack the WAR by hand.
if exist target\classes rmdir /s /q target\classes
mkdir target\classes
dir /s /b src\main\java\*.java > target\sources.txt
javac -cp "%SERVLET_JAR%" -d target\classes @target\sources.txt
if not %errorlevel%==0 goto fail

if exist target\HotelRoomReservationSystem rmdir /s /q target\HotelRoomReservationSystem
mkdir target\HotelRoomReservationSystem\WEB-INF\classes
mkdir target\HotelRoomReservationSystem\WEB-INF\lib
xcopy /e /y src\main\webapp\* target\HotelRoomReservationSystem\ >nul
xcopy /e /y target\classes\* target\HotelRoomReservationSystem\WEB-INF\classes\ >nul
copy /y "%MYSQL_JAR%" target\HotelRoomReservationSystem\WEB-INF\lib\ >nul
if exist target\HotelRoomReservationSystem.war del target\HotelRoomReservationSystem.war
set "JAR_EXE=jar"
where jar >nul 2>nul
if not %errorlevel%==0 (
    for /d %%D in ("C:\Program Files\Java\*") do if exist "%%D\bin\jar.exe" set "JAR_EXE=%%D\bin\jar.exe"
)
cd target\HotelRoomReservationSystem
"%JAR_EXE%" cf ..\HotelRoomReservationSystem.war *
cd ..\..
if not exist target\HotelRoomReservationSystem.war goto fail

:webdone
echo.
echo Web application built: target\HotelRoomReservationSystem.war
echo Deploy this WAR in Tomcat 10.1, or open the project in NetBeans and press Run.
echo Then open in browser: http://localhost:8080/HotelRoomReservationSystem/
pause
goto end

:desktop
echo.
echo Compiling desktop application...
if exist desktop\classes rmdir /s /q desktop\classes
mkdir desktop\classes
dir /s /b desktop\src\*.java > desktop\sources.txt
javac -d desktop\classes @desktop\sources.txt
if not %errorlevel%==0 goto fail
echo Starting desktop application...
java -cp "desktop\classes;%MYSQL_JAR%" com.hotel.desktop.HotelDesktopApp
goto end

:fail
echo.
echo Build failed. Read the error above.
pause

:end
