# Conference Management System - Production Deployment Guide

## Quick Start Checklist

- [ ] Read SECURITY_CHECKLIST.md
- [ ] Review all 6 security fixes in SECURITY_FIXES_REPORT.md
- [ ] Set up environment variables
- [ ] Deploy with init-prod.sql
- [ ] Create admin user manually
- [ ] Test all security features
- [ ] Monitor logs post-deployment

## Prerequisites

- Java 21 JDK
- PostgreSQL 18+
- Maven 3.8+
- HTTPS certificate (self-signed or CA)

## Step 1: Build the Project

```bash
cd /path/to/management-system
./mvnw clean package
```

Build artifacts:
- `target/management-system-0.0.1-SNAPSHOT.jar` - Main application JAR

## Step 2: Set Environment Variables

Create a `.env` file or set these in your deployment environment:

```bash
# JWT Security
export JWT_SECRET="$(openssl rand -base64 32)"

# Database
export SPRING_DATASOURCE_URL="jdbc:postgresql://db-host:5432/conference_db"
export SPRING_DATASOURCE_USERNAME="app_user"
export SPRING_DATASOURCE_PASSWORD="$(openssl rand -base64 32)"

# Server
export SERVER_PORT="8080"
export SPRING_PROFILES_ACTIVE="prod"
```

## Step 3: Prepare Database

### Create Database and User
```sql
CREATE DATABASE conference_db;
CREATE USER app_user WITH PASSWORD 'your-secure-password';
GRANT CONNECT ON DATABASE conference_db TO app_user;

-- Connect to conference_db
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO app_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO app_user;

-- Grant existing table privileges
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO app_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO app_user;
```

### Initialize Schema
```bash
# Using init-prod.sql (NO default users)
psql -U app_user -d conference_db -f database/init-prod.sql
```

## Step 4: Create Admin User

### Generate Secure Password Hash

**Option A: Using online BCrypt calculator**
1. Visit https://www.bcryptcalculator.com/
2. Enter your secure password (minimum 12 characters)
3. Select rounds: 10
4. Copy the hash

**Option B: Using Python**
```bash
python -c "from werkzeug.security import generate_password_hash; print(generate_password_hash('YourSecurePassword123!', 'bcrypt'))"
```

**Option C: Using Java**
```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
String hash = new BCryptPasswordEncoder().encode("YourSecurePassword123!");
System.out.println(hash);
```

### Insert Admin User
```sql
psql -U app_user -d conference_db

INSERT INTO users (username, email, password, first_name, last_name, role, is_active)
VALUES (
    'admin',
    'admin@yourdomain.com',
    '$2a$10$[PASTE_BCRYPT_HASH_HERE]',
    'System',
    'Administrator',
    'ADMIN',
    true
);
```

### Verify Admin User
```sql
SELECT * FROM users WHERE username = 'admin';
```

## Step 5: Deploy Application

### Using Docker (Recommended)

```dockerfile
FROM openjdk:21-jdk-slim
ARG JWT_SECRET
ARG SPRING_DATASOURCE_URL
ARG SPRING_DATASOURCE_USERNAME
ARG SPRING_DATASOURCE_PASSWORD

ENV JWT_SECRET=${JWT_SECRET}
ENV SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL}
ENV SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}
ENV SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD}
ENV SPRING_PROFILES_ACTIVE=prod

COPY target/management-system-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080
```

Deploy with:
```bash
docker build \
  --build-arg JWT_SECRET="$(openssl rand -base64 32)" \
  --build-arg SPRING_DATASOURCE_URL="jdbc:postgresql://db:5432/conference_db" \
  --build-arg SPRING_DATASOURCE_USERNAME="app_user" \
  --build-arg SPRING_DATASOURCE_PASSWORD="secure-password" \
  -t conference-system:latest .

docker run -d \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  conference-system:latest
```

### Using systemd (Linux Server)

Create `/etc/systemd/system/conference-system.service`:
```ini
[Unit]
Description=Conference Management System
After=network.target postgresql.service

[Service]
Type=simple
User=app
WorkingDirectory=/opt/conference-system

# Environment variables
EnvironmentFile=/opt/conference-system/.env
Environment="SPRING_PROFILES_ACTIVE=prod"

ExecStart=/usr/bin/java -jar /opt/conference-system/management-system-0.0.1-SNAPSHOT.jar

Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

Start service:
```bash
sudo systemctl daemon-reload
sudo systemctl start conference-system
sudo systemctl enable conference-system
```

Monitor logs:
```bash
sudo journalctl -u conference-system -f
```

## Step 6: Post-Deployment Verification

### Test Admin Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "YourSecurePassword123!"
  }'
```

Expected response:
```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@yourdomain.com",
  "role": "ADMIN",
  "token": null,
  "message": "Login successful"
}
```

