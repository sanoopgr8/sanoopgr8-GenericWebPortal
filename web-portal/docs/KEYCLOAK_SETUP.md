# Keycloak Setup Guide for Windows

This guide provides step-by-step instructions for installing and configuring Keycloak on Windows for SSO integration with the Web Portal.

## Prerequisites

### Required Software
- **Java 17 or higher (JDK)** - [Download from Adoptium](https://adoptium.net/)
  - Verify installation: `java -version`
- **PostgreSQL** (optional) - Can use embedded H2 database for development
- **Administrator access** for installation

### Verify Java Installation
```powershell
java -version
```
Expected output should show version 17 or higher.

## Installation Methods

### Option 1: Automated Installation (Recommended)

1. Open PowerShell as Administrator
2. Navigate to the project directory:
   ```powershell
   cd e:\aiexploration\antigravity\web-portal
   ```

3. Run the installation script:
   ```powershell
   .\scripts\install-keycloak.ps1
   ```

4. The script will:
   - Check Java installation
   - Download Keycloak 23.0.1
   - Extract to `C:\keycloak`
   - Configure for port 8180
   - Create startup scripts

### Option 2: Manual Installation

1. **Download Keycloak**
   - Visit [Keycloak Downloads](https://www.keycloak.org/downloads)
   - Download the latest ZIP distribution (e.g., `keycloak-23.0.1.zip`)

2. **Extract Keycloak**
   ```powershell
   Expand-Archive -Path keycloak-23.0.1.zip -DestinationPath C:\keycloak
   ```

3. **Configure Keycloak**
   - Edit `C:\keycloak\conf\keycloak.conf`:
   ```properties
   # HTTP Settings
   http-enabled=true
   http-port=8180
   hostname-strict=false
   hostname-strict-https=false

   # Database (H2 for development)
   db=dev-file
   ```

4. **Set Admin Credentials** (Environment Variables)
   ```powershell
   $env:KEYCLOAK_ADMIN="admin"
   $env:KEYCLOAK_ADMIN_PASSWORD="admin"
   ```

## Starting Keycloak

### Development Mode (Recommended for Testing)

```powershell
cd C:\keycloak
.\bin\kc.bat start-dev --http-port=8180
```

Wait for the message: `Listening on: http://0.0.0.0:8180`

### Access Admin Console

1. Open browser: `http://localhost:8180`
2. On first startup, create admin user:
   - Username: `admin`
   - Password: `admin` (or your chosen password)

## Keycloak Configuration for Web Portal

### Step 1: Create Realm

1. Log in to Keycloak Admin Console
2. Click dropdown in top-left (currently shows "master")
3. Click **"Create Realm"**
4. Enter realm name: `webportal`
5. Click **"Create"**

### Step 2: Create Client

1. In the `webportal` realm, go to **Clients** → **Create client**
2. **General Settings**:
   - Client type: `OpenID Connect`
   - Client ID: `webportal-client`
   - Click **"Next"**

3. **Capability config**:
   - Client authentication: **ON** (confidential client)
   - Authorization: **OFF**
   - Authentication flow:
     - ✅ Standard flow
     - ✅ Direct access grants
   - Click **"Next"**

4. **Login settings**:
   - Valid redirect URIs:
     ```
     http://localhost:5173/*
     https://localhost/*
     http://localhost:8080/*
     ```
   - Valid post logout redirect URIs:
     ```
     http://localhost:5173/*
     https://localhost/*
     ```
   - Web origins:
     ```
     http://localhost:5173
     https://localhost
     http://localhost:8080
     ```
   - Click **"Save"**

5. **Get Client Secret**:
   - Go to **Credentials** tab
   - Copy the **Client Secret** (you'll need this for the Settings page)

### Step 3: Configure AD/LDAP User Federation (Optional)

> **Note**: This step is only needed if you want to authenticate users from Active Directory.

1. Go to **User Federation** → **Add provider** → **ldap**

2. **General Options**:
   - Console display name: `Active Directory`
   - Vendor: `Active Directory`

3. **Connection and Authentication**:
   - Connection URL: `ldap://your-domain-controller:389`
     - Example: `ldap://dc.company.local:389`
   - Bind type: `simple`
   - Bind DN: `CN=ServiceAccount,CN=Users,DC=company,DC=local`
   - Bind credential: `<service-account-password>`

4. **LDAP Searching and Updating**:
   - Edit mode: `READ_ONLY` (or `WRITABLE` if you want to sync changes)
   - Users DN: `CN=Users,DC=company,DC=local`
   - Username LDAP attribute: `sAMAccountName`
   - RDN LDAP attribute: `cn`
   - UUID LDAP attribute: `objectGUID`
   - User object classes: `person, organizationalPerson, user`

5. **Synchronization Settings**:
   - Import users: **ON**
   - Sync registrations: **OFF** (for read-only)
   - Periodic full sync: **ON**
   - Full sync period: `86400` (24 hours)
   - Periodic changed users sync: **ON**
   - Changed users sync period: `3600` (1 hour)

6. **Test Connection**:
   - Click **"Test connection"** button
   - Should show success message

7. **Test Authentication**:
   - Click **"Test authentication"** button
   - Should show success message

8. **Save** the configuration

9. **Sync Users**:
   - Click **"Synchronize all users"** button
   - Check that users are imported

### Step 4: Configure User Attribute Mappings

1. Go to **User Federation** → **Active Directory** → **Mappers** tab

2. Verify/Create mappers for:
   - **email**: Maps `mail` LDAP attribute to `email` user attribute
   - **first name**: Maps `givenName` LDAP attribute to `firstName`
   - **last name**: Maps `sn` LDAP attribute to `lastName`

3. If missing, create new mapper:
   - Click **"Add mapper"**
   - Name: `email`
   - Mapper type: `user-attribute-ldap-mapper`
   - User model attribute: `email`
   - LDAP attribute: `mail`
   - Read only: **ON**
   - Always read value from LDAP: **ON**
   - Click **"Save"**

## Testing Keycloak Configuration

### Test 1: Create Test User (Non-AD)

1. Go to **Users** → **Add user**
2. Fill in:
   - Username: `testuser`
   - Email: `testuser@example.com`
   - First name: `Test`
   - Last name: `User`
   - Email verified: **ON**
3. Click **"Create"**
4. Go to **Credentials** tab
5. Click **"Set password"**
6. Enter password and set **Temporary** to **OFF**
7. Click **"Save"**

### Test 2: Test Login Flow

1. Open new incognito browser window
2. Navigate to: `http://localhost:8180/realms/webportal/account`
3. You should be redirected to Keycloak login page
4. Log in with test user credentials
5. Should see account management page

### Test 3: Test AD User (if configured)

1. Try logging in with AD credentials
2. Should successfully authenticate
3. User should be created in Keycloak automatically

## Configuring PostgreSQL Database (Optional)

For production use, configure Keycloak to use PostgreSQL instead of H2:

1. Create Keycloak database:
   ```sql
   CREATE DATABASE keycloak;
   CREATE USER keycloak_user WITH PASSWORD 'keycloak_password';
   GRANT ALL PRIVILEGES ON DATABASE keycloak TO keycloak_user;
   ```

2. Edit `C:\keycloak\conf\keycloak.conf`:
   ```properties
   # Database
   db=postgres
   db-url=jdbc:postgresql://localhost:5432/keycloak
   db-username=keycloak_user
   db-password=keycloak_password
   ```

3. Restart Keycloak

## Running Keycloak as Windows Service

To run Keycloak as a Windows service for auto-start:

1. Install [NSSM (Non-Sucking Service Manager)](https://nssm.cc/download)

2. Open PowerShell as Administrator:
   ```powershell
   nssm install Keycloak "C:\keycloak\bin\kc.bat" "start --http-port=8180"
   nssm set Keycloak AppDirectory "C:\keycloak"
   nssm set Keycloak DisplayName "Keycloak SSO Server"
   nssm set Keycloak Description "Keycloak Identity and Access Management"
   nssm set Keycloak Start SERVICE_AUTO_START
   ```

3. Set environment variables for the service:
   ```powershell
   nssm set Keycloak AppEnvironmentExtra KEYCLOAK_ADMIN=admin KEYCLOAK_ADMIN_PASSWORD=admin
   ```

4. Start the service:
   ```powershell
   nssm start Keycloak
   ```

5. Check service status:
   ```powershell
   nssm status Keycloak
   ```

## Troubleshooting

### Keycloak Won't Start

**Issue**: Error about Java not found
- **Solution**: Ensure Java 17+ is installed and in PATH
- Verify: `java -version`

**Issue**: Port 8180 already in use
- **Solution**: Change port in configuration or stop conflicting service
- Check: `netstat -ano | findstr :8180`

### Cannot Access Admin Console

**Issue**: Browser shows "Connection refused"
- **Solution**: Ensure Keycloak is running
- Check console output for errors

**Issue**: "Invalid redirect URI" error
- **Solution**: Verify redirect URIs in client configuration match your frontend URL

### AD Federation Not Working

**Issue**: "Cannot connect to LDAP server"
- **Solution**: 
  - Verify domain controller address and port
  - Check firewall allows LDAP traffic (port 389/636)
  - Test with: `Test-NetConnection -ComputerName dc.company.local -Port 389`

**Issue**: "Authentication failed"
- **Solution**:
  - Verify bind DN and credentials are correct
  - Ensure service account has read permissions on AD

**Issue**: Users not syncing
- **Solution**:
  - Check Users DN path is correct
  - Verify user object classes match your AD schema
  - Check sync logs in Keycloak console

## Next Steps

After Keycloak is configured:

1. Copy the **Client Secret** from the `webportal-client` credentials tab
2. Configure SSO settings in the Web Portal Settings page:
   - Server URL: `http://localhost:8180`
   - Realm: `webportal`
   - Client ID: `webportal-client`
   - Client Secret: `<paste-from-keycloak>`
3. Test SSO login from the Web Portal

## Additional Resources

- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [Server Administration Guide](https://www.keycloak.org/docs/latest/server_admin/)
- [LDAP/AD Federation](https://www.keycloak.org/docs/latest/server_admin/#_ldap)
