# Deployment Guide - Conference Management System

## 🚀 Langkah-langkah Deployment

### 1. Persiapan Environment

#### Install Prerequisites
```bash
# Java Development Kit
java -version  # Harus Java 17+ atau 21

# Maven
mvn -version

# PostgreSQL
psql --version  # Harus PostgreSQL 12+
```

### 2. Setup Database

#### a. Buat Database PostgreSQL
```sql
-- Login ke PostgreSQL
psql -U postgres

-- Buat database
CREATE DATABASE conference_db;

-- Exit psql
\q
```

#### b. Jalankan Init Script
```bash
# Jalankan script SQL
psql -U postgres -d conference_db -f database/init.sql
```

#### c. Verifikasi Database
```sql
-- Login ke database
psql -U postgres -d conference_db

-- Cek tables
\dt

-- Cek sample data
SELECT * FROM users;
```

### 3. Konfigurasi Aplikasi

#### a. Edit application.properties
```properties
# Database - GANTI dengan credentials Anda
spring.datasource.url=jdbc:postgresql://localhost:5432/conference_db
spring.datasource.username=postgres
spring.datasource.password=your_actual_password

# JWT Secret - GANTI dengan secret key yang aman
jwt.secret=generate-a-secure-random-key-at-least-256-bits-long-here
```

#### b. Generate JWT Secret (Recommended)
```bash
# Generate random 256-bit key
openssl rand -base64 32
```

Atau gunakan online generator: https://www.allkeysgenerator.com/Random/Security-Encryption-Key-Generator.aspx

### 4. Build Aplikasi

```bash
# Clean dan compile
./mvnw clean compile

# Run tests (optional)
./mvnw test

# Build JAR file
./mvnw clean package -DskipTests
```

Output JAR: `target/management-system-0.0.1-SNAPSHOT.jar`

### 5. Run Aplikasi

#### Development Mode
```bash
# Run dengan Maven
./mvnw spring-boot:run

# Atau run JAR
java -jar target/management-system-0.0.1-SNAPSHOT.jar
```

#### Production Mode
```bash
# Run dengan custom port
java -jar -Dserver.port=8081 target/management-system-0.0.1-SNAPSHOT.jar

# Run dengan custom profile
java -jar -Dspring.profiles.active=prod target/management-system-0.0.1-SNAPSHOT.jar

# Run in background (Linux/Mac)
nohup java -jar target/management-system-0.0.1-SNAPSHOT.jar &

# Run in background (Windows)
start javaw -jar target/management-system-0.0.1-SNAPSHOT.jar
```

### 6. Verifikasi Deployment

#### a. Health Check
```bash
curl http://localhost:8080/actuator/health
```

#### b. Test Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }'
```

#### c. Akses Swagger UI
```
http://localhost:8080/swagger-ui.html
```

---

## 🐳 Docker Deployment

### 1. Buat Dockerfile
```dockerfile
# Sudah ada di project root
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/management-system-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 2. Build Docker Image
```bash
# Build JAR first
./mvnw clean package -DskipTests

# Build Docker image
docker build -t conference-management:1.0 .
```

### 3. Docker Compose Setup

#### docker-compose.yml
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    container_name: conference-db
    environment:
      POSTGRES_DB: conference_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: your_password
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
      - ./database/init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - conference-network

  app:
    image: conference-management:1.0
    container_name: conference-app
    depends_on:
      - postgres
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/conference_db
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: your_password
      JWT_SECRET: your-jwt-secret-key
    ports:
      - "8080:8080"
    networks:
      - conference-network

volumes:
  postgres-data:

networks:
  conference-network:
    driver: bridge
```

### 4. Run dengan Docker Compose
```bash
# Start services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

---

## ☁️ Cloud Deployment

### AWS Elastic Beanstalk

#### 1. Install EB CLI
```bash
pip install awsebcli
```

#### 2. Initialize EB
```bash
eb init -p java-17 conference-management
```

#### 3. Create Environment
```bash
eb create conference-prod

# Set environment variables
eb setenv SPRING_DATASOURCE_URL=jdbc:postgresql://your-rds-endpoint:5432/conference_db
eb setenv SPRING_DATASOURCE_USERNAME=admin
eb setenv SPRING_DATASOURCE_PASSWORD=your_password
eb setenv JWT_SECRET=your-jwt-secret
```

#### 4. Deploy
```bash
# Build JAR
./mvnw clean package -DskipTests

# Deploy
eb deploy
```

