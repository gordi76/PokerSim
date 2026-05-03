@echo off
cd /d "%~dp0"
echo Starting PokerSim...
call gradlew.bat run --console=plain
pause