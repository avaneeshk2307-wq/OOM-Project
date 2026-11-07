# CampusConnect — Smart Campus Management System (Phase 1 Core)

[![Spring Boot 3](https://img.shields.io/badge/Spring_Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://openjdk.org/)
[![React](https://img.shields.io/badge/React-18-blue.svg)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.5-blue.svg)](https://www.typescriptlang.org/)
[![Vite](https://img.shields.io/badge/Vite-5.4-purple.svg)](https://vitejs.dev/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791.svg)](https://www.postgresql.org/)
[![Tests](https://img.shields.io/badge/Automated_Tests-18_Passed-success.svg)]()

CampusConnect is a production-grade academic management platform built for university environments using rigorous **Object-Oriented Methodology (OOM)** principles and software engineering best practices.

---

## 🏛️ System Architecture

- **Backend:** Java 21+ with Spring Boot 3.3.5, Spring Data JPA, Spring Security, JWT (HMAC-SHA512).
- **Frontend:** React 18, Vite, TypeScript, Tailwind CSS, Lucide Icons, Axios.
- **Database:** PostgreSQL 16 with Flyway schema migration engine (`V1__init_core_schema.sql`).
- **Object-Oriented Design:**
  - **Inheritance:** Joined-table inheritance (`Person` -> `Student`, `Faculty`, `Admin`).
  - **Composition:** `Course` -> `AssessmentComponent`, `AttendanceSession` -> `AttendanceRecord`.
  - **Aggregation:** `Department` -> `Course`, `Student`, `Faculty`.
  - **State Pattern:** Governs `Enrollment` state transitions (`DRAFT` -> `SUBMITTED` -> `APPROVED` -> `ENROLLED`, `DROPPED`, `REJECTED`).
  - **Strategy Pattern:** Decouples grading policies through `GradingStrategy` and `AbsoluteGradingStrategy`.

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
Database credentials are automatically configured in `backend/src/main/resources/application.yml`:
- **URL:** `jdbc:postgresql://localhost:5432/campusconnect`
- **Username:** `postgres`
- **Password:** `postgres`

---

### 2. Running the Backend
From the repository root:
```bash
cd CampusConnect/backend
./mvnw.cmd spring-boot:run   # On Windows (PowerShell/CMD)
./mvnw spring-boot:run       # On Linux/macOS
```
The backend starts at `http://localhost:8080`. Flyway automatically executes database migrations on startup and `DataInitializer` seeds initial test accounts.

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

CampusConnect contains a comprehensive automated integration and unit test suite verifying database integrity, security boundaries, state transitions, and evaluation calculations.

To execute all tests:
```bash
cd CampusConnect/backend
./mvnw.cmd test
```

### Test Suite Summary:
- **`DomainModelAndRepositoryTest`** (5 tests): Verifies JPA Joined table inheritance, course assessment composition, and repository invariants.
- **`AuthControllerTest`** (3 tests): Tests JWT authentication, bad credentials rejection, and current user profile retrieval.
- **`AdminManagementControllerTest`** (2 tests): Verifies department creation and student semester promotion.
- **`EnrollmentControllerTest`** (3 tests): Verifies GoF State Pattern lifecycle transitions, capacity limits, and duplicate enrollment prevention.
- **`AttendanceControllerTest`** (2 tests): Verifies class session creation, roster presence marking, and 75% low attendance shortage detection.
- **`GradeControllerTest`** (2 tests): Verifies GoF Strategy Pattern grade computation, letter grade assignments, and student GPA reports.
- **`AnnouncementControllerTest`** (2 tests): Verifies campus-wide announcements and role-filtered notice boards.
- **Total:** **18 Tests Passed (100% Success)**

---

## 🔑 Default Accounts (Auto-Seeded)

| Role | Username | Password | Access Capabilities |
| :--- | :--- | :--- | :--- |
| **System Administrator** | `admin` | `admin123` | Department, Course, Faculty, Student, Enrollment, and Announcement Management |
| **Faculty Member** | `faculty` | `faculty123` | Course Roster inspection, Attendance Session marking, Assessment & Grade Entry |
| **Student** | `student` | `student123` | Course Catalog registration, Personal Attendance tracking, Semester Grade Reports |

---

## 📐 UML Diagrams & Documentation

All PlantUML source code diagrams are located in `CampusConnect/diagrams/`:
- `class-diagram.puml`: Comprehensive domain class diagram with design patterns.
- `use-case-diagram.puml`: Role-based system use cases.
- `sequence-diagram.puml`: Course registration and state transition sequence.
- `activity-diagram.puml`: Class meeting attendance marking and 75% shortage branch.
- `state-diagram.puml`: Enrollment lifecycle state machine.
- `component-diagram.puml`: Frontend, backend, and persistence tier component wiring.
- `deployment-diagram.puml`: Physical deployment layout.
- `er-diagram.puml`: 3NF relational schema.

Detailed theoretical mapping for viva defense is available in `CampusConnect/documentation/oom-traceability-matrix.md`.
