# API Testing Dashboard Setup

## Overview
I've created a comprehensive **API Testing Dashboard** that runs on the root path of your Spring Boot application. This provides a professional, interactive interface for testing all your APIs without needing to use external tools like Postman.

## What's Been Done

### 1. **API Testing Dashboard** (Main Feature)
- **Location**: `http://localhost:8080/` (root path)
- **Features**:
  - ✅ Beautiful, modern UI with gradient background
  - ✅ Login/Registration panel in top-right corner
  - ✅ GET and POST filter buttons to view specific endpoint types
  - ✅ All available API endpoints listed with:
    - HTTP method (GET/POST)
    - Full endpoint path
    - Description
    - Authentication requirements
    - Required role badges
  - ✅ Click any endpoint to open a testing modal
  - ✅ Direct API request testing with JSON request body support
  - ✅ Real-time response display with status codes
  - ✅ Role-based access control with error messages
  - ✅ Authentication state management

### 2. **Security Configuration Updated**
- Updated `SecurityConfig.java` to allow access to the dashboard
- Root path (`/`) is publicly accessible
- Swagger UI resources remain accessible
- JWT authentication works seamlessly

### 3. **Application Properties Updated**
- Moved Swagger UI from root (`/`) to `/swagger-ui`
- Dashboard now runs on the root path
- Both are accessible side-by-side

### 4. **Included API Endpoints** (24+ endpoints documented)

#### Authentication APIs (No Auth Required)
- `POST /api/auth/register` - Create new user
- `POST /api/auth/login` - Login and get JWT token
- `POST /api/auth/logout` - Logout

#### User Management APIs (Admin Only)
- `GET /api/users` - List all users
- `GET /api/users/{id}` - Get specific user
- `PUT /api/users/{id}/role` - Update user role
- `DELETE /api/users/{id}` - Delete user

#### Session Management APIs
- `POST /api/sessions` - Create session (Coordinator/Admin)
- `GET /api/sessions` - List all sessions
- `GET /api/sessions/upcoming` - Get upcoming sessions
- `GET /api/sessions/{id}` - Get session details
- `GET /api/sessions/my` - Get current user's sessions
- `PUT /api/sessions/{id}` - Update session

#### Proposal Management APIs
- `POST /api/proposals` - Submit proposal
- `GET /api/proposals` - List proposals (Coordinator/Admin)
- `GET /api/proposals/my` - Get my proposals
- `GET /api/proposals/status/{status}` - Filter by status
- `POST /api/proposals/{id}/review` - Review proposal
- `DELETE /api/proposals/{id}` - Delete proposal

#### Feedback APIs
- `POST /api/feedback` - Submit feedback
- `GET /api/feedback/session/{sessionId}` - Get session feedback
- `GET /api/feedback/session/{sessionId}/average` - Get average rating
- `GET /api/feedback/my` - Get my feedback
- `DELETE /api/feedback/{id}` - Delete feedback (Coordinator/Admin)

#### Registration APIs
- `POST /api/registrations/session/{sessionId}` - Register for session
- `GET /api/registrations/my` - Get my registrations
- `GET /api/registrations/session/{sessionId}` - Get session registrations (Coordinator/Admin)
- `DELETE /api/registrations/{id}` - Cancel registration

## How to Run

### Method 1: Using the Batch Script (Windows)
```bash
cd c:\Users\ADMIN\Desktop\P_Code\Java\management-system
start-app.bat
```

### Method 2: Using Maven Directly
```bash
cd c:\Users\ADMIN\Desktop\P_Code\Java\management-system
.\mvnw.cmd spring-boot:run
```

### Method 3: Run the JAR Directly
```bash
cd c:\Users\ADMIN\Desktop\P_Code\Java\management-system\target
java -jar management-system-0.0.1-SNAPSHOT.jar
```

## Accessing the Dashboard

Once the application starts:

1. **API Testing Dashboard**: Open `http://localhost:8080/` in your browser
2. **Swagger UI**: Open `http://localhost:8080/swagger-ui` for OpenAPI documentation

