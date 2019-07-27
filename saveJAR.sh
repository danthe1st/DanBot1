#!/bin/bash

git clone https://github.com/danthe1st/DanBot1.wiki.git wiki
mvn package -DskipTests=true
cp target/DanBot1.jar wiki/DanBot1.jar
cd wiki
git add DanBot1.jar
git commit -m "CI JAR deploy: `date`"
(echo $githubUsername ; echo $githubToken) | git push