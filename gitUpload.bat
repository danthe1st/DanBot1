@echo off
set REP_PATH=https://github.com/danthe1st/DanBot1.git
::cd GIT

git remote add origin %REP_PATH%
git add .

echo enter Commit name:
if x%var%==x (
	::empty
	set var=%TIME%
)

git commit -m %TIME%"
git push -u origin --all -f
git remote remove origin
pause>nul