## Using the Dashboard

### Step 1: Login (Optional)
- Click the "Login" button in the top-right corner
- Enter credentials:
  - **Example User**: `user@example.com` / `password`
  - **Example Admin**: `admin@example.com` / `adminpass`
- After login, your username and role appear in top-right
- Now you can test role-restricted endpoints

### Step 2: Filter Endpoints
- Use the sidebar buttons:
  - **All**: Show all endpoints
  - **GET**: Show only GET endpoints
  - **POST**: Show only POST endpoints

### Step 3: Test an Endpoint
1. Click on any endpoint card
2. For POST endpoints, you can modify the JSON request body
3. Click "Send Request"
4. View the response with status code and JSON body
5. If you lack permissions, you'll see a friendly error message

### Step 4: View Response
- Success responses: Green status badge
- Error responses: Red status badge
- Raw JSON response displayed below

## Dashboard Features

### 🔐 Role-Based Testing
- **Public Endpoints**: No authentication needed
- **Authenticated Endpoints**: Login first to test
- **Role-Restricted Endpoints**: Shows error if you don't have the required role
  - Example: Admin endpoints show: "❌ This endpoint requires ADMIN role"

### 📝 Request Examples
- Each endpoint has pre-filled JSON examples
- Modify as needed for your testing
- Press "Send Request" to submit

### 🎨 Modern UI Design
- Clean, professional interface
- Color-coded endpoints (Blue for GET, Green for POST)
- Role badges for visual identification
- Responsive design (works on desktop, tablet, mobile)

### 💾 Session Persistence
- Login credentials stored in browser localStorage
- Stay logged in between page refreshes
- Click "Logout" to clear session

## Files Created/Modified

### New Files:
- `src/main/java/com/conference/management_system/controller/ApiTestingController.java`
- `src/main/resources/templates/api-testing-dashboard.html`
- `start-app.bat`

### Modified Files:
- `src/main/java/com/conference/management_system/config/SecurityConfig.java`
- `src/main/resources/application.properties`

## Testing Common Scenarios

### Test 1: Public API (No Auth)
1. Don't login
2. Click on "Get all sessions" (`GET /api/sessions`)
3. Send request - should succeed

### Test 2: Authenticated API
1. Try "Get all proposals" without login - should show "Authentication required" error
2. Login with any credentials
3. Try again - if you're a COORDINATOR/ADMIN, it succeeds
4. If not, shows "You don't have COORDINATOR role"

### Test 3: Admin-Only API
1. Login with non-admin account
2. Click "Get all users" (`GET /api/users`)
3. See error: "This endpoint requires ADMIN role"
4. Logout, login as admin
5. Now it works!

### Test 4: Create & Submit
1. Login (any account)
2. Click "Submit a new proposal" (`POST /api/proposals`)
3. Modify JSON body with your data
4. Send request
5. View response with created proposal details

## Troubleshooting

### Port Already in Use
If port 8080 is in use:
- Edit `application.properties` and change `server.port=8080` to another port
- Rebuild with `.\mvnw.cmd package -DskipTests`

### Database Connection Error
- Ensure PostgreSQL is running on `localhost:5432`
- Database name: `conference_db`
- Username: `postgres`
- Password: `123`
- Update in `application.properties` if needed

### Templates Not Found
- Ensure file is at: `src/main/resources/templates/api-testing-dashboard.html`
- Rebuild the project
- Clear browser cache (Ctrl+Shift+Delete)

## Next Steps

1. ✅ Start the application
2. ✅ Visit `http://localhost:8080`
3. ✅ Create a test account or use existing credentials
4. ✅ Test different endpoints
5. ✅ Share the dashboard URL with your team for collaborative testing

## Notes

- The dashboard uses **localStorage** to store authentication tokens (safe for local testing)
- For production, implement proper token management
- All API endpoints can be tested directly from the browser
- No external tools like Postman are needed
- Real-time error messages help understand what's wrong

Enjoy your new API Testing Dashboard! 🎉
