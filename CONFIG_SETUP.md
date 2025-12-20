# Setup Guide untuk application.properties

## Langkah 1: Copy file example
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

## Langkah 2: Edit credentials di application.properties

### Database Password
Ganti:
```properties
spring.datasource.password=YOUR_POSTGRES_PASSWORD_HERE
```
Menjadi password PostgreSQL Anda yang sebenarnya.

### JWT Secret (PENTING!)
Generate 256-bit Base64 secret baru (security best practice):

**Windows (PowerShell):**
```powershell
[Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(32)))
```

**Linux/Mac:**
```bash
openssl rand -base64 32
```

Kemudian ganti:
```properties
jwt.secret=YOUR_256_BIT_BASE64_JWT_SECRET_HERE
```

## Langkah 3: Pastikan .gitignore correct
Verifikasi `src/main/resources/application.properties` ada di `.gitignore` supaya tidak ter-push ke git.

## Langkah 4: Build & Run
```bash
./mvnw clean package -DskipTests
java -jar target/management-system-0.0.1-SNAPSHOT.jar
```

## ⚠️ JANGAN PERNAH:
- ❌ Push `application.properties` dengan credentials ke git
- ❌ Share JWT secret di Slack, GitHub, atau tempat publik
- ❌ Commit `.env` file dengan passwords
- ❌ Hard-code database password di source code

## ✅ BEST PRACTICES:
- ✅ Keep `application.properties.example` di repo (untuk dokumentasi)
- ✅ Exclude `application.properties` via `.gitignore`
- ✅ Use environment variables untuk production
- ✅ Rotate JWT secret secara berkala
- ✅ Use strong database password (minimal 12 characters)
