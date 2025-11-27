# Security Configuration Guide

## Environment Variables

This application uses environment variables for sensitive configuration. **Never commit passwords or secrets to the repository.**

### Required Environment Variables

#### Database Configuration
```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/webportal
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_secure_password
```

#### Mail Configuration (Optional)
```bash
MAIL_HOST=smtp.example.com
MAIL_PORT=587
MAIL_USERNAME=your-email@example.com
MAIL_PASSWORD=your_app_specific_password
MAIL_FROM_EMAIL=noreply@example.com
MAIL_FROM_NAME=Web Portal
```

#### Keycloak SSO Configuration (Optional)
```bash
KEYCLOAK_ENABLED=true
KEYCLOAK_SERVER_URL=http://localhost:8180
KEYCLOAK_REALM=webportal
KEYCLOAK_CLIENT_ID=webportal-client
KEYCLOAK_CLIENT_SECRET=your_client_secret
```

## Local Development Setup

### Option 1: Using PowerShell Script (Recommended)

1. Copy the example script:
   ```powershell
   copy backend\run-local.example.ps1 backend\run-local.ps1
   ```

2. Edit `backend\run-local.ps1` with your local settings

3. Run the backend:
   ```powershell
   cd backend
   .\run-local.ps1
   ```

**Note:** `run-local.ps1` is in `.gitignore` and will not be committed.

### Option 2: Set Environment Variables Manually

**Windows PowerShell:**
```powershell
$env:DATABASE_PASSWORD = 'your_password'
$env:DATABASE_USERNAME = 'postgres'
$env:DATABASE_URL = 'jdbc:postgresql://localhost:5432/webportal'

cd backend
.\build-and-run.ps1
```

**Windows CMD:**
```cmd
set DATABASE_PASSWORD=your_password
set DATABASE_USERNAME=postgres
set DATABASE_URL=jdbc:postgresql://localhost:5432/webportal

cd backend
start-backend.bat
```

**Linux/Mac:**
```bash
export DATABASE_PASSWORD='your_password'
export DATABASE_USERNAME='postgres'
export DATABASE_URL='jdbc:postgresql://localhost:5432/webportal'

cd backend
./mvnw spring-boot:run
```

### Option 3: IDE Configuration

**IntelliJ IDEA / Eclipse:**
1. Right-click on `DemoApplication.java`
2. Run → Edit Configurations
3. Add Environment Variables:
   - `DATABASE_PASSWORD=your_password`
   - `DATABASE_USERNAME=postgres`
   - etc.

## Production Deployment

### Using System Environment Variables

**Linux (systemd service):**
```ini
[Service]
Environment="DATABASE_PASSWORD=your_secure_password"
Environment="DATABASE_USERNAME=postgres"
Environment="DATABASE_URL=jdbc:postgresql://localhost:5432/webportal"
```

**Docker:**
```yaml
services:
  backend:
    environment:
      - DATABASE_PASSWORD=your_secure_password
      - DATABASE_USERNAME=postgres
      - DATABASE_URL=jdbc:postgresql://db:5432/webportal
```

**Kubernetes:**
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: webportal-secrets
type: Opaque
stringData:
  database-password: your_secure_password
---
apiVersion: apps/v1
kind: Deployment
spec:
  template:
    spec:
      containers:
      - name: backend
        env:
        - name: DATABASE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: webportal-secrets
              key: database-password
```

## CORS Configuration

The application is currently configured for **development** with permissive CORS settings.

### Development (Current)
- Allows localhost and local network access
- Located in: `SecurityConfig.java`

### Production Recommendations

**Update `SecurityConfig.java` for production:**

```java
configuration.setAllowedOriginPatterns(Arrays.asList(
    "https://yourdomain.com",
    "https://www.yourdomain.com"
));
```

**Production Checklist:**
- ✅ Use HTTPS only
- ✅ Specify exact domain names (no wildcards)
- ✅ Remove development IP patterns
- ✅ Configure SSL certificates
- ✅ Use reverse proxy (nginx, Apache)
- ✅ Enable rate limiting
- ✅ Implement proper authentication for all endpoints

## Password Security

✅ **User passwords are hashed using BCrypt** with salt.
✅ **Passwords are never stored in plain text** in the database.
✅ **Infrastructure passwords use environment variables**.

## Files That Should Never Be Committed

The following are in `.gitignore`:
- `*.local.ps1` - Local development scripts
- `*.local.bat` - Local batch files
- `*-output.txt` - Runtime logs
- `.env` - Environment files
- `backend-output.txt` - Application logs

## Security Best Practices

1. **Never commit passwords** to version control
2. **Use strong passwords** for all services
3. **Rotate secrets regularly** in production
4. **Use separate credentials** for dev/staging/production
5. **Enable SSL/TLS** for production
6. **Regular security updates** for dependencies
7. **Monitor access logs** for suspicious activity
8. **Use secret management tools** (HashiCorp Vault, AWS Secrets Manager, Azure Key Vault)

## Getting Help

If you accidentally commit sensitive information:

1. **Immediately change the password/secret**
2. **Remove from git history:**
   ```bash
   git filter-branch --force --index-filter \
     "git rm --cached --ignore-unmatch path/to/file" \
     --prune-empty --tag-name-filter cat -- --all
   ```
3. **Force push (if repository is private and you have permission)**
4. **Consider the secret compromised** and rotate it

## Contact

For security concerns, please contact the development team immediately.
