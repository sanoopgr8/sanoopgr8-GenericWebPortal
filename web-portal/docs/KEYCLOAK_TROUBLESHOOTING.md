# Keycloak Troubleshooting Guide

## Common Errors and Solutions

### Error: "Network response was not OK" in Admin Console

This error typically occurs when:
1. Keycloak is still starting up
2. Database connection issues
3. Port conflicts
4. Configuration problems

### Solutions

#### 1. Check if Keycloak is Fully Started

**Symptoms:**
- Admin console shows "Network response was not OK"
- Error in browser console

**Solution:**
Wait for Keycloak to fully start. Look for this message in the console:
```
Listening on: http://0.0.0.0:8180
```

This can take 30-60 seconds on first startup.

#### 2. Restart Keycloak in Clean Mode

**Stop Keycloak:**
- Press `Ctrl+C` in the terminal where Keycloak is running

**Clear Cache and Restart:**
```powershell
cd C:\keycloak
# Remove cache
Remove-Item -Path data\h2 -Recurse -Force -ErrorAction SilentlyContinue

# Restart
.\bin\kc.bat start-dev --http-port=8180
```

#### 3. Check Port Availability

**Verify port 8180 is not in use:**
```powershell
netstat -ano | findstr :8180
```

If another process is using port 8180, either:
- Stop that process
- Or use a different port:
  ```powershell
  .\bin\kc.bat start-dev --http-port=8181
  ```

#### 4. Use Production Build (More Stable)

Instead of dev mode, build and start in production mode:

```powershell
cd C:\keycloak

# Build Keycloak
.\bin\kc.bat build

# Start in production mode
.\bin\kc.bat start --http-enabled=true --http-port=8180 --hostname-strict=false
```

**Set admin credentials as environment variables first:**
```powershell
$env:KEYCLOAK_ADMIN="admin"
$env:KEYCLOAK_ADMIN_PASSWORD="admin"
```

#### 5. Check Java Version

Keycloak requires Java 17+:
```powershell
java -version
```

Should show version 17 or higher.

#### 6. Access Admin Console Correctly

**Correct URL:**
```
http://localhost:8180
```

**NOT:**
- ~~http://localhost:8180/admin~~ (this will show the error)
- ~~http://localhost:8180/auth~~

**First Time Setup:**
1. Go to `http://localhost:8180`
2. You'll see "Welcome to Keycloak"
3. Click "Administration Console"
4. Create initial admin user if prompted
5. Or login with credentials you set

#### 7. Browser Cache Issues

**Clear browser cache:**
1. Open DevTools (F12)
2. Right-click refresh button
3. Select "Empty Cache and Hard Reload"

Or try in Incognito/Private mode.

#### 8. Check Keycloak Logs

Look for errors in the console output where Keycloak is running.

Common errors:
- Port already in use
- Database connection failed
- Java version incompatible

### Recommended Startup Procedure

```powershell
# 1. Navigate to Keycloak directory
cd C:\keycloak

# 2. Set admin credentials
$env:KEYCLOAK_ADMIN="admin"
$env:KEYCLOAK_ADMIN_PASSWORD="admin"

# 3. Start Keycloak
.\bin\kc.bat start-dev --http-port=8180

# 4. Wait for startup message
# Look for: "Listening on: http://0.0.0.0:8180"

# 5. Access admin console
# Open browser: http://localhost:8180
```

### Alternative: Use Docker (If Issues Persist)

If you continue having issues with standalone installation:

```powershell
# Pull Keycloak image
docker pull quay.io/keycloak/keycloak:latest

# Run Keycloak
docker run -p 8180:8080 `
  -e KEYCLOAK_ADMIN=admin `
  -e KEYCLOAK_ADMIN_PASSWORD=admin `
  quay.io/keycloak/keycloak:latest `
  start-dev
```

Access at: `http://localhost:8180`

### Still Having Issues?

1. **Check if Keycloak process is running:**
   ```powershell
   Get-Process | Where-Object {$_.ProcessName -like "*java*"}
   ```

2. **Kill any stuck Keycloak processes:**
   ```powershell
   Stop-Process -Name java -Force
   ```

3. **Try a fresh installation:**
   ```powershell
   # Remove old installation
   Remove-Item -Path C:\keycloak -Recurse -Force
   
   # Run installation script again
   .\scripts\install-keycloak.ps1
   ```

### Quick Fix Checklist

- [ ] Wait 60 seconds after starting Keycloak
- [ ] Access `http://localhost:8180` (not `/admin`)
- [ ] Check console for "Listening on" message
- [ ] Try clearing browser cache
- [ ] Restart Keycloak with `Ctrl+C` and start again
- [ ] Verify Java 17+ is installed
- [ ] Check port 8180 is not in use

### Success Indicators

You'll know Keycloak is working when:
1. Console shows: `Listening on: http://0.0.0.0:8180`
2. Browser shows "Welcome to Keycloak" page
3. Can click "Administration Console" without errors
4. Can log in with admin credentials

### Contact Points

If you see the "Welcome to Keycloak" page, you're good! The error you saw was likely because:
- Keycloak was still starting up
- You accessed `/admin` directly instead of the root URL
- Browser cached an error state

**Solution:** Just refresh the page or go to `http://localhost:8180` and start fresh.
