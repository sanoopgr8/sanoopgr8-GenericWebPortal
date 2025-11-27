@echo off
REM DATABASE_PASSWORD should be set as environment variable before running this script
REM Example: set DATABASE_PASSWORD=your_password
if "%DATABASE_PASSWORD%"=="" (
    echo WARNING: DATABASE_PASSWORD environment variable is not set!
    echo Set it with: set DATABASE_PASSWORD=your_password
)
java -jar target\demo-0.0.1-SNAPSHOT.jar
