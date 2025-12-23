# How to Add More Endpoints to the Dashboard

The API Testing Dashboard is fully customizable. Follow this guide to add your own endpoints.

## Location of Endpoint Configuration

The endpoints are defined in the JavaScript within the HTML file:
```
src/main/resources/templates/api-testing-dashboard.html
```

Look for the `const endpoints = [` section (around line 320 in the file).

## Endpoint Object Structure

Each endpoint is a JavaScript object with this structure:

```javascript
{
    method: 'GET',                    // 'GET', 'POST', 'PUT', 'DELETE'
    path: '/api/users',               // Full API path
    description: 'Get all users',     // What it does
    requiresAuth: true,               // true if needs JWT token
    role: 'ADMIN',                    // null or 'ADMIN', 'COORDINATOR', etc.
    requestBody: { /* optional */ },  // Example JSON body (for POST/PUT)
    queryParams: [ /* optional */ ],  // Query parameters if needed
    example: true                     // true if has request example
}
```

## Example 1: Simple GET Endpoint (No Auth)

```javascript
{
    method: 'GET',
    path: '/api/sessions',
    description: 'Get all conference sessions',
    requiresAuth: false,
    role: null,
    example: false
}
```

## Example 2: POST Endpoint with Request Body

```javascript
{
    method: 'POST',
    path: '/api/sessions',
    description: 'Create a new conference session',
    requiresAuth: true,
    role: 'COORDINATOR',
    requestBody: {
        title: 'Web Development Best Practices',
        description: 'Learn modern web development',
        scheduledDate: '2024-03-15',
        startTime: '10:00',
        endTime: '12:00',
        location: 'Hall A'
    },
    example: true
}
```

## Example 3: Admin-Only Endpoint

```javascript
{
    method: 'DELETE',
    path: '/api/users/{id}',
    description: 'Delete a user from the system',
    requiresAuth: true,
    role: 'ADMIN',
    example: false
}
```

## Step-by-Step: Add Your Endpoint

### Step 1: Open the HTML File
Edit: `src/main/resources/templates/api-testing-dashboard.html`

### Step 2: Find the Endpoints Array
Search for: `const endpoints = [`

### Step 3: Add Your Endpoint
At the end of the array (before the closing `]`), add a comma and your endpoint:

```javascript
        {
            method: 'POST',
            path: '/api/your-endpoint',
            description: 'What your endpoint does',
            requiresAuth: true,
            role: 'ADMIN',
            requestBody: {
                field1: 'example value',
                field2: 'another value'
            },
            example: true
        }
```

### Step 4: Rebuild and Test

```bash
cd c:\Users\ADMIN\Desktop\P_Code\Java\management-system
.\mvnw.cmd package -DskipTests
java -jar target\management-system-0.0.1-SNAPSHOT.jar
```

Then refresh: `http://localhost:8080`

## Field Explanations

### `method`
- **Values**: `'GET'`, `'POST'`, `'PUT'`, `'DELETE'`, `'PATCH'`
- Determines the HTTP method
- Colors: GET (blue), POST (green), others (default)

### `path`
- Full API endpoint path
- Examples: `/api/users`, `/api/users/{id}/role`
- Can include path parameters like `{id}`, `{sessionId}`

### `description`
- User-friendly description of what the endpoint does
- Shown in the endpoint card
- Keep it concise but informative

### `requiresAuth`
- `true` = User must be logged in to test
- `false` = Public endpoint, no login needed
- Shows 🔐 badge if true

### `role`
- `null` = Any authenticated user can access
- `'ADMIN'` = Only admin role
- `'COORDINATOR'` = Only coordinator role
- `'ADMIN'` or other = Shows role badge
- Displays error if user doesn't have required role

### `requestBody`
- Object with example JSON for POST/PUT requests
- Optional (leave out for GET/DELETE)
- User can modify this in the modal before sending
- Use realistic example values

### `queryParams`
- Array of query parameters
- Optional (for GET endpoints with filters)
- Structure: 
```javascript
queryParams: [
    { name: 'id', type: 'number', description: 'User ID' },
    { name: 'status', type: 'text', description: 'Filter by status' }
]
```

### `example`
- `true` = Has example request body
- `false` = No example needed
- Shows 📝 badge if true

## Complete Examples

### Example A: Get Endpoint (No Auth)
```javascript
{
    method: 'GET',
    path: '/api/sessions/upcoming',
    description: 'Get upcoming conference sessions',
    requiresAuth: false,
    role: null,
    example: false
}
```

### Example B: Create Endpoint (Auth Required)
```javascript
{
    method: 'POST',
    path: '/api/feedback',
    description: 'Submit feedback and rating for a session',
    requiresAuth: true,
    role: null,
    requestBody: {
        sessionId: 1,
        rating: 5,
        comment: 'Excellent session!'
    },
    example: true
}
```

### Example C: Admin Update (Admin Only)
```javascript
{
    method: 'PUT',
    path: '/api/users/{id}/role',
    description: 'Update user role (Admin only)',
    requiresAuth: true,
    role: 'ADMIN',
    requestBody: {
        role: 'COORDINATOR'
    },
    example: true
}
```

### Example D: Delete with Query Params
```javascript
{
    method: 'DELETE',
    path: '/api/sessions/{id}',
    description: 'Delete a session',
    requiresAuth: true,
    role: 'COORDINATOR',
    queryParams: [
        { name: 'id', type: 'number', description: 'Session ID' }
    ],
    example: false
}
```

## Tips & Best Practices

### ✅ DO:
- Use descriptive, clear descriptions
- Include realistic example request bodies
- Set `requiresAuth` correctly for security testing
- Set `role` for role-based endpoints
- Keep example values realistic

### ❌ DON'T:
- Leave out required fields (method, path, description)
- Use incorrect HTTP methods
- Forget the comma between endpoints
- Use invalid role names

## Verify Your Changes

1. **Check Syntax**: Ensure valid JSON syntax in `requestBody`
2. **Rebuild**: Run Maven package command
3. **Clear Cache**: Press Ctrl+Shift+Delete in browser
4. **Test**: Open `http://localhost:8080` and look for your endpoint

## Common Mistakes & Fixes

### Mistake 1: Forgot Comma Between Endpoints
```javascript
// ❌ WRONG
{method: 'GET', ...}
{method: 'POST', ...}  // ← Missing comma above!

// ✅ CORRECT
{method: 'GET', ...},
{method: 'POST', ...}
```

### Mistake 2: Invalid JSON in requestBody
```javascript
// ❌ WRONG - Single quotes
requestBody: {
    name: 'John'  // ← Should be double quotes
}

// ✅ CORRECT - Double quotes
requestBody: {
    "name": "John"
}
```

### Mistake 3: Typo in Role
```javascript
// ❌ WRONG
role: 'admin'  // ← Lowercase, won't match

// ✅ CORRECT
role: 'ADMIN'  // ← Uppercase
```

## Finding All Current Endpoints

Currently in the dashboard:
- **Auth**: 3 endpoints (register, login, logout)
- **Users**: 4 endpoints (list, get, update role, delete)
- **Sessions**: 6 endpoints (create, list, upcoming, get, my, update)
- **Proposals**: 6 endpoints (create, list, my, by status, review, delete)
- **Feedback**: 5 endpoints (create, get, average, my, delete)
- **Registrations**: 4 endpoints (register, my, session, cancel)

**Total: 28+ endpoints** - all categorized and ready to test!

## Need More Help?

Refer to the main guide: `API_TESTING_DASHBOARD_GUIDE.md`

---

Your API Testing Dashboard is now fully customizable! 🎨
