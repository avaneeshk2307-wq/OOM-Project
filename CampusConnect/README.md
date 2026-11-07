# CampusConnect — Smart Campus Management System (Phase 1 & Phase 2 Complete)

[![Spring Boot 3](https://img.shields.io/badge/Spring_Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://openjdk.org/)
[![React](https://img.shields.io/badge/React-18-blue.svg)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.5-blue.svg)](https://www.typescriptlang.org/)
[![Vite](https://img.shields.io/badge/Vite-5.4-purple.svg)](https://vitejs.dev/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791.svg)](https://www.postgresql.org/)
[![Tests](https://img.shields.io/badge/Automated_Tests-41_Passed_100%25-success.svg)]()
[![Design Patterns](https://img.shields.io/badge/GoF_Patterns-State_|_Strategy_|_Observer_|_Builder_|_Adapter-purple.svg)]()

**CampusConnect** is an enterprise-grade academic management platform built for university environments using rigorous **Object-Oriented Methodology (OOM)** principles, **SOLID** design axioms, and classic **Gang of Four (GoF)** design patterns.

---

## 🏛️ System Architecture

- **Backend:** Java 21+ with Spring Boot 3.3.5, Spring Data JPA, Spring Security, JWT (HMAC-SHA512).
- **Frontend:** React 18, Vite, TypeScript, Tailwind CSS, Lucide Icons, Recharts, Axios.
- **Database:** PostgreSQL 16 with Flyway schema migration engine (`V1__init_core_schema.sql` and `V2__phase2_advanced_schema.sql`).
- **Object-Oriented Design Highlights:**
  - **Inheritance:** Joined-table inheritance (`Person` $\to$ `Student`, `Faculty`, `Admin`).
  - **Composition:** `Course` $\to$ `AssessmentComponent`, `AttendanceSession` $\to$ `AttendanceRecord`.
  - **Aggregation:** `Department` $\to$ `Course`, `Student`, `Faculty`; `TranscriptDirector` $\to$ `TranscriptBuilder`.
  - **State Pattern (GoF):** Governs `Enrollment` state transitions (`DRAFT` $\to$ `SUBMITTED` $\to$ `APPROVED` $\to$ `ENROLLED`, `DROPPED`, `REJECTED`).
  - **Strategy Pattern (GoF):** Decouples grading evaluation: `AbsoluteGradingStrategy` (criterion scale) and `RelativeGradingStrategy` (Gaussian normal curve / Z-scores) managed via `GradingStrategyFactory`.
  - **Observer Pattern (GoF):** Event-driven notification distribution: `AcademicEventListener` captures Spring domain events (`GradePublishedEvent`, `AttendanceShortageEvent`, `EnrollmentApprovedEvent`) and persists in-app alerts.
  - **Builder Pattern (GoF):** Multi-part academic transcript construction: `TranscriptBuilder`, `ConcreteTranscriptBuilder`, and `TranscriptDirector` assemble credentialed, printable official transcripts.
  - **Algorithmic Collision Detection:** Mathematical interval intersection checking for classroom, faculty, and course scheduling.
  - **Immutable Audit Trail:** System-wide administrative activity tracking via `AuditLogService` into `audit_logs`.

---

## 🚀 Getting Started

### Prerequisites
- **JDK:** Java 21 or higher (OpenJDK / Oracle JDK)
- **Node.js:** Node.js 18+ and npm
- **Database:** PostgreSQL 16 (or Docker)

---

### 1. Database Setup
Ensure PostgreSQL is running locally or start the included Docker Compose container:
```bash
docker-compose up -d postgres
```
Database credentials are configured in `backend/src/main/resources/application.yml`:
- **URL:** `jdbc:postgresql://localhost:5432/campusconnect`
- **Username:** `postgres`
- **Password:** `postgres`

---

### 2. Running the Backend
From the repository root:
```bash
cd CampusConnect/backend
./mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"   # Windows (PowerShell/CMD)
./mvnw spring-boot:run "-Dspring-boot.run.profiles=dev"       # Linux/macOS
```
The backend starts at `http://localhost:8080`. Flyway executes database migrations automatically and `DataInitializer` seeds initial test accounts, classrooms, and timetables.

---

### 3. Running the Frontend
In a separate terminal:
```bash
cd CampusConnect/frontend
npm install
npm run dev
```
The application opens in your browser at `http://localhost:5173`.

---

## 🧪 Automated Testing

CampusConnect contains an automated regression test suite verifying database integrity, security boundaries, state transitions, grading algorithms, and timetable conflict collisions.

To execute all tests:
```bash
cd CampusConnect/backend
./mvnw.cmd test
```

### Test Suite Summary:
- **`DomainModelAndRepositoryTest`** (5 tests): JPA Joined table inheritance, course assessment composition, repository queries.
- **`AuthControllerTest`** (3 tests): JWT login, invalid password handling, authenticated user profile endpoint.
- **`AdminManagementControllerTest`** (5 tests): Department CRUD, student semester promotion, faculty assignment.
- **`EnrollmentControllerTest`** (4 tests): State pattern transitions, capacity enforcement, duplicate enrollment prevention.
- **`AttendanceControllerTest`** (2 tests): Attendance session recording and 75% shortage alert detection.
- **`GradeControllerTest`** (4 tests): Absolute grading, relative grading toggle, student GPA calculation.
- **`AnnouncementControllerTest`** (2 tests): Campus-wide announcements and notice boards.
- **`HealthControllerTest`** (1 test): Application health probe.
- **`TimetableServiceTest`** (5 tests): Conflict collision detection for rooms, faculty, and courses; capacity checks.
- **`TimetableControllerIntegrationTest`** (2 tests): MockMvc interval conflict detection returning HTTP 409 Conflict; 401 unauthenticated check.
- **`RelativeGradingStrategyTest`** (4 tests): Gaussian normal distribution, Z-score calculations, and cutoff validations.
- **`AcademicEventListenerTest`** (3 tests): Observer pattern event publishing and persistent notification persistence.
- **`TranscriptBuilderTest`** (2 tests): GoF Builder pattern transcript assembly, Dean's List standing, and signature verification.
- **`AnalyticsServiceTest`** (1 test): Institutional aggregation metrics across departments and course capacity.
- **`AuditLogServiceTest`** (3 tests): Administrative activity recording, pagination, and recent logs retrieval.
- **`AuditAndSecurityIntegrationTest`** (3 tests): Role-based access boundaries (Admin permitted, Student forbidden 403, Anonymous 401).
- **Total:** **41 Tests Passed (100% Success, 0 Failures, 0 Errors)**

---

## 🔑 Default Accounts (Auto-Seeded)

| Role | Username | Password | Access Capabilities |
| :--- | :--- | :--- | :--- |
| **System Administrator** | `admin` | `admin123` | Departments, Courses, Faculty, Students, Timetables, Analytics, Audit Logs, Announcements |
| **Faculty Member** | `faculty` | `faculty123` | Assigned Courses, Teaching Timetable, One-Click Attendance, Absolute & Relative Grade Entry |
| **Student** | `student` | `student123` | Class Schedule, Course Catalog, Attendance Records, Official Transcript Modal (Printable) |

---

## 📐 UML Diagrams & Documentation

All PlantUML source code diagrams are located in `CampusConnect/diagrams/`:
- `class-diagram.puml`: Complete domain model with State, Strategy, Observer, and Builder patterns.
- `er-diagram.puml`: 3NF relational schema (15 tables including classrooms, timetables, notifications, audit logs).
- `sequence-diagram.puml`: Sequence interactions for State transitions, Conflict checking, Observer alerts, and Transcript Builder.
- `component-diagram.puml`: 3-tier component wiring across React, Spring Boot, and PostgreSQL.
- `deployment-diagram.puml`: Physical deployment topology.
- `state-diagram.puml`: Course registration lifecycle state machine.
- `activity-diagram.puml`: Attendance workflow with statutory 75% shortage branch.
- `use-case-diagram.puml`: Role-based actor use cases.

### University Evaluation Deliverables:
- **Traceability Matrix:** `documentation/oom-traceability-matrix.md` (Mapping OOP pillars, SOLID principles, and GoF patterns to concrete lines of code).
- **Project Report:** `documentation/project-report.md` (Academic project report with abstract, architecture, algorithms, and results).
- **Presentation Content:** `documentation/presentation-content.md` (16-slide academic presentation deck with complete speaker notes).
- **Viva Voce Preparation:** `documentation/viva-questions.md` (32 detailed Q&As covering OOP, design patterns, algorithms, database normalization, and security).
