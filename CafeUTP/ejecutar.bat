@echo off
rem Compila y ejecuta CafeUTP solo con Java (no necesita Maven ni IDE).
cd /d "%~dp0"
if exist target\classes rmdir /s /q target\classes
mkdir target\classes
dir /s /b src\main\java\*.java > fuentes.txt
javac -encoding UTF-8 -d target\classes @fuentes.txt
del fuentes.txt
java -cp target\classes cafeteria.app.Main
pause