**Note**: Token is set in httpOnly cookie (not in response body)

### Test Rate Limiting
```bash
# Run 6 login attempts (5 succeed, 6th should be rate-limited)
for i in {1..6}; do
  curl -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"wrong"}' \
    -w "\nAttempt $i: HTTP %{http_code}\n"
done
```

Expected:
- Attempts 1-5: HTTP 401 (Unauthorized)
- Attempt 6: HTTP 429 (Too Many Requests)

### Test Exception Handling
```bash
# Try to access a non-existent endpoint
curl http://localhost:8080/api/nonexistent \
  -H "Authorization: Bearer invalid-token"
```

Expected: Generic error message, no internal details exposed

## Step 7: Configure Monitoring

### Log Aggregation Setup

**Using ELK Stack:**
```bash
# In application-prod.properties
logging.level.com.conference.management_system=INFO
logging.file.name=/var/log/conference-system/app.log
```

**Using Splunk:**
Configure Splunk agent to monitor `/var/log/conference-system/app.log`

### Alert Configuration

Set up alerts for:
1. **Rate Limit Exceeded**: `grep "Too many login attempts" logs`
2. **Authentication Failures**: `grep "WARN" logs | grep "BadCredentialsException"`
3. **Database Connection Errors**: `grep "ERROR" logs | grep "DataAccessException"`
4. **Exception Handler Warnings**: `grep "ERROR" logs | grep "Unhandled exception"`

## Step 8: Security Maintenance

### Weekly Tasks
- [ ] Review application logs for anomalies
- [ ] Check rate limit triggers in logs
- [ ] Verify backup integrity

### Monthly Tasks
- [ ] Check for dependency security updates: `./mvnw dependency:check-updates`
- [ ] Review access logs for unauthorized attempts
- [ ] Test backup restoration process

### Quarterly Tasks
- [ ] Rotate database password
- [ ] Rotate JWT_SECRET if needed
- [ ] Security audit of application logs

## Troubleshooting

### Application won't start
```bash
# Check logs
tail -f /var/log/conference-system/app.log

# Common issues:
# 1. JWT_SECRET not set
# 2. Database connection failed
# 3. Port 8080 already in use
```

### Database connection failed
```bash
# Test database connectivity
psql -h db-host -U app_user -d conference_db -c "SELECT 1"

# Check credentials in environment variables
echo $SPRING_DATASOURCE_URL
echo $SPRING_DATASOURCE_USERNAME
```

### Admin login fails
```bash
# Verify admin user exists
psql -U app_user -d conference_db -c "SELECT username, role FROM users WHERE username='admin'"

# Check password hash (use BCrypt validator)
# Re-create if needed with correct hash
```

### Rate limiting not working
```bash
# Check if interceptor is registered
# Look for "Login attempt allowed from IP" in logs

# Verify Bucket4j library is included
jar tf target/management-system-0.0.1-SNAPSHOT.jar | grep bucket4j
```

## API Endpoints (After Login)

All endpoints require JWT token in httpOnly cookie (set automatically on login).

### Admin Endpoints
- `GET /api/admin/users` - List all users
- `PUT /api/admin/users/{id}/role` - Change user role
- `DELETE /api/admin/users/{id}` - Delete user

### Proposal Endpoints
- `GET /api/proposals` - List proposals
- `POST /api/proposals` - Create proposal
- `PUT /api/proposals/{id}/accept` - Accept proposal (admin)
- `PUT /api/proposals/{id}/reject` - Reject proposal (admin)

### Session Endpoints
- `GET /api/sessions` - List sessions
- `POST /api/sessions/{id}/register` - Register for session
- `DELETE /api/sessions/{id}/register` - Cancel registration

See Swagger at `http://localhost:8080/swagger-ui.html` (development only, disabled in prod)

## Rollback Plan

If issues occur in production:

1. **Stop the application**
   ```bash
   sudo systemctl stop conference-system
   ```

2. **Revert to previous version**
   ```bash
   cp /opt/conference-system/backup/management-system-0.0.0-SNAPSHOT.jar \
      /opt/conference-system/management-system-0.0.1-SNAPSHOT.jar
   ```

3. **Check database integrity**
   ```bash
   psql -U app_user -d conference_db -c "SELECT COUNT(*) FROM users"
   ```

4. **Restart application**
   ```bash
   sudo systemctl start conference-system
   ```

5. **Monitor logs**
   ```bash
   sudo journalctl -u conference-system -f
   ```

---

**For detailed security information, see SECURITY_CHECKLIST.md**

**For all security fixes implemented, see SECURITY_FIXES_REPORT.md**

---
**Deployment Status**: Ready for Production ✅
**Last Updated**: 2024
