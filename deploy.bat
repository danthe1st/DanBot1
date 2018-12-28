@echo off

:: build
cmd /C mvn package

:: move to directory DanBot1
ren DanBot1\DanBot1-jar-with-dependencies.jar DanBot1.jar

pause