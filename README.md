# Conference Management System

Production-ready Spring Boot backend for running conferences: manage proposals, sessions, registrations, feedback, and administrative workflows with consistent error handling and hardened security defaults.

## Requirements

- Java 21 (Temurin or Corretto recommended)
- Maven 3.9+
- PostgreSQL 14+ (local or managed service)
- OpenSSL (optional, for generating secrets)

## Quick Start

1. Clone and enter the repository
  ```bash
  git clone <repo-url>
  cd management-system
  ```
2. Copy environment template and set secrets
  ```bash
  copy .env.sample .env         # Windows
  # or
  cp .env.sample .env           # macOS/Linux
  ```
  Update `.env` with a strong `JWT_SECRET` and real database credentials (`openssl rand -base64 32`).
3. Provision the database
  ```bash
  psql -U postgres -h localhost -f database/init.sql        # Dev data
  psql -U postgres -h localhost -f database/init-prod.sql   # Prod baseline
  ```
4. Run the application
  ```bash
  .\mvnw.cmd spring-boot:run     # Windows
  ./mvnw spring-boot:run         # macOS/Linux
  ```
5. Explore the API at `http://localhost:8080/swagger-ui`.

## Configuration

Key environment variables (see `.env.sample` for full list):

| Variable | Purpose | Default |
| --- | --- | --- |
| `JWT_SECRET` | HMAC key for JWT signing | `dev-secret-key-change-in-production-immediately` |
| `SPRING_DATASOURCE_URL` | JDBC connection string | `jdbc:postgresql://localhost:5432/conference_db` |
| `SPRING_DATASOURCE_USERNAME` | Database user | `app_user` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | _none_ |
| `SERVER_PORT` | HTTP port | `8080` |
| `SPRING_PROFILES_ACTIVE` | Active profile (`dev` or `prod`) | `dev` |

- `application.properties` is tuned for local development.
- `application-prod.properties` enforces secure cookies, disables Swagger, and expects secrets via environment variables. Activate with `SPRING_PROFILES_ACTIVE=prod`.
- Database scripts live in `database/` (`init.sql`, `init-prod.sql`, `reset.sql`).

## Security Hardening

- JWT-based stateless authentication with `JwtAuthenticationFilter` and BCrypt password hashing.
- Role-based authorization (`USER`, `COORDINATOR`, `ADMIN`) enforced via `SecurityConfig`.
- Login endpoint protected by `RateLimitingInterceptor` (5 attempts/minute/IP using Bucket4j).
- Centralized exception translation via `ApiException` and `GlobalExceptionHandler`; no internal details leak to clients.
- Production profile forces HTTPS cookies (`SameSite=strict`, `httpOnly=true`) and hides Swagger UI.
- Actuator restricted to `health` and `info` in production.

## Running & Testing

```bash
./mvnw verify                     # Compile, run unit tests
./mvnw clean package -DskipTests  # Build runnable JAR
java -jar target/management-system-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

- H2 is used for tests; PostgreSQL is required at runtime.
- Add `-Dspring-boot.run.profiles=prod` to run the wrapper with production settings.

## Observability & Operations

- API docs: `/swagger-ui` (disabled in prod) and `/v3/api-docs`.
- Health checks: `/actuator/health`, `/actuator/info` (exposed via Spring Boot Actuator).
- Structured error payloads returned on every failure (`timestamp`, `status`, `message`, `path`).
- Application logs default to INFO for business packages; override via `logging.level.*` properties.

## Feature Overview

- **Proposals**: submit, review (accept/reject), audit reviewer metadata.
- **Sessions**: schedule accepted proposals with conflict detection and capacity limits.
- **Registrations**: pessimistic locking avoids overbooking; conflict checks prevent schedule overlaps.
- **Feedback**: authenticated attendees provide ratings/comments; average rating endpoint included.
- **Administration**: manage users and roles with safeguards against removing the last administrator.

## Project Structure

```
src/main/java/com/conference/management_system
├── config/        # Security, Swagger, and web interceptors
├── controller/    # REST endpoints
├── dto/           # Request/response payloads
├── entity/        # JPA entities
├── exception/     # ApiException + global handler
├── repository/    # Spring Data repositories
├── security/      # JWT utilities and rate limiting
└── service/       # Transactional domain logic

src/main/resources
├── application.properties
├── application-prod.properties
└── static/swagger-custom.css
```

## Additional Resources

- Deployment checklist: [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)
- Troubleshooting and FAQs: [HELP.md](HELP.md)
- API testing dashboard notes: [API_TESTING_DASHBOARD_GUIDE.md](API_TESTING_DASHBOARD_GUIDE.md)

## Production Checklist

- [ ] Set strong values in `.env` or platform secrets store (JWT, database credentials).
- [ ] Run `database/init-prod.sql` against the production database.
- [ ] Enable HTTPS and forward `X-Forwarded-*` headers when deploying behind a proxy.
- [ ] Keep at least two administrator accounts active.
- [ ] Monitor login throttling via server logs for suspicious activity.

---

Built with Spring Boot 4, Java 21, and a focus on predictable, observable APIs.
