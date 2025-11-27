# Keycloak Installation Script for Windows
# This script downloads and installs Keycloak standalone on Windows

param(
    [string]$InstallPath = "C:\keycloak",
    [string]$Version = "23.0.1",
    [int]$HttpPort = 8180,
    [string]$AdminUser = "admin",
    [string]$AdminPassword = "admin"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Keycloak Installation Script for Windows" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if Java is installed
Write-Host "Checking Java installation..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object { $_.Line }
    Write-Host "Java found: $javaVersion" -ForegroundColor Green
    
    # Extract version number
    if ($javaVersion -match '"(\d+)\.') {
        $javaMajorVersion = [int]$matches[1]
        if ($javaMajorVersion -lt 17) {
            Write-Host "ERROR: Java 17 or higher is required. Found version $javaMajorVersion" -ForegroundColor Red
            Write-Host "Please install Java 17+ from: https://adoptium.net/" -ForegroundColor Yellow
            exit 1
        }
    }
}
catch {
    Write-Host "ERROR: Java not found in PATH" -ForegroundColor Red
    Write-Host "Please install Java 17+ from: https://adoptium.net/" -ForegroundColor Yellow
    exit 1
}

# Create installation directory
Write-Host ""
Write-Host "Creating installation directory: $InstallPath" -ForegroundColor Yellow
if (!(Test-Path $InstallPath)) {
    New-Item -ItemType Directory -Path $InstallPath -Force | Out-Null
    Write-Host "Directory created successfully" -ForegroundColor Green
}
else {
    Write-Host "Directory already exists" -ForegroundColor Yellow
}

# Download Keycloak
$downloadUrl = "https://github.com/keycloak/keycloak/releases/download/$Version/keycloak-$Version.zip"
$zipFile = "$env:TEMP\keycloak-$Version.zip"

Write-Host ""
Write-Host "Downloading Keycloak $Version..." -ForegroundColor Yellow
Write-Host "URL: $downloadUrl" -ForegroundColor Gray

try {
    # Use TLS 1.2
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    
    $ProgressPreference = 'SilentlyContinue'
    Invoke-WebRequest -Uri $downloadUrl -OutFile $zipFile -UseBasicParsing
    $ProgressPreference = 'Continue'
    
    Write-Host "Download completed successfully" -ForegroundColor Green
}
catch {
    Write-Host "ERROR: Failed to download Keycloak" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}

# Extract Keycloak
Write-Host ""
Write-Host "Extracting Keycloak..." -ForegroundColor Yellow

try {
    Expand-Archive -Path $zipFile -DestinationPath $InstallPath -Force
    
    # Move files from keycloak-X.X.X folder to root
    $extractedFolder = Get-ChildItem -Path $InstallPath -Directory | Where-Object { $_.Name -like "keycloak-*" } | Select-Object -First 1
    if ($extractedFolder) {
        Get-ChildItem -Path $extractedFolder.FullName | Move-Item -Destination $InstallPath -Force
        Remove-Item -Path $extractedFolder.FullName -Recurse -Force
    }
    
    Write-Host "Extraction completed successfully" -ForegroundColor Green
}
catch {
    Write-Host "ERROR: Failed to extract Keycloak" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}

# Clean up downloaded file
Remove-Item -Path $zipFile -Force

# Configure Keycloak
Write-Host ""
Write-Host "Configuring Keycloak..." -ForegroundColor Yellow

$configFile = "$InstallPath\conf\keycloak.conf"
$configContent = @"
# Keycloak Configuration for Web Portal

# HTTP Settings
http-enabled=true
http-port=$HttpPort
hostname-strict=false
hostname-strict-https=false

# Database (H2 - Development)
# For production, configure PostgreSQL connection here
db=dev-file

# Admin Console
# Initial admin user will be created on first startup
"@

Set-Content -Path $configFile -Value $configContent -Force
Write-Host "Configuration file created: $configFile" -ForegroundColor Green

# Create startup script
$startupScript = "$InstallPath\start-keycloak.bat"
$startupContent = @"
@echo off
echo Starting Keycloak on port $HttpPort...
echo.
echo Admin Console: http://localhost:$HttpPort
echo.
cd /d "%~dp0"
set KEYCLOAK_ADMIN=$AdminUser
set KEYCLOAK_ADMIN_PASSWORD=$AdminPassword
bin\kc.bat start-dev --http-port=$HttpPort
"@

Set-Content -Path $startupScript -Value $startupContent -Force
Write-Host "Startup script created: $startupScript" -ForegroundColor Green

# Create environment setup script
$envScript = "$InstallPath\set-admin.bat"
$envContent = @"
@echo off
REM Set Keycloak admin credentials
set KEYCLOAK_ADMIN=$AdminUser
set KEYCLOAK_ADMIN_PASSWORD=$AdminPassword
echo Admin credentials set:
echo   Username: %KEYCLOAK_ADMIN%
echo   Password: %KEYCLOAK_ADMIN_PASSWORD%
"@

Set-Content -Path $envScript -Value $envContent -Force

# Installation complete
Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Keycloak Installation Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Installation Details:" -ForegroundColor Cyan
Write-Host "  Location: $InstallPath" -ForegroundColor White
Write-Host "  Version: $Version" -ForegroundColor White
Write-Host "  HTTP Port: $HttpPort" -ForegroundColor White
Write-Host "  Admin User: $AdminUser" -ForegroundColor White
Write-Host "  Admin Password: $AdminPassword" -ForegroundColor White
Write-Host ""
Write-Host "To start Keycloak:" -ForegroundColor Cyan
Write-Host "  1. Open PowerShell or Command Prompt" -ForegroundColor White
Write-Host "  2. Run: $InstallPath\start-keycloak.bat" -ForegroundColor Yellow
Write-Host ""
Write-Host "Or manually:" -ForegroundColor Cyan
Write-Host "  cd $InstallPath" -ForegroundColor Yellow
Write-Host "  .\bin\kc.bat start-dev --http-port=$HttpPort" -ForegroundColor Yellow
Write-Host ""
Write-Host "Access Admin Console:" -ForegroundColor Cyan
Write-Host "  http://localhost:$HttpPort" -ForegroundColor Yellow
Write-Host ""
Write-Host "Note: On first startup, create admin user with:" -ForegroundColor Cyan
Write-Host "  Username: $AdminUser" -ForegroundColor Yellow
Write-Host "  Password: $AdminPassword" -ForegroundColor Yellow
Write-Host ""
