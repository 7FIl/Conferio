# Platform Manajemen Konferensi

Platform manajemen konferensi dinamis yang merevolusi cara pengelolaan sesi, keterlibatan peserta, dan pengawasan administratif dengan Spring Boot.

## Quick Start

### 1. Clone Repository
```bash
git clone <repo-url>
cd management-system
```

### 2. Setup Environment Variables
```bash
# Copy .env.sample ke .env
cp .env.sample .env

# Edit .env dengan nilai Anda:
# - JWT_SECRET: Generate dengan: openssl rand -base64 32
# - Database credentials: Setup PostgreSQL terlebih dahulu
```

### 3. Initialize Database
```bash
# Development (dengan sample data)
psql -U app_user -d conference_db -f database/init.sql

# Production (tanpa default users)
psql -U app_user -d conference_db -f database/init-prod.sql
```

### 4. Run Application
```bash
# Development
./mvnw spring-boot:run

# Production
./mvnw clean package
java -jar target/management-system-0.0.1-SNAPSHOT.jar
```

### 5. Access Application
```
Frontend: http://localhost:5173
Backend API: http://localhost:8080
API Docs: http://localhost:8080/swagger-ui.html
```

## Security (Keamanan)

Sistem ini telah diperkuat dengan 6 perbaikan keamanan kritis:

1. **JWT Secret Management** - Secret disimpan di environment variables
2. **Token Storage Security** - Token disimpan di httpOnly cookies, immune terhadap XSS
3. **Race Condition Prevention** - Pessimistic locking untuk mencegah overbooking
4. **Exception Handling Sanitization** - Error messages yang generic tanpa expose internals
5. **Rate Limiting** - Login endpoint dilindungi 5 attempts/minute per IP
6. **Production Database Security** - init-prod.sql tanpa default credentials

**Setup**: Copy `.env.sample` ke `.env` dan isi nilai Anda. Lihat `SECURITY_CHECKLIST.md` untuk production setup detail.

## Fitur Utama

### 1. Autentikasi & Otorisasi (JWT)
- Register & Login dengan JWT token
- Role-Based Access Control (USER, COORDINATOR, ADMIN)

### 2. Manajemen Proposal
- Submit proposal dari pembicara
- Status workflow: PENDING → ACCEPTED/REJECTED
- Auto-create session dari accepted proposal

### 3. Manajemen Sesi
- Buat dan kelola sesi konferensi
- Validasi bentrok jadwal otomatis
- Track jumlah peserta real-time

### 4. Pendaftaran Peserta
- Daftar ke sesi dengan validasi kapasitas
- Cek bentrok jadwal otomatis
- Unique constraint: 1 user = 1 sesi

### 5. Sistem Feedback
- Rating 1-5 dan komentar
- Hanya peserta registered yang bisa feedback
- Average rating calculation

## Tech Stack

- **Backend**: Java 21, Spring Boot 4.0.1, Spring Security + JWT
- **Database**: PostgreSQL (Production), H2 (Testing)
- **Frontend**: React 18.3.1, Vite, TanStack Query, Tailwind CSS
- **Build**: Maven with Spring Boot wrapper
- **Documentation**: OpenAPI/Swagger

## Project Structure

```
management-system/
├── .env.sample                      # Environment variables template
├── SECURITY_CHECKLIST.md            # Security deployment guide
├── DEPLOYMENT_GUIDE.md              # Complete deployment instructions
├── database/
│   ├── init.sql                     # Development data (with samples)
│   ├── init-prod.sql                # Production schema (no defaults)
│   └── reset.sql                    # Reset script
├── src/main/java/
│   └── com/conference/management_system/
│       ├── config/                  # Security, Web configuration
│       ├── controller/              # REST API endpoints
│       ├── dto/                     # Data Transfer Objects
│       ├── entity/                  # JPA Entities
│       ├── repository/              # Data Access Layer
│       ├── service/                 # Business Logic
│       ├── security/                # JWT & Rate Limiting
│       └── exception/               # Exception Handling
├── src/main/resources/
│   ├── application.properties       # Default (dev) configuration
│   ├── application-prod.properties  # Production configuration
│   └── static/, templates/          # Static resources
├── conferio-ui/                     # React Frontend
│   ├── src/
│   │   ├── pages/                  # Page components
│   │   ├── components/             # Reusable components
│   │   ├── context/                # Auth context
│   │   ├── hooks/                  # Custom hooks
│   │   └── App.jsx                 # Main app component
│   └── vite.config.js             # Vite configuration
├── pom.xml                          # Maven dependencies
└── mvnw, mvnw.cmd                  # Maven wrapper
```

