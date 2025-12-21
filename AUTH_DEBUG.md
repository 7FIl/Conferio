# Auth Debugging Checklist

## Backend (Java Spring Boot)

### 1. Database Password Hash Verification
```bash
# Check if hash is correct in database
psql -U postgres -d conference_db -c "SELECT username, password FROM users LIMIT 1;"
```
Expected: Password hash starting with `$2a$10$slYQmyNdGzin7olVN3p5be4DlH...`

### 2. Verify User Exists
```bash
psql -U postgres -d conference_db -c "SELECT id, username, email, role FROM users WHERE username='admin';"
```

### 3. Check Backend Logs
- Look for: `AuthService register/login` methods called
- Look for: `BadCredentialsException` if password mismatch
- Look for: CORS errors if frontend origin not allowed

### 4. CORS Configuration
Check SecurityConfig.java has:
```java
configuration.setAllowedOrigins(List.of("http://localhost:3000"));
configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
configuration.setAllowCredentials(true);
```

### 5. Test Backend API Directly
```bash
# Test login with curl (Windows PowerShell):
$payload = @{username="admin"; password="password123"} | ConvertTo-Json
Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $payload

# Test register with curl:
$payload = @{username="testuser"; email="test@test.com"; fullName="Test User"; password="password123"} | ConvertTo-Json
Invoke-WebRequest -Uri "http://localhost:8080/api/auth/register" -Method POST -ContentType "application/json" -Body $payload
```

## Frontend (React)

### 1. Check Console Logs (F12 Browser DevTools)
- Should show: `API: Calling POST /api/auth/login with: {username: "admin"}`
- Should show: `API: Login response: {token: "...", username: "admin", role: "ADMIN"}`
- Should show: `AuthContext: Login successful, token: ...`

### 2. Check Network Tab (F12)
- Login request should show Status 200
- Response should contain `token` field
- Check Response Headers for CORS (should have `Access-Control-Allow-Origin: http://localhost:3000`)

### 3. Check Local Storage (F12 Console)
```javascript
localStorage.getItem('token')
localStorage.getItem('user')
```
Both should have values after successful login

### 4. Check HTTP Client Configuration
File: `src/api/http.js`
- Should have Authorization interceptor
- Should set `Authorization: Bearer <token>` header

## Typical Issues & Solutions

### Issue: "Invalid username or password"
**Causes:**
1. BCrypt hash in database is invalid
2. Database not updated after password hash change
3. Custom password salt not matching

**Solution:**
- Drop and recreate database: `psql -U postgres -c "DROP DATABASE conference_db;" && psql -U postgres -d conference_db -f database/init.sql"`
- Rebuild backend JAR
- Restart backend

### Issue: "Registration failed" or "Email/Username already exists"
**Causes:**
1. Missing `fullName` field in frontend request
2. Duplicate username/email in database
3. Validation errors not shown

**Solution:**
- Ensure Signup form has `fullName` field
- Clear database and test with new account
- Check browser console for detailed error message

### Issue: CORS Error in Browser Console
**Error:** `Access to XMLHttpRequest at 'http://localhost:8080/api/auth/login' from origin 'http://localhost:3000' has been blocked by CORS policy`

**Causes:**
1. Backend CORS not configured
2. Origin mismatch (e.g., `localhost:3001` instead of `3000`)

**Solution:**
- Verify SecurityConfig CORS allows `http://localhost:3000`
- Restart backend after CORS change
- Check frontend `.env.development` has correct `VITE_API_BASE_URL`

### Issue: Token not persisting after login
**Causes:**
1. AuthContext not saving to localStorage
2. Browser localStorage disabled
3. Token not returned from API

**Solution:**
- Check Response body contains `token` field
- Clear localStorage and try again
- Check browser DevTools > Application > Storage > Local Storage

## Quick Restart

```bash
# Terminal 1 - Backend
cd C:\Users\ADMIN\Desktop\P_Code\Java\management-system
.\mvnw.cmd clean package -DskipTests
java -jar target/management-system-0.0.1-SNAPSHOT.jar

# Terminal 2 - Frontend
cd C:\Users\ADMIN\Desktop\P_Code\Java\management-system\conferio-ui
npm run dev
```

Then:
1. Open http://localhost:3000
2. Open DevTools (F12)
3. Go to Console tab
4. Try login with admin/password123
5. Watch console logs for request/response
6. Check Network tab for HTTP 200
7. Check Local Storage for token

## Test Accounts (After Database Reset)
- admin / password123
- coordinator1 / password123
- speaker1 / password123
- participant1 / password123

All use role-based access (admin, coordinator, user)
