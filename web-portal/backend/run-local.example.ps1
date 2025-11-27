# Local Development Startup Script Template
# Copy this file to run-local.ps1 and update with your local settings
# run-local.ps1 is in .gitignore and will not be committed

Write-Host "Starting backend with local configuration..." -ForegroundColor Cyan

# Stop existing backend if running
Get-Process -Name java -ErrorAction SilentlyContinue | Stop-Process -Force
Start-Sleep -Seconds 2

# Set your local environment variables here
$env:DATABASE_PASSWORD = 'your_password_here'
$env:DATABASE_URL = 'jdbc:postgresql://localhost:5432/webportal'
$env:DATABASE_USERNAME = 'postgres'

# Optional: Mail configuration
# $env:MAIL_HOST = 'smtp.gmail.com'
# $env:MAIL_PORT = '587'
# $env:MAIL_USERNAME = 'your-email@example.com'
# $env:MAIL_PASSWORD = 'your-app-password'

# Optional: Keycloak configuration
# $env:KEYCLOAK_SERVER_URL = 'http://localhost:8180'
# $env:KEYCLOAK_REALM = 'webportal'
# $env:KEYCLOAK_CLIENT_ID = 'webportal-client'
# $env:KEYCLOAK_CLIENT_SECRET = 'your-client-secret'

Write-Host "Environment variables set" -ForegroundColor Green
java -jar target\demo-0.0.1-SNAPSHOT.jar
