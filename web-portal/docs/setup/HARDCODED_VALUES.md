# Hardcoded Values Audit

This document lists all hardcoded values found in the web portal codebase that should be externalized for better configuration management.

## Summary

✅ **Most values are already properly configured!**
- Backend configuration uses `application.properties` with environment variable overrides
- Frontend uses relative API paths (proxied through nginx)
- nginx configuration uses external files

⚠️ **A few values need attention** - listed below

---

## Backend Configuration (application.properties)

### Database Connection
**File**: `backend/src/main/resources/application.properties`

```properties
# Current
spring.datasource.url=${DATABASE_URL:jdbc:postgresql://localhost:5432/webportal}
spring.datasource.username=${DATABASE_USERNAME:postgres}
spring.datasource.password=${DATABASE_PASSWORD:password}
```

**Status**: ✅ **FIXED** - Uses environment variables with defaults
**Environment Variables**:
- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`

### Email Configuration
**File**: `backend/src/main/resources/application.properties`

```properties
# Current
spring.mail.host=${MAIL_HOST:smtp.example.com}
spring.mail.port=${MAIL_PORT:587}
spring.mail.username=${MAIL_USERNAME:noreply@example.com}
spring.mail.password=${MAIL_PASSWORD:password}
...
mail.from.email=${MAIL_FROM_EMAIL:noreply@example.com}
mail.from.name=${MAIL_FROM_NAME:Your Application}
```

**Status**: ✅ **FIXED** - Uses environment variables with defaults
**Environment Variables**:
- `MAIL_HOST`
- `MAIL_PORT`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `MAIL_FROM_EMAIL`
- `MAIL_FROM_NAME`

### Application Base URL
**File**: `backend/src/main/resources/application.properties`

```properties
# Current
app.base.url=${APP_BASE_URL:https://localhost:8080}
```

**Status**: ✅ **FIXED** - Uses environment variable with default
**Environment Variables**:
- `APP_BASE_URL`

---

## nginx Configuration

### Server Names
**File**: `nginx.conf`

```nginx
# Current (Lines 27, 35)
server_name localhost;
```

**Status**: ⚠️ HARDCODED
**Note**: nginx doesn't support environment variables natively. Options:
1. Use template file and substitute at deployment time
2. Use nginx-template tools
3. Keep as-is for simple deployments (current approach)

### SSL Certificate Paths
**File**: `nginx.conf`

```nginx
# Current (Lines 38-39)
ssl_certificate /path/to/server.crt;
ssl_certificate_key /path/to/server.key;
```

**Status**: ⚠️ HARDCODED ABSOLUTE PATH
**Recommendation**: 
- Use relative paths from nginx installation directory
- Or use standard paths like `/etc/nginx/ssl/`
- For development, document in README.md

### Upstream Servers
**File**: `nginx.conf`

```nginx
# Current (Lines 16, 21)
server localhost:5174;  # Frontend (Vite)
server localhost:8080;  # Backend (Spring Boot)
```

**Status**: ✅ Good for development
**Note**: These are standard development ports and localhost is appropriate

---

## Frontend (JavaScript/React)

### API Calls
**Files**: `frontend/src/*.jsx`

**Status**: ✅ ALL FIXED - Using relative paths
All API calls now use relative paths like `/api/signup`, `/api/login`, etc.
nginx proxies these to the backend, eliminating hardcoded URLs.

### Vite Configuration
**File**: `frontend/vite.config.js`

```javascript
// Current (Lines 9-11)
host: '0.0.0.0',
port: 5174,
strictPort: false,
```

**Status**: ✅ Good - Standard development configuration

---

## Recommendations Priority

### High Priority
1. **nginx SSL paths**: Document expected paths in deployment guide
2. **nginx server_name**: Create deployment template or document in setup guide

### Completed
- Backend Database Credentials
- Backend Email Configuration
- Backend App Base URL
- Frontend API URLs

---

## Environment Variables Summary

### Required for Production

```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/webportal
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=<secure-password>

# Email
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=<your-email>
MAIL_PASSWORD=<app-specific-password>
MAIL_FROM_EMAIL=noreply@yourdomain.com
MAIL_FROM_NAME="Your App Name"

# Application
APP_BASE_URL=https://yourdomain.com
```

### Optional (have defaults)

```bash
# Spring Boot
SERVER_PORT=8080

# Liquibase
SPRING_LIQUIBASE_ENABLED=true
```

---

## Deployment Checklist

- [ ] Set all required environment variables
- [ ] Update nginx configuration with production server names
- [ ] Update nginx SSL certificate paths
- [ ] Build frontend for production
- [ ] Build backend JAR
- [ ] Configure nginx to start on boot
- [ ] Test all features in production environment

---

## Files Reviewed

✅ `backend/src/main/resources/application.properties`
✅ `backend/src/main/java/com/example/demo/*.java`
✅ `frontend/src/*.jsx`
✅ `frontend/vite.config.js`
✅ `nginx.conf`

---

**Generated**: 2025-11-20
**Updated**: 2025-11-21
**Status**: Backend secrets externalized. Nginx config remains hardcoded (expected).
