# Start Frontend on Port 443 (Requires Administrator)
# This script must be run as Administrator

Write-Host "Starting Frontend Server on Port 443 (HTTPS)..." -ForegroundColor Green
Write-Host "Press Ctrl+C to stop the server" -ForegroundColor Yellow
Write-Host ""

# Change to the frontend directory
Set-Location -Path $PSScriptRoot

# Run npm dev server
npm run dev
