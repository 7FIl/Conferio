# 🎯 API Testing Dashboard - Quick Start Guide

## What You Get

A professional, interactive API testing interface at **http://localhost:8080** with:

```
✅ Modern, beautiful UI with dark gradient background
✅ Login panel (top-right corner) for authentication testing  
✅ GET/POST filter buttons to view specific endpoint types
✅ 24+ pre-configured API endpoints ready to test
✅ Click any endpoint to open a testing panel
✅ Send real API requests and see responses
✅ Role-based access control with error messages
✅ Authentication state management
```

## Quick Start (3 Steps)

### Step 1: Build
```bash
cd c:\Users\ADMIN\Desktop\P_Code\Java\management-system
.\mvnw.cmd package -DskipTests
```

### Step 2: Run
```bash
java -jar target\management-system-0.0.1-SNAPSHOT.jar
```
Or simply double-click: `start-app.bat`

### Step 3: Open Browser
```
http://localhost:8080
```

## Features Overview

### 🔐 Login System (Top-Right)
- Click "Login" button
- Enter email and password
- Your name and role display after login
- Click "Logout" to disconnect
- Perfect for testing role-restricted APIs!

### 📊 Sidebar Filters
```
[All] [GET] [POST]
```
- **All**: Show all 24+ endpoints
- **GET**: Show only GET endpoints
- **POST**: Show only POST endpoints

### 🔌 API Cards
Each endpoint shows:
- HTTP method (GET = Blue, POST = Green)
- Full path (e.g., `/api/users`)
- Description (what it does)
- 🔐 Auth required badge
- 🏆 Role required badge (ADMIN, COORDINATOR, etc.)

### 💾 Testing Modal
When you click an endpoint:
1. View the endpoint details
2. Modify JSON request body (if POST)
3. Click "Send Request"
4. See response with status code
5. If you lack permissions, get a helpful error message

## Example Testing Scenarios

### Test 1️⃣: Public API (No Login Needed)
```
1. Go to http://localhost:8080
2. Click filter "GET"
3. Click "Get all sessions"
4. Click "Send Request"
5. ✅ See response!
```

### Test 2️⃣: Authenticated API
```
1. Click "Login" (top-right)
2. Enter credentials
3. Now try "Get my registrations" 
4. ✅ Works because you're logged in!
```

### Test 3️⃣: Admin-Only API
```
1. Click "Login" as regular user
2. Click "Get all users"
3. ❌ Error: "Requires ADMIN role"
4. Logout, login as admin
5. ✅ Now it works!
```

### Test 4️⃣: Create/Submit API
```
1. Login as any user
2. Click "Submit proposal" (POST)
3. Edit JSON body with your data
4. Click "Send Request"
5. ✅ See new proposal created!
```

## API Endpoints Included

### 🔑 Auth (No login needed)
- Register new account
- Login
- Logout

### 👥 Users (Admin only)
- Get all users
- Get specific user
- Update user role
- Delete user

### 📅 Sessions (Public/Auth)
- Create session (Coordinator)
- List all sessions
- Get upcoming sessions
- Get session details
- Get my sessions
- Update session

### 💡 Proposals (Auth)
- Submit proposal
- Get all proposals (Coordinator)
- Get my proposals
- Filter by status
- Review proposal
- Delete proposal

### ⭐ Feedback (Auth)
- Submit feedback
- Get session feedback
- Get average rating
- Get my feedback
- Delete feedback

### 📝 Registrations (Auth)
- Register for session
- Get my registrations
- Get session registrations
- Cancel registration

## Key Features

### 🎨 Beautiful Design
- Modern gradient UI
- Color-coded endpoints
- Responsive layout
- Easy to use

### 🔐 Security
- JWT authentication
- Role-based access control
- Error messages for blocked requests
- Secure token storage

### ⚡ Interactive
- Live API testing
- Real-time responses
- JSON formatting
- Status code display

### 📚 Self-Documenting
- Endpoint descriptions
- Example request bodies
- Parameter information
- Required role badges

## File Structure

```
management-system/
├── start-app.bat                          (← Click to run!)
├── API_TESTING_DASHBOARD_GUIDE.md        (← Full guide)
├── pom.xml
├── src/
│   └── main/
│       ├── java/.../ApiTestingController.java  (NEW)
│       ├── resources/
│       │   ├── application.properties    (UPDATED)
│       │   └── templates/
│       │       └── api-testing-dashboard.html  (NEW - 600+ lines)
│       └── java/.../config/SecurityConfig.java (UPDATED)
└── target/
    └── management-system-0.0.1-SNAPSHOT.jar
```

## URLs

| Resource | URL |
|----------|-----|
| **API Testing Dashboard** | http://localhost:8080 |
| **Swagger UI (OpenAPI)** | http://localhost:8080/swagger-ui |
| **API Docs (JSON)** | http://localhost:8080/v3/api-docs |
| **Health Check** | http://localhost:8080/actuator/health |

## Troubleshooting

| Problem | Solution |
|---------|----------|
| Port 8080 in use | Change `server.port` in `application.properties` |
| DB connection error | Ensure PostgreSQL is running on `localhost:5432` |
| Template not found | Clear browser cache (Ctrl+Shift+Delete) |
| Can't login | Check database has `conference_db` created |

## That's It! 🎉

You now have a professional API testing dashboard that:
- ✅ Runs on the root path (`http://localhost:8080`)
- ✅ Shows all your APIs in an organized, searchable interface
- ✅ Supports login for authentication testing
- ✅ Shows role-based errors for blocked requests
- ✅ Has GET/POST filtering buttons
- ✅ Lets you test APIs directly with request/response display

**Start testing your APIs now!** 🚀

---
Need more details? See `API_TESTING_DASHBOARD_GUIDE.md` for comprehensive documentation.
