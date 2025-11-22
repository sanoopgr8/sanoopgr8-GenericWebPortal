# Check for administrative privileges
if (!([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")) {
    Write-Warning "This script may require Administrator privileges to install software."
    Write-Warning "If installation fails, please run PowerShell as Administrator."
}

Write-Host "Checking for PostgreSQL..." -ForegroundColor Cyan

if (Get-Command "psql" -ErrorAction SilentlyContinue) {
    Write-Host "PostgreSQL client found." -ForegroundColor Green
}
else {
    Write-Host "PostgreSQL not found. Attempting to install via Winget..." -ForegroundColor Yellow
    if (Get-Command "winget" -ErrorAction SilentlyContinue) {
        try {
            # Install PostgreSQL
            winget install PostgreSQL.PostgreSQL -e --source winget --accept-source-agreements --accept-package-agreements
            
            if ($?) {
                Write-Host "Installation initiated. If it completed successfully, you MUST restart your terminal/PowerShell to update your PATH." -ForegroundColor Magenta
                Write-Host "After restarting the terminal, run this script again to create the database." -ForegroundColor Magenta
                exit
            }
            else {
                Write-Error "Installation failed. Please install PostgreSQL manually."
                exit
            }
        }
        catch {
            Write-Error "An error occurred during installation: $_"
            exit
        }
    }
    else {
        Write-Error "Winget not found. Please install PostgreSQL manually from https://www.postgresql.org/download/windows/"
        exit
    }
}

# Database Configuration
$DB_NAME = "webportal"
$DB_USER = "postgres"

Write-Host "`nChecking database configuration..." -ForegroundColor Cyan

# Check if service is running (basic check, might not work if service name differs, but good attempt)
$service = Get-Service -Name "postgresql*" -ErrorAction SilentlyContinue
if ($service) {
    if ($service.Status -ne 'Running') {
        Write-Host "Starting PostgreSQL service..." -ForegroundColor Yellow
        Start-Service $service.Name
    }
}

Write-Host "Attempting to create database '$DB_NAME'..." -ForegroundColor Cyan
Write-Host "You may be prompted for the PostgreSQL password for user '$DB_USER'." -ForegroundColor Yellow

# Check if database exists using psql
# We use invoke-expression or direct call. 
# We need to handle the case where psql asks for password.
# 'createdb' is a wrapper that might be easier.

try {
    # List databases to check existence
    $list = psql -U $DB_USER -lqt 2>&1
    
    if ($list -match $DB_NAME) {
        Write-Host "Database '$DB_NAME' already exists." -ForegroundColor Green
    }
    else {
        # Create database
        createdb -U $DB_USER $DB_NAME
        if ($?) {
            Write-Host "Database '$DB_NAME' created successfully!" -ForegroundColor Green
        }
        else {
            Write-Error "Failed to create database. Please check your password and ensure PostgreSQL is running."
        }
    }
}
catch {
    Write-Error "Failed to connect to PostgreSQL. Please ensure the service is running."
    Write-Error "Error details: $_"
}

Write-Host "`nSetup complete." -ForegroundColor Cyan
