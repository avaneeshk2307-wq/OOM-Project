# CampusConnect: Installation & Deployment Guide

This guide provides end-to-end setup and configuration instructions for building, running, and deploying the **CampusConnect** application across development and production environments on Windows and Linux.

---

## 1. System Requirements & Prerequisites

Ensure the following runtimes and developer tools are installed on your host system:

| Tool / Runtime | Recommended Version | Verification Command | Download / Installation Source |
|:---|:---|:---|:---|
| **Java JDK** | OpenJDK 21 LTS (Temurin / Corretto) | `java -version` | [Eclipse Temurin](https://adoptium.net/) |
| **Apache Maven** | Maven 3.9.x (or bundled `./mvnw`) | `mvn -v` | [Apache Maven](https://maven.apache.org/) |
| **Node.js & npm** | Node.js v20.18+ LTS / npm 10.8+ | `node -v && npm -v` | [NodeJS.org](https://nodejs.org/) |
| **Docker (Optional)**| Docker Engine 24+ & Compose | `docker --version` | [Docker Desktop](https://www.docker.com/) |
| **PostgreSQL (Prod)**| PostgreSQL 16.x | `psql -V` | [PostgreSQL.org](https://www.postgresql.org/) |

---

## 2. Fast-Track Development Setup (Zero Database Configuration)

CampusConnect includes a pre-configured **Development Profile (`dev`)** utilizing an embedded, in-memory **H2 Database** with automatic Flyway schema migrations and demo seed records.

### Step 2.1: Clone and Inspect Directory
```bash
git clone https://github.com/avaneeshk2307-wq/OOM-Project.git
cd OOM-Project/CampusConnect
```

### Step 2.2: Launch Backend Service (Spring Boot)
Open a terminal in the backend directory:

**On Windows (PowerShell):**
```powershell
cd backend
$env:SPRING_PROFILES_ACTIVE="dev"
mvn spring-boot:run
```

**On Linux / macOS (Bash):**
```bash
cd backend
export SPRING_PROFILES_ACTIVE="dev"
./mvnw spring-boot:run
```
*The Spring Boot server will initialize on port `8080`.*  
*Access the interactive H2 Web Console at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:campusconnectdb`, Username: `sa`, Password: empty).*

### Step 2.3: Launch Frontend Service (React + Vite)
Open a separate terminal in the frontend directory:

```bash
cd frontend
npm install
npm run dev
```
*The Vite development server will start at `http://localhost:5173`.*

---

## 3. Production Deployment with PostgreSQL & Docker

For production environments, CampusConnect uses PostgreSQL 16 for ACID-compliant persistence.

### Step 3.1: Start PostgreSQL via Docker Compose
From the root `CampusConnect/` directory, launch the database container:
```bash
docker compose up -d
```
This spins up:
- **PostgreSQL 16:** Running on `localhost:5432`, Database: `campusconnect`, User: `postgres`, Password: `password`.
- **pgAdmin 4 (Optional):** Accessible on `http://localhost:5050` (Email: `admin@campusconnect.com`, Password: `admin`).

### Step 3.2: Configure Environment Variables
Set the following environment variables or configure them in `.env`:
```env
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/campusconnect
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=86400000
```

### Step 3.3: Package and Run Backend JAR
```bash
cd backend
mvn clean package -DskipTests
java -jar target/campusconnect-0.0.1-SNAPSHOT.jar
```

### Step 3.4: Build Frontend Production Artifacts
```bash
cd frontend
npm run build
```
This compiles the optimized production bundle into `frontend/dist/`. The output can be served via Nginx, Apache HTTP Server, or static CDN hosting.

---

## 4. Default Seed Accounts for Testing

When initialized with the development profile, the following seed accounts are pre-populated:

| Role | Email Address | Password | Permissions & Dashboard |
|:---|:---|:---|:---|
| **Administrator** | `admin@campusconnect.edu` | `Admin@123` | Full system governance, course creation, user administration. |
| **Faculty Member** | `faculty@campusconnect.edu` | `Faculty@123` | Course rosters, attendance logging, grade submission. |
| **Student** | `student@campusconnect.edu` | `Student@123` | Self-service registration, grade transcript, attendance tracker. |

---

## 5. Troubleshooting & Frequently Asked Questions (FAQ)

### Issue 1: Port Conflict (Port 8080 already in use)
- **Symptom:** `Web server failed to start. Port 8080 was already in use.`
- **Resolution:**
  - Find and terminate the process holding port 8080:
    ```powershell
    # Windows
    Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess | Stop-Process
    ```
  - Alternatively, change the port in `application.yml`:
    ```yaml
    server:
      port: 8081
    ```
  - Update `frontend/.env` to reflect the new port: `VITE_API_BASE_URL=http://localhost:8081/api/v1`.

### Issue 2: CORS Header Rejection
- **Symptom:** Browser console error: `Access to XMLHttpRequest at 'http://localhost:8080/api/v1/...' from origin 'http://localhost:5173' has been blocked by CORS policy.`
- **Resolution:** Verify `SecurityConfig.java` allows `http://localhost:5173` in its `CorsConfigurationSource` bean. The default configuration permits `http://localhost:5173` and `http://localhost:3000`.

### Issue 3: Incompatible Java Version
- **Symptom:** `Fatal error compiling: error: release version 21 not supported.`
- **Resolution:** CampusConnect requires Java 21 LTS features (record patterns, modern pattern matching). Verify `JAVA_HOME` points to JDK 21:
  ```powershell
  $env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot"
  ```
