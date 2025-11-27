# Run Backend (no build)
Write-Host "Starting backend..." -ForegroundColor Cyan

# Stop existing backend if running
Get-Process -Name java -ErrorAction SilentlyContinue | Stop-Process -Force
Start-Sleep -Seconds 2

# DATABASE_PASSWORD should be set as environment variable before running this script
# Example: $env:DATABASE_PASSWORD = 'your_password'
if (-not $env:DATABASE_PASSWORD) {
    Write-Host "WARNING: DATABASE_PASSWORD environment variable is not set!" -ForegroundColor Yellow
    Write-Host "Set it with: `$env:DATABASE_PASSWORD = 'your_password'" -ForegroundColor Yellow
}
java -jar target\demo-0.0.1-SNAPSHOT.jar
