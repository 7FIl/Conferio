# API Testing Guide - Conference Management System

## Setup

1. Base URL: `http://localhost:8080`
2. Setelah login, simpan JWT token
3. Tambahkan header di setiap request (kecuali auth):
   ```
   Authorization: Bearer {your-jwt-token}
   ```

## 1. Authentication

### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "fullName": "Test User"
}
```

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

**Response:** Simpan `token` dari response untuk request berikutnya.

---

## 2. Proposals

### Submit Proposal (USER)
```http
POST /api/proposals
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Introduction to Microservices",
  "description": "A comprehensive guide to building and deploying microservices architecture"
}
```

### Get My Proposals
```http
GET /api/proposals/my
Authorization: Bearer {token}
```

### Get All Proposals (COORDINATOR/ADMIN)
```http
GET /api/proposals
Authorization: Bearer {coordinator-token}
```

### Get Proposals by Status
```http
GET /api/proposals/status/PENDING
Authorization: Bearer {coordinator-token}
```

### Review Proposal (COORDINATOR/ADMIN)
```http
POST /api/proposals/{proposalId}/review
Authorization: Bearer {coordinator-token}
Content-Type: application/json

{
  "status": "ACCEPTED"
}
```

**For Rejection:**
```json
{
  "status": "REJECTED",
  "rejectionReason": "Topic already covered in another session"
}
```

### Delete Proposal
```http
DELETE /api/proposals/{proposalId}
Authorization: Bearer {token}
```

---

## 3. Sessions

### Create Session (COORDINATOR/ADMIN)
```http
POST /api/sessions
Authorization: Bearer {coordinator-token}
Content-Type: application/json

{
  "proposalId": 1,
  "room": "Conference Room A",
  "sessionTime": "2025-12-25T10:00:00",
  "durationMinutes": 90,
  "maxParticipants": 50
}
```

### Get All Sessions
```http
GET /api/sessions
Authorization: Bearer {token}
```

### Get Upcoming Sessions
```http
GET /api/sessions/upcoming
Authorization: Bearer {token}
```

### Get Session by ID
```http
GET /api/sessions/{sessionId}
Authorization: Bearer {token}
```

### Get My Sessions (as Speaker)
```http
GET /api/sessions/my
Authorization: Bearer {token}
```

### Update Session (COORDINATOR/ADMIN)
```http
PUT /api/sessions/{sessionId}
Authorization: Bearer {coordinator-token}
Content-Type: application/json

{
  "proposalId": 1,
  "room": "Conference Room B",
  "sessionTime": "2025-12-25T14:00:00",
  "durationMinutes": 120,
  "maxParticipants": 100
}
```

### Delete Session (COORDINATOR/ADMIN)
```http
DELETE /api/sessions/{sessionId}
Authorization: Bearer {coordinator-token}
```

---

## 4. Registrations

### Register for Session
```http
POST /api/registrations/session/{sessionId}
Authorization: Bearer {token}
```

### Get My Registrations
```http
GET /api/registrations/my
Authorization: Bearer {token}
```

### Get Session Registrations (COORDINATOR/ADMIN)
```http
GET /api/registrations/session/{sessionId}
Authorization: Bearer {coordinator-token}
```

### Cancel Registration
```http
DELETE /api/registrations/{registrationId}
Authorization: Bearer {token}
```

---

## 5. Feedback

### Submit Feedback
```http
POST /api/feedback
Authorization: Bearer {token}
Content-Type: application/json

{
  "sessionId": 1,
  "rating": 5,
  "comment": "Excellent session! Very informative and well-presented."
}
```

### Get Session Feedback
```http
GET /api/feedback/session/{sessionId}
Authorization: Bearer {token}
```

### Get Session Average Rating
```http
GET /api/feedback/session/{sessionId}/average
Authorization: Bearer {token}
```

### Get My Feedback
```http
GET /api/feedback/my
Authorization: Bearer {token}
```

### Delete Feedback (COORDINATOR/ADMIN)
```http
DELETE /api/feedback/{feedbackId}
Authorization: Bearer {coordinator-token}
```

---

## Testing Workflow

### Scenario 1: Complete User Journey

1. **Register as User**
   ```
   POST /api/auth/register
   ```

2. **Login and get token**
   ```
   POST /api/auth/login
   ```

3. **Submit Proposal**
   ```
   POST /api/proposals
   ```

4. **Check Proposal Status**
   ```
   GET /api/proposals/my
   ```

5. **Register Coordinator** (untuk testing review)

6. **Coordinator Login**

7. **Review Proposal**
   ```
   POST /api/proposals/{id}/review
   ```

8. **Create Session from Accepted Proposal**
   ```
   POST /api/sessions
   ```

9. **Register for Session**
   ```
   POST /api/registrations/session/{sessionId}
   ```

10. **Submit Feedback**
    ```
    POST /api/feedback
    ```

### Scenario 2: Time Conflict Testing

1. Create Session at 10:00-12:00
2. Try to register for another session at 11:00-13:00
3. Should get error: "You have another session at this time"

### Scenario 3: Session Full Testing

1. Create Session with maxParticipants = 1
2. Register first user (success)
3. Try to register second user (should fail)
4. Should get error: "Session is full"

---

## Error Responses

### 400 Bad Request
```json
{
  "timestamp": "2025-12-20T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Already registered for this session",
  "path": "/api/registrations/session/1"
}
```

### 401 Unauthorized
```json
{
  "timestamp": "2025-12-20T10:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid username or password",
  "path": "/api/auth/login"
}
```

### Validation Error
```json
{
  "timestamp": "2025-12-20T10:00:00",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "title": "Title is required",
    "description": "Description must be at least 20 characters"
  },
  "path": "/api/proposals"
}
```

---

## Sample Test Accounts

After running init.sql:

| Username | Password | Role | Email |
|----------|----------|------|-------|
| admin | password123 | ADMIN | admin@conference.com |
| coordinator1 | password123 | COORDINATOR | coordinator@conference.com |
| speaker1 | password123 | USER | speaker1@conference.com |
| speaker2 | password123 | USER | speaker2@conference.com |
| participant1 | password123 | USER | participant1@conference.com |

---

## Tips

1. Gunakan environment variables di Postman untuk menyimpan token
2. Buat collection dengan pre-request scripts untuk auto-add token
3. Test edge cases seperti invalid data, expired tokens, dll
4. Verify database state setelah setiap operation
5. Test concurrent requests untuk race conditions

---

## Swagger UI (Recommended)

Untuk testing yang lebih mudah, gunakan Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

Swagger UI provides:
- Interactive API documentation
- Try it out feature
- Auto-generated request examples
- Response schemas
- Authorization support
