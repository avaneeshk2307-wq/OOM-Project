# Diagram 7: Deployment Diagram Description

## 1. Diagram Title
**CampusConnect Physical & Virtual Deployment Topology**

## 2. Diagram Purpose & Academic Importance
The Deployment Diagram defines the physical and virtual hardware nodes, environments, and communication protocols over which CampusConnect artifacts are distributed and executed in production and development environments.

## 3. Execution Nodes & Artifacts
1. **Client Workstation / Mobile Node**:
   - Modern Web Browser executing the compiled React SPA client bundle (`87.64 kB` main bundle with on-demand chunk loading).
2. **Frontend Web Server Node**:
   - Vite development server or production Nginx reverse proxy serving static assets (`dist/assets/*`).
   - Communication: HTTP/HTTPS (Port 5173 dev / 80 & 443 prod).
3. **Application Server Node**:
   - Host machine or Docker container hosting Java 21 OpenJDK runtime.
   - Embedded Apache Tomcat servlet container running `campusconnect-backend.jar` on Port 8080.
   - Communication: RESTful JSON over HTTPS with JWT bearer authentication.
4. **Database Server Node**:
   - PostgreSQL 16 container or server instance listening on Port 5432.
   - Connected via HikariCP high-performance connection pool over JDBC.
   - H2 in-memory relational engine used for isolated regression testing suites.

## 4. Source Code Mapping
- Deployment Manifest: `docker-compose.yml`, `backend/Dockerfile`, `frontend/Dockerfile`
- Configuration: `backend/src/main/resources/application.yml`
