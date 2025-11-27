# Build and Deploy Backend Script

Write-Host "Building backend..." -ForegroundColor Cyan

# Build using Maven wrapper
.\mvnw.cmd clean package -DskipTests 2>&1 | Tee-Object -Variable buildOutput

if ($LASTEXITCODE -ne 0) {
    Write-Host "`nBuild failed! Error details:" -ForegroundColor Red
    Write-Host $buildOutput -ForegroundColor Yellow
    
    Write-Host "`nTrying to compile only..." -ForegroundColor Cyan
    .\mvnw.cmd compile 2>&1 | Tee-Object -Variable compileOutput
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "`nCompilation errors found:" -ForegroundColor Red
        $compileOutput | Select-String -Pattern "\[ERROR\]" | ForEach-Object { Write-Host $_ -ForegroundColor Red }
        exit 1
    }
}

Write-Host "`nBuild successful!" -ForegroundColor Green

# Stop existing backend if running
Write-Host "`nStopping existing backend..." -ForegroundColor Cyan
Get-Process -Name java -ErrorAction SilentlyContinue | Where-Object { $_.Path -like "*java*" } | Stop-Process -Force

Start-Sleep -Seconds 2

# Start backend
Write-Host "`nStarting backend..." -ForegroundColor Cyan
# DATABASE_PASSWORD should be set as environment variable before running this script
# Example: $env:DATABASE_PASSWORD = 'your_password'
if (-not $env:DATABASE_PASSWORD) {
    Write-Host "WARNING: DATABASE_PASSWORD environment variable is not set!" -ForegroundColor Yellow
    Write-Host "Set it with: `$env:DATABASE_PASSWORD = 'your_password'" -ForegroundColor Yellow
}
java -jar target\demo-0.0.1-SNAPSHOT.jar