### Azure App Service

#### 1. Install Azure CLI
```bash
# Windows
choco install azure-cli

# Mac
brew install azure-cli
```

#### 2. Login & Deploy
```bash
# Login
az login

# Create resource group
az group create --name conference-rg --location eastus

# Create App Service plan
az appservice plan create \
  --name conference-plan \
  --resource-group conference-rg \
  --sku B1 \
  --is-linux

# Create web app
az webapp create \
  --name conference-app \
  --resource-group conference-rg \
  --plan conference-plan \
  --runtime "JAVA:21-java21"

# Configure app settings
az webapp config appsettings set \
  --name conference-app \
  --resource-group conference-rg \
  --settings \
    SPRING_DATASOURCE_URL="jdbc:postgresql://..." \
    JWT_SECRET="your-secret"

# Deploy JAR
az webapp deploy \
  --name conference-app \
  --resource-group conference-rg \
  --src-path target/management-system-0.0.1-SNAPSHOT.jar \
  --type jar
```

### Heroku

#### 1. Install Heroku CLI
```bash
# Mac
brew install heroku/brew/heroku

# Windows
choco install heroku-cli
```

#### 2. Deploy
```bash
# Login
heroku login

# Create app
heroku create conference-management

# Add PostgreSQL
heroku addons:create heroku-postgresql:hobby-dev

# Set environment variables
heroku config:set JWT_SECRET=your-jwt-secret

# Deploy
git push heroku main
```

---

## 🔒 Production Security Checklist

### 1. Environment Variables
- [ ] JWT secret dari environment variable
- [ ] Database credentials dari environment variable
- [ ] Tidak hardcode passwords di code

### 2. Database
- [ ] Use strong passwords
- [ ] Enable SSL connections
- [ ] Regular backups
- [ ] Database firewall rules

### 3. Application
- [ ] HTTPS only (TLS/SSL)
- [ ] CORS properly configured
- [ ] Rate limiting implemented
- [ ] Input validation enabled
- [ ] SQL injection prevention (JPA handles this)

### 4. Monitoring
- [ ] Application logs
- [ ] Error tracking (Sentry, NewRelic)
- [ ] Performance monitoring
- [ ] Database monitoring

### 5. Backups
- [ ] Daily database backups
- [ ] Application state backups
- [ ] Disaster recovery plan

---

## 📊 Performance Tuning

### application-prod.properties
```properties
# Connection Pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000

# JPA Performance
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# Logging
logging.level.root=WARN
logging.level.com.conference.management_system=INFO

# Compression
server.compression.enabled=true
server.compression.mime-types=application/json,application/xml,text/html,text/xml,text/plain
```

### JVM Options
```bash
java -jar \
  -Xms512m \
  -Xmx2g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  target/management-system-0.0.1-SNAPSHOT.jar
```

---

## 🔄 CI/CD Pipeline

### GitHub Actions

#### .github/workflows/deploy.yml
```yaml
name: Deploy

on:
  push:
    branches: [ main ]

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up JDK 21
      uses: actions/setup-java@v2
      with:
        java-version: '21'
        distribution: 'temurin'
    
    - name: Build with Maven
      run: ./mvnw clean package -DskipTests
    
    - name: Run tests
      run: ./mvnw test
    
    - name: Build Docker image
      run: docker build -t conference-management:${{ github.sha }} .
    
    - name: Deploy to production
      run: |
        # Your deployment script here
        echo "Deploying..."
```

---

## 📱 Monitoring & Logging

### Spring Boot Actuator
```properties
# application.properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
```

### Access Endpoints
```
GET /actuator/health
GET /actuator/metrics
GET /actuator/info
```

---

## 🆘 Troubleshooting

### Application tidak start
```bash
# Check logs
tail -f logs/application.log

# Check if port already used
netstat -ano | findstr :8080  # Windows
lsof -i :8080  # Linux/Mac
```

### Database connection error
```bash
# Test connection
psql -h localhost -U postgres -d conference_db

# Check if PostgreSQL running
systemctl status postgresql  # Linux
brew services list  # Mac
```

### Out of memory
```bash
# Increase heap size
java -jar -Xmx2g target/management-system-0.0.1-SNAPSHOT.jar
```

---

## 📞 Support

Untuk bantuan deployment:
- Email: info@conference.com
- Documentation: http://localhost:8080/swagger-ui.html
- GitHub Issues: [Your Repo]

---

**Happy Deploying! 🚀**
