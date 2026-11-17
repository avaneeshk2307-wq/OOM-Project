# CampusConnect — Complete Project Analysis & OOM Architecture Baseline

**Document Title**: Comprehensive Project Technical Analysis  
**System**: CampusConnect Smart Campus Management System  
**Academic Focus**: Object-Oriented Methodology (OOM)  
**Date**: November 15, 2026  
**Auditor**: Senior Software Documentation Engineer & UML Architect  

---

## 1. Project Overview

**CampusConnect** is a centralized, enterprise-grade Smart Campus Academic Management System designed to digitize and unify administrative, instructional, and student operations within higher education institutions. The system addresses critical inefficiencies in legacy campus workflows by providing automated, secure, role-based workflows for course enrollment, curriculum planning, room and timetable scheduling, attendance tracking, and modular grade calculations.

The platform is engineered around modern Object-Oriented Design (OOD) principles, enforcing loose coupling, high cohesion, clear interface segregation, and formal design patterns (State, Strategy, Observer, Repository).

---

## 2. Actual Technology Stack & Versions

All versions have been verified directly from configuration files (`pom.xml`, `package.json`, environment manifests):

| Layer / Subsystem | Technology | Actual Version | Configuration Source |
| :--- | :--- | :--- | :--- |
| **Backend Runtime** | Java Development Kit (JDK) | OpenJDK 21 LTS (Dev runtime: Java 26.0.2) | `backend/pom.xml` (`<java.version>21</java.version>`) |
| **Backend Framework** | Spring Boot | 3.3.5 | `backend/pom.xml` (`spring-boot-starter-parent:3.3.5`) |
| **Security Layer** | Spring Security | 6.3.4 | Managed by Spring Boot 3.3.5 |
| **Authentication Token**| JSON Web Tokens (JJWT) | 0.12.6 (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`) | `backend/pom.xml` |
| **ORM / Persistence** | Hibernate ORM / Spring Data JPA | 6.5.3.Final | Managed by Spring Boot 3.3.5 |
| **Database Migrations**| Flyway Migration Core | 10.10.0 | `backend/pom.xml` (`flyway-core`, `flyway-database-postgresql`) |
| **Database (Dev/Test)**| H2 In-Memory Database | 2.2.224 | `backend/pom.xml` |
| **Database (Prod)** | PostgreSQL Relational Database | 16.x compatible (`postgresql:42.7.4`) | `backend/pom.xml` |
| **Frontend Framework** | React | 18.3.1 | `frontend/package.json` |
| **Language (Frontend)**| TypeScript | 5.6.3 | `frontend/package.json` |
| **Build & Bundler** | Vite | 5.4.10 (with Rollup code-splitting) | `frontend/package.json` |
| **CSS & Styling** | Tailwind CSS & PostCSS | Tailwind CSS 3.4.14, PostCSS 8.4.47 | `frontend/package.json` |
| **Client Routing** | React Router DOM | 6.27.0 | `frontend/package.json` |
| **Data Visualization** | Recharts | 2.13.3 | `frontend/package.json` |
| **Iconography** | Lucide React | 0.454.0 | `frontend/package.json` |
| **Frontend Testing** | Vitest & React Testing Library | Vitest 4.1.12, RTL 16.3.3, jsdom 29.1.1 | `frontend/package.json` |
| **Backend Testing** | JUnit 5 & Mockito | JUnit Platform 5.10.3, Spring Boot Test | `backend/pom.xml` |
| **Diagram Tool** | PlantUML | 1.2024+ compatible | `diagrams/*.puml` |

---

## 3. Project Folder Structure

```
OOM PROJECT/
├── .env.example                                 # Template environment secrets with placeholders
├── .gitignore                                   # Standard OS, build, and editor exclusions
├── README.md                                    # Root repository overview
├── docker-compose.yml                           # Container definitions for Postgres & app
├── implementation-plan.md                       # Historical engineering milestones
│
└── CampusConnect/
    ├── backend/
    │   ├── pom.xml                              # Maven build definition & dependency tree
    │   └── src/
    │       ├── main/
    │       │   ├── java/com/campusconnect/
    │       │   │   ├── CampusConnectApplication.java
    │       │   │   ├── config/                  # SecurityConfig, DataInitializer, WebMvcConfig
    │       │   │   ├── controller/              # REST Endpoints (11 controllers)
    │       │   │   ├── dto/                     # Request and Response transfer objects
    │       │   │   ├── exception/               # GlobalExceptionHandler, Custom exceptions
    │       │   │   ├── model/                   # JPA Entities (14 domain models)
    │       │   │   ├── pattern/                 # State pattern, Strategy pattern implementations
    │       │   │   ├── repository/              # Spring Data JPA Repository interfaces (17 repositories)
    │       │   │   ├── security/                # JwtTokenProvider, AcademicSecurityService, UserDetails
    │       │   │   └── service/                 # Service interfaces & implementation classes
    │       │   └── resources/
    │       │       ├── application.yml          # Core Spring Boot profile configuration
    │       │       └── db/migration/            # Flyway SQL migrations (V1 through V4)
    │       └── test/java/com/campusconnect/     # JUnit 5 backend test suites (57 tests)
    │
    ├── frontend/
    │   ├── package.json                         # npm package specifications & test scripts
    │   ├── vite.config.ts                       # Vite build configuration, chunking, & Vitest setup
    │   ├── tsconfig.json                        # TypeScript strict compiler rules
    │   └── src/
    │       ├── api/                             # Axios client & service API wrappers
    │       ├── components/                      # UI cards, layouts (DashboardLayout, RoleGuard)
    │       ├── context/                         # AuthContext (session state & credentials)
    │       ├── pages/
    │       │   ├── admin/                       # 10 Admin management and analytics views
    │       │   ├── auth/                        # LoginPage, UnauthorizedPage
    │       │   ├── faculty/                     # 6 Faculty teaching & grading views
    │       │   └── student/                     # 7 Student catalog & academic views
    │       ├── routes/                          # AppRoutes.tsx (lazy-loaded routes)
    │       ├── test/                            # Vitest & RTL test suites (18 tests)
    │       └── types/                           # TypeScript domain interfaces
    │
    ├── diagrams/                                # PlantUML architecture & database models
    │   ├── class-diagram.puml
    │   └── er-diagram.puml
    │
    └── documentation/                           # Academic reports, trace matrices, QA audits
        ├── final-regression-report.md
        ├── oom-traceability-matrix.md
        ├── presentation-content.md
        ├── project-report.md
        └── viva-questions.md
```

---

## 4. Main Application Modules

The application is decomposed into three strictly segregated functional domains according to user roles:

### 4.1 Administrative Subsystem (`ROLE_ADMIN`)
1. **Department Management**: Creation, curriculum linking, and capacity oversight of university departments.
2. **Faculty Administration**: Onboarding instructors, generating employee codes, assigning academic titles, and linking them to departments.
3. **Student Administration**: Student enrollment, roll number generation, semester progression/promotion, and account lifecycle.
4. **Curriculum & Course Management**: Course definition (code, credits, semester, capacity) and instructor section assignment.
5. **Classroom & Timetable Scheduling**: Facility registration (building, room number, type, capacity) and conflict-free timetable matrix generation.
6. **Enrollment Workflow Management**: Institutional review, approval, and finalization of student registration applications.
7. **Institutional Analytics & Audit Logs**: High-level KPIs, grade distributions, enrollment rates, and append-only security audit trail records.

### 4.2 Faculty Instructional Subsystem (`ROLE_FACULTY`)
1. **Instructional Schedule**: Real-time weekly timetable view showing assigned course sections and lecture halls.
2. **Attendance Management**: Session creation, attendance marking (Present, Absent, Late, Excused), and attendance rate analytics.
3. **Continuous Assessment & Grading**: Creation of weighted assessment components (Quizzes, Midterms, Assignments, Finals) and student score entry.
4. **Student Academic Evaluation**: Reviewing student course performance and GPA calculations.

### 4.3 Student Self-Service Subsystem (`ROLE_STUDENT`)
1. **Course Catalog & Registration**: Browsing available curriculum offerings, verifying capacity and prerequisites, and submitting registration requests.
2. **Enrollment Lifecycle**: Real-time status tracking (Submitted $\rightarrow$ Approved $\rightarrow$ Enrolled $\rightarrow$ Dropped).
3. **Personal Timetable**: Personalized weekly schedule view of enrolled course lectures and assigned halls.
4. **Attendance Tracking**: Subject-wise attendance percentages and warning alerts for sub-75% attendance.
5. **Academic Transcript & Grades**: Component-wise scores, letter grades, and cumulative GPA.

---

## 5. Backend Architecture & Design

The backend follows a rigorous **Layered Architecture** with strict inversion of control:

```
[ HTTP REST Clients ]
        │
        ▼
[ Security Filter Chain ] ──▶ JwtAuthenticationFilter / AcademicSecurityService
        │
        ▼
[ Controller Layer ]      ──▶ DTO Validation (@Valid), JSON Serialization
        │
        ▼
[ Service Layer ]         ──▶ Business Rules, State Machines, Strategies, Transactions (@Transactional)
        │
        ▼
[ Repository Layer ]      ──▶ Spring Data JPA, Custom JPQL Queries, Dynamic Projections
        │
        ▼
[ Database Layer ]        ──▶ PostgreSQL / H2 In-Memory Relational Engine
```

### Key Architectural Characteristics
- **Stateless RESTful Execution**: No server-side HTTP sessions; all state is encapsulated in cryptographically signed JWT tokens.
- **Declarative Access Control**: Method-level security via `@PreAuthorize("hasAuthority(...)")` and custom SpEL evaluator beans.
- **Transactional Consistency**: `@Transactional` boundaries on enrollment state transitions and attendance batch submissions.
- **Defensive Error Handling**: Centralized `GlobalExceptionHandler` mapping exceptions (`EntityNotFoundException`, `ConflictException`, `BadCredentialsException`, `HttpMessageNotReadableException`) to RFC 7807 compliant JSON payloads.

---

## 6. Frontend Architecture & Design

The frontend is constructed as a **Single Page Application (SPA)** utilizing modular component architecture:

```
[ App.tsx ]
     │
     ▼
[ AuthProvider ] ──────────▶ Manages JWT, User Identity, localStorage Synchronization
     │
     ▼
[ AppRoutes ] ─────────────▶ Route-level Lazy Loading (React.lazy + Suspense)
     │
     ├── /login, /unauthorized
     │
     └── RoleGuard ────────▶ Verifies role eligibility before rendering protected tree
            │
            ▼
     DashboardLayout ──────▶ Dynamic Navigation Sidebar, Header, Notification Bell
            │
            ▼
     [ Outlet: Active View Component ]
```

### Performance & Bundle Optimization
- Route-based code splitting ensures zero monolithic JavaScript files; main application bundle is **87.64 kB**.
- Shared libraries (`react`, `recharts`, `lucide-react`) are isolated into dedicated vendor chunks.
- Responsive layout utilizes Tailwind CSS utility classes and flexbox grids.

---

## 7. Database Structure & Relational Integrity

The relational schema is enforced via Flyway migrations:

1. **`persons`**: Base identity table (`id`, `name`, `email`, `phone`, `created_at`, `updated_at`).
2. **`admins`**: Specialized identity table (`id` FK to `persons`, `access_level`).
3. **`faculty`**: Specialized identity table (`id` FK to `persons`, `employee_code`, `designation`, `department_id` FK).
4. **`students`**: Specialized identity table (`id` FK to `persons`, `roll_number`, `semester`, `department_id` FK, `account_status`).
5. **`user_accounts`**: Security credential table (`id`, `username`, `password_hash`, `role`, `person_id` FK, `enabled`).
6. **`departments`**: Academic units (`id`, `code` UNIQUE, `name`, `description`).
7. **`courses`**: Curriculum catalog (`id`, `code` UNIQUE, `title`, `credits`, `semester`, `capacity`, `department_id` FK, `faculty_id` FK).
8. **`enrollments`**: Course registrations (`id`, `student_id` FK, `course_id` FK, `semester`, `status`, `registered_at`, `UQ(student_id, course_id, semester)`).
9. **`classrooms`**: Facilities (`id`, `room_number`, `building`, `capacity`, `room_type`).
10. **`timetables`**: Scheduled sections (`id`, `course_id` FK, `classroom_id` FK, `faculty_id` FK, `day_of_week`, `start_time`, `end_time`).
11. **`attendance_sessions`**: Lecture meetings (`id`, `course_id` FK, `faculty_id` FK, `session_date`, `topic`).
12. **`attendance_records`**: Student meeting attendance (`id`, `session_id` FK, `student_id` FK, `status`, `UQ(session_id, student_id)`).
13. **`course_assessments`**: Graded components (`id`, `course_id` FK, `title`, `type`, `max_score`, `weightage_percentage`).
14. **`student_grades`**: Assigned marks (`id`, `assessment_id` FK, `student_id` FK, `score_obtained`, `comments`).
15. **`announcements`**: Broadcast messages (`id`, `title`, `content`, `author_id` FK, `target_role`, `priority`, `published_at`).
16. **`audit_logs`**: Immutable security logs (`id`, `action`, `entity_type`, `entity_id`, `username`, `details`, `timestamp`).

---

## 8. Main Classes and Responsibilities

| Class Name | Package | Role & Responsibility |
| :--- | :--- | :--- |
| `Person` | `model` | Abstract root entity for polymorphism; captures common demographic attributes. |
| `Student` | `model` | Concrete subclass representing a learner; maintains semester standing and course enrollments. |
| `Faculty` | `model` | Concrete subclass representing an instructor; maintains course assignments and employee code. |
| `Admin` | `model` | Concrete subclass representing an administrator with elevated institutional access level. |
| `Enrollment` | `model` | Core association class linking `Student` and `Course`; manages registration lifecycle. |
| `EnrollmentState` | `pattern.state` | State interface governing valid actions (`submit()`, `approve()`, `finalizeEnrollment()`, `drop()`). |
| `GradingStrategy` | `pattern.strategy`| Strategy interface defining score-to-grade calculation algorithm implementations. |
| `AcademicSecurityService`| `security` | Evaluates fine-grained ownership rules to prevent IDOR attacks. |
| `JwtTokenProvider` | `security` | Generates, parses, and cryptographically validates HS256 JWT tokens. |
| `GlobalExceptionHandler`| `exception` | Centralized advice interceptor standardizing error response contracts. |

---

## 9. Authentication & Authorization Workflow

1. **Client Submission**: User submits username and password to `/api/v1/auth/login`.
2. **Credential Verification**: Spring Security's `AuthenticationManager` verifies credentials against BCrypt-hashed passwords in `user_accounts`.
3. **Token Minting**: `JwtTokenProvider` issues a signed JWT token containing `username`, `role`, `personId`, and expiration claim (24 hours).
4. **Client Storage**: React frontend stores token in `localStorage` and synchronizes `AuthContext`.
5. **Authorized Requests**: Axios client attaches header `Authorization: Bearer <token>` to all subsequent requests.
6. **Server Interception**: `JwtAuthenticationFilter` validates token integrity and loads `UserDetails` into `SecurityContextHolder`.
7. **SpEL Evaluation**: Controller methods evaluate `@PreAuthorize` expressions before execution.

---

## 10. Object-Oriented Programming (OOP) Principles Identified

1. **Encapsulation**: All entity attributes are private, exposed only via validated getters, setters, and business methods.
2. **Inheritance**: `Person` serves as the abstract parent for `Student`, `Faculty`, and `Admin` using JPA `InheritanceType.JOINED`.
3. **Polymorphism**:
   - Subtyping: Generic queries on `PersonRepository` polymorphic to all user entities.
   - Dynamic Dispatch: `GradingStrategy.calculateGrade()` and `EnrollmentState.approve()` resolve dynamically at runtime.
4. **Abstraction**: Services expose interfaces (`EnrollmentService`, `CourseService`) completely decoupling business callers from concrete data access.
5. **Association, Aggregation, & Composition**:
   - Composition: `AttendanceSession` strictly owns `AttendanceRecord` instances (cascade delete).
   - Aggregation: `Department` aggregates `Faculty` and `Student` (independent lifecycles).
   - Association: `Timetable` associates `Course`, `Faculty`, and `Classroom`.

---

## 11. Design Patterns Identified

1. **State Design Pattern**:
   - Context: `Enrollment`
   - State Interface: `EnrollmentState`
   - Concrete States: `DraftState`, `SubmittedState`, `ApprovedState`, `EnrolledState`, `DroppedState`, `RejectedState`
   - Value: Eliminates complex nested conditional blocks and prevents illegal status transitions.
2. **Strategy Design Pattern**:
   - Strategy Interface: `GradingStrategy`
   - Concrete Strategies: `PercentageGradingStrategy`, `LetterGradingStrategy`, `GpaGradingStrategy`
   - Value: Allows dynamic switching between percentage calculation, letter grades (A-F), and 4.0 GPA points without code modification.
3. **Repository Pattern**:
   - Interfaces: `StudentRepository`, `EnrollmentRepository`, `CourseRepository`, etc.
   - Value: Abstracts database persistence logic behind standard domain collections.
4. **Data Transfer Object (DTO) Pattern**:
   - `CreateStudentRequest`, `TimetableResponse`, `CampusAnalyticsResponse`
   - Value: Decouples internal database entities from external API contracts and prevents over-posting vulnerabilities.
5. **Observer Pattern**:
   - Application event publishing for audit logging on critical actions (`CREATE_STUDENT`, `ENROLLMENT_STATUS_CHANGE`).

---

## 12. Existing Test Coverage Baseline

- **Backend Test Suite**: 57 tests across 44 test classes (`mvn clean test` — 100% pass rate).
- **Frontend Test Suite**: 18 tests across 4 test files (`npm test` — 100% pass rate).
- **Live Integration Suite**: 36 verified HTTP endpoints across all operational flows (100% pass rate).
- **Total Validated Assertions**: **112 / 112 passed**.

---

## 13. Placeholders for Academic Documentation

To ensure submission integrity, the following institutional parameters will be maintained as clearly marked editable placeholders:

- `[Student Name]` — Full Name of Candidate / Team Member
- `[Roll Number]` — University Examination Roll Number
- `[Department Name]` — e.g., Department of Computer Science & Engineering
- `[University/Institution Name]` — e.g., University Institute of Technology
- `[Faculty Guide Name]` — Faculty Project Supervisor / Evaluator
- `[Academic Session]` — e.g., 2025–2026
- `[Submission Date]` — Official University Submission Date
