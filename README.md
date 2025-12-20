# Platform Manajemen Konferensi

Platform manajemen konferensi dinamis yang merevolusi cara pengelolaan sesi, keterlibatan peserta, dan pengawasan administratif dengan Spring Boot.

## 🚀 Fitur Utama

### 1. Autentikasi & Otorisasi (JWT)
- **Register & Login**: Registrasi user baru dan login dengan JWT token
- **Role-Based Access Control**: 
  - `USER`: Dapat submit proposal, daftar sesi, berikan feedback
  - `COORDINATOR`: Dapat review proposal, kelola sesi
  - `ADMIN`: Full access ke semua fitur

### 2. Manajemen Proposal
- Submit proposal sesi oleh pembicara
- Status: PENDING → ACCEPTED/REJECTED
- Auto-create session dari proposal yang diterima
- Review dan moderasi oleh coordinator

### 3. Manajemen Sesi
- Buat dan kelola sesi konferensi
- Validasi bentrok jadwal otomatis
- Track jumlah peserta real-time
- Filter sesi upcoming

### 4. Pendaftaran Peserta
- Daftar ke sesi dengan validasi kapasitas
- Cek bentrok jadwal peserta otomatis
- Cancel pendaftaran
- Unique constraint: 1 user = 1 sesi

### 5. Sistem Feedback
- Rating 1-5 dan komentar
- Hanya peserta terdaftar yang bisa beri feedback
- Hitung rata-rata rating per sesi
- Moderasi feedback oleh coordinator

## 🏗️ Arsitektur

```
com.conference.management_system
├── config/          # Security, Swagger configuration
├── controller/      # REST API endpoints
├── dto/             # Request/Response objects
├── entity/          # Database models (JPA)
├── repository/      # Data access layer
├── service/         # Business logic
├── security/        # JWT authentication & authorization
└── exception/       # Global exception handling
```

## 🗄️ Database Schema

### Tables:
- **users**: User data dengan role (USER/COORDINATOR/ADMIN)
- **proposals**: Proposal sesi dari pembicara
- **sessions**: Sesi konferensi terjadwal
- **registrations**: Pendaftaran peserta ke sesi
- **feedback**: Rating dan komentar sesi

### Key Relations:
- User → Proposals (1:N)
- User → Registrations (1:N)
- Session → Registrations (1:N)
- Session → Feedback (1:N)
- Proposal → Session (1:1)

## 🛠️ Tech Stack

- **Java 21** (LTS)
- **Spring Boot 4.0.1**
- **Spring Security** + JWT
- **Spring Data JPA**
- **PostgreSQL** (Production)
- **H2** (Testing)
- **Lombok**
- **Swagger/OpenAPI** (API Documentation)

## 📋 Prerequisites

1. **JDK 17 atau 21**
2. **Maven** (included with wrapper)
3. **PostgreSQL 12+**
4. **IDE**: IntelliJ IDEA / VS Code / Eclipse

## ⚙️ Setup & Installation

### 1. Clone Repository
```bash
git clone <repository-url>
cd management-system
```

### 2. Setup Database
```sql
CREATE DATABASE conference_db;
```

### 3. Configure Application
Edit `src/main/resources/application.properties`:
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/conference_db
spring.datasource.username=postgres
spring.datasource.password=your_password

# JWT Secret (ganti dengan secret key Anda)
jwt.secret=your-secret-key-must-be-at-least-256-bits-long-for-HS256-algorithm
```

### 4. Build & Run
```bash
# Build project
./mvnw clean install

# Run application
./mvnw spring-boot:run
```

Application akan berjalan di: `http://localhost:8080`

## 📚 API Documentation

Setelah aplikasi running, akses:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

## 🔐 API Endpoints

### Authentication
```
POST /api/auth/register   - Register user baru
POST /api/auth/login      - Login dan dapat JWT token
```

### Proposals
```
POST   /api/proposals              - Submit proposal (USER)
GET    /api/proposals/my           - Get proposals saya
GET    /api/proposals              - Get all proposals (COORDINATOR)
GET    /api/proposals/status/{status} - Filter by status
POST   /api/proposals/{id}/review  - Review proposal (COORDINATOR)
DELETE /api/proposals/{id}         - Delete proposal
```

### Sessions
```
POST   /api/sessions           - Create session (COORDINATOR)
GET    /api/sessions           - Get all sessions
GET    /api/sessions/upcoming  - Get upcoming sessions
GET    /api/sessions/{id}      - Get session by ID
GET    /api/sessions/my        - Get my sessions (as speaker)
PUT    /api/sessions/{id}      - Update session (COORDINATOR)
DELETE /api/sessions/{id}      - Delete session (COORDINATOR)
```

