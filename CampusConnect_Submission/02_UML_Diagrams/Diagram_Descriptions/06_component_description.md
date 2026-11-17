# Diagram 6: Component Diagram Description

## 1. Diagram Title
**CampusConnect Multi-Tier Component Architecture**

## 2. Diagram Purpose & Academic Importance
The Component Diagram illustrates the high-level structural decomposition of the software into autonomous, loosely coupled software modules. It emphasizes modular interfaces (ports and sockets) and runtime dependencies across client, application server, and persistence tiers.

## 3. Structural Decomposition
- **Client Tier (React Single Page Application)**:
  - `AuthContext` manages global session tokens.
  - `RoleGuard & Router` enforces client-side route access control.
  - `AdminUI`, `FacultyUI`, and `StudentUI` modules interact exclusively through `apiClient` (Axios).
- **Application Server Tier (Spring Boot Modular Engine)**:
  - `Security Subsystem`: `JwtAuthenticationFilter` and `AcademicSecurityService` enforce IDOR and RBAC boundaries.
  - `REST Controller Layer`: Validates incoming DTOs (`@Valid`) and returns standardized JSON responses.
  - `Business Service Layer`: Encapsulates business logic, state pattern transitions, and grading algorithms.
  - `Persistence Layer`: Spring Data JPA repositories orchestrate Hibernate ORM operations.
- **Database Tier**: Relational schema managed by Flyway migrations on PostgreSQL / H2 engines.

## 4. Source Code Mapping
- Client: `frontend/src/*`
- Backend Modules: `com.campusconnect.config`, `controller`, `service`, `repository`, `model`