## Environment Variables (.env)

Required variables (copy from `.env.sample`):

```bash
# JWT Configuration
JWT_SECRET=your-secure-jwt-secret-here-change-this

# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/conference_db
SPRING_DATASOURCE_USERNAME=app_user
SPRING_DATASOURCE_PASSWORD=your-secure-database-password-here

# Server Configuration
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev  # or 'prod' for production
```

Generate secure values:
```bash
# Generate JWT_SECRET
openssl rand -base64 32

# Generate database password
openssl rand -base64 32
```

**Note:** The project has two separate configuration files:
- **Root `.env.sample`** - Backend (Spring Boot) configuration for database and security
- **`conferio-ui/.env.development`** - Frontend (React/Vite) configuration pointing to backend API
  - Already pre-configured to connect to `http://localhost:8080`
  - No changes needed unless you change the backend port

## API Endpoints

### Authentication
```
POST   /api/auth/register          # Register user baru
POST   /api/auth/login             # Login
POST   /api/auth/logout            # Logout
```

### Proposals
```
GET    /api/proposals              # Get all proposals
POST   /api/proposals              # Submit proposal (USER)
GET    /api/proposals/my           # Get my proposals
PUT    /api/proposals/{id}/accept  # Accept proposal (ADMIN)
PUT    /api/proposals/{id}/reject  # Reject proposal (ADMIN)
```

### Sessions
```
GET    /api/sessions               # Get all sessions
GET    /api/sessions/upcoming      # Get upcoming sessions
POST   /api/sessions               # Create session (COORDINATOR)
POST   /api/sessions/{id}/register # Register to session
DELETE /api/sessions/{id}/register # Cancel registration
```

### Feedback
```
POST   /api/feedback               # Submit feedback
GET    /api/feedback/session/{id}  # Get feedback for session
```

### Admin
```
GET    /api/admin/users            # List all users
PUT    /api/admin/users/{id}/role  # Change user role
DELETE /api/admin/users/{id}       # Delete user
```

## Testing

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report

# Build production JAR
./mvnw clean package -DskipTests
```

## Docker

```bash
# Build Docker image
docker build -t conference-system:latest .

# Run container
docker run -d \
  -p 8080:8080 \
  -p 5173:5173 \
  -e JWT_SECRET="your-secret" \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://db:5432/conference_db" \
  -e SPRING_DATASOURCE_USERNAME="app_user" \
  -e SPRING_DATASOURCE_PASSWORD="password" \
  conference-system:latest
```

## Documentation

- **[SECURITY_CHECKLIST.md](SECURITY_CHECKLIST.md)** - Security setup & verification
- **[DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)** - Complete deployment instructions
- **[.env.sample](.env.sample)** - Environment variables template

## Troubleshooting

### Port already in use
```bash
# Change port in .env
SERVER_PORT=8081
```

### Database connection failed
```bash
# Verify PostgreSQL is running
psql -U postgres -h localhost

# Check .env credentials
```

### JWT secret too short
```bash
# Generate new 32-character secret
openssl rand -base64 32
```

## User Roles

| Role | Permissions |
|------|-------------|
| **USER** | Register, submit proposal, register for session, feedback |
| **COORDINATOR** | Review proposal, create session, manage sessions |
| **ADMIN** | Full access, user management |

## Contact

- **Documentation**: See `.md` files
- **API Docs**: `http://localhost:8080/swagger-ui.html`
- **Issues**: Create issue in repository

## License

MIT License - Free for educational use

---

**Dibangun dengan penuh dedikasi menggunakan Spring Boot & Clean Architecture**

Last Updated: December 2025
Security Status: PRODUCTION READY