### Registrations
```
POST   /api/registrations/session/{sessionId}  - Register ke sesi
GET    /api/registrations/my                    - Get registrations saya
GET    /api/registrations/session/{sessionId}  - Get registrations per sesi (COORDINATOR)
DELETE /api/registrations/{id}                 - Cancel registration
```

### Feedback
```
POST   /api/feedback                     - Submit feedback
GET    /api/feedback/session/{sessionId} - Get feedback per sesi
GET    /api/feedback/session/{sessionId}/average - Get average rating
GET    /api/feedback/my                  - Get feedback saya
DELETE /api/feedback/{id}                - Delete feedback (COORDINATOR)
```

## 🔑 Authorization Header

Untuk endpoint yang memerlukan authentication, tambahkan header:
```
Authorization: Bearer {your-jwt-token}
```

## 📝 Business Logic Highlights

### Validasi Bentrok Sesi
- Sistem otomatis cek overlapping time slots
- Mencegah double booking untuk peserta
- Validasi di level service layer

### Workflow Proposal
1. User submit proposal → Status: PENDING
2. Coordinator review → Status: ACCEPTED/REJECTED
3. Jika ACCEPTED → Auto-create Session
4. Coordinator set waktu & ruangan

### Pendaftaran Sesi
- Check kapasitas max participants
- Check bentrok jadwal user
- Unique constraint (user_id, session_id)
- Auto increment current_participants

### Feedback System
- Hanya peserta registered yang bisa feedback
- 1 user = 1 feedback per sesi
- Rating 1-5 dengan komentar opsional
- Average rating calculation

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

## 📦 Build Production

```bash
# Build JAR
./mvnw clean package -DskipTests

# Run JAR
java -jar target/management-system-0.0.1-SNAPSHOT.jar
```

## 🐳 Docker (Optional)

```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/management-system-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
docker build -t conference-management .
docker run -p 8080:8080 conference-management
```

## 🔒 Security Features

1. **JWT Authentication**: Stateless token-based auth
2. **BCrypt Password**: Secure password hashing
3. **Role-Based Authorization**: Method-level security
4. **CORS Configuration**: Cross-origin resource sharing
5. **Input Validation**: Jakarta Validation annotations

## 📊 Database Indexes (Recommended)

```sql
CREATE INDEX idx_proposals_status ON proposals(status);
CREATE INDEX idx_proposals_user_id ON proposals(user_id);
CREATE INDEX idx_sessions_time ON sessions(session_time);
CREATE INDEX idx_sessions_speaker_id ON sessions(speaker_id);
CREATE INDEX idx_registrations_user_session ON registrations(user_id, session_id);
CREATE INDEX idx_feedback_session_id ON feedback(session_id);
```

## 🚨 Common Issues

### Issue: JWT Secret Too Short
```
Solution: Pastikan jwt.secret minimal 256 bits (32 characters)
```

### Issue: Database Connection Failed
```
Solution: Cek PostgreSQL running & credentials di application.properties
```

### Issue: Port 8080 Already in Use
```
Solution: Change port di application.properties
server.port=8081
```

## 📈 Future Enhancements

- [ ] Email notifications untuk proposal approval
- [ ] QR code untuk check-in peserta
- [ ] Live streaming integration
- [ ] File upload untuk presentation materials
- [ ] Analytics dashboard untuk coordinator
- [ ] Mobile app integration
- [ ] WebSocket untuk real-time updates

## 👥 Roles & Permissions Matrix

| Feature | USER | COORDINATOR | ADMIN |
|---------|------|-------------|-------|
| Register/Login | ✅ | ✅ | ✅ |
| Submit Proposal | ✅ | ✅ | ✅ |
| Review Proposal | ❌ | ✅ | ✅ |
| Create Session | ❌ | ✅ | ✅ |
| Register to Session | ✅ | ✅ | ✅ |
| Submit Feedback | ✅ | ✅ | ✅ |
| Delete Feedback | ❌ | ✅ | ✅ |
| View All Data | ❌ | ✅ | ✅ |

## 📞 Contact & Support

- **Developer**: Conference Team
- **Email**: info@conference.com
- **Documentation**: http://localhost:8080/swagger-ui.html

## 📄 License

MIT License - Free to use for educational purposes

---

**Built with ❤️ using Spring Boot & Clean Architecture principles**
