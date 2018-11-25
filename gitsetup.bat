@echo off
set REP_PATH=https://github.com/danthe1st/DanBot1.git
::cd GIT
git init
git remote remove origin
git remote add origin %REP_PATH%
git add .



pause>nul