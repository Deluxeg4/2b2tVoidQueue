@echo off
cd /d "%~dp0"
java -Xms2G -Xmx6G -jar "purpur-server\build\libs\purpur-bundler-1.21.11-R0.1-SNAPSHOT-mojmap.jar" --nogui
pause
