@echo off
echo Starting Web Portal Setup...
powershell -ExecutionPolicy Bypass -File "%~dp0scripts\setup-db.ps1"
pause
