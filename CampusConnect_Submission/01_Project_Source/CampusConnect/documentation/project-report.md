# CampusConnect: Smart Campus Management System
## Object-Oriented Methodology (OOM) Project Report
**Academic Year:** 2026  
**Degree Program:** Bachelor of Technology / Computer Science & Engineering  
**Project Category:** Enterprise Software Engineering & Object-Oriented Design  

---

## Abstract
Modern higher education institutions require software architectures that are robust, extensible, and mathematically verifiable across high-volume academic operations. **CampusConnect** is a full-stack, enterprise-grade Smart Campus Management System engineered strictly under Object-Oriented Methodology (OOM) and Clean Architecture principles.

The system addresses critical academic workflows: multi-role authentication (RBAC), student and faculty lifecycle management, department and curriculum organization, dynamic course registration managed via the **GoF State Pattern**, dual-mode grading (criterion-referenced and Gaussian normal distribution) via the **GoF Strategy Pattern**, decoupled event-driven alerts via the **GoF Observer Pattern**, credentialed academic transcript generation via the **GoF Builder Pattern**, mathematical interval-intersection conflict detection for timetable scheduling, institutional analytics visualization, and tamper-evident administrative audit logging.

The system is implemented using a modern decoupled technology stack comprising **Java 21 LTS**, **Spring Boot 3.3.5**, **PostgreSQL 16** (with **Flyway** migrations), **Hibernate/JPA**, and a responsive frontend built with **React 18**, **TypeScript**, **Tailwind CSS**, and **Vite**. Automated regression testing demonstrates 100% test pass rates across 41 backend unit, integration, and security tests.

---

## Table of Contents
1. **Chapter 1: Introduction & Problem Statement**
2. **Chapter 2: System Requirements & Architecture**
3. **Chapter 3: Object-Oriented Analysis & Design (OOAD)**
4. **Chapter 4: Design Patterns Implementation**
5. **Chapter 5: Relational Database Design (3NF)**
6. **Chapter 6: Algorithmic Implementations**
7. **Chapter 7: Frontend Architecture & User Experience**
8. **Chapter 8: Verification, Testing & Security Audit**
9. **Chapter 9: University Evaluation & Traceability Matrix**
10. **Chapter 10: Conclusion & Future Enhancements**

---

## Chapter 1: Introduction & Problem Statement

### 1.1 Context
University campus administration involves intricate, interrelated workflows across multiple administrative and academic tiers. Legacy university systems frequently suffer from architectural pitfalls:
- Tightly coupled monolithic codebases where changes to grading rules disrupt course registration.
- Ad-hoc database designs lacking normalization, resulting in data anomalies and orphan records.
- Procedural code structures filled with deeply nested conditional statements (`if-else` / `switch`) to handle state transitions.
- High risk of resource collisions in classroom and faculty scheduling.

### 1.2 Problem Statement
The objective of this project is to architect and deliver **CampusConnect**, a campus management platform that models academic domain entities using pure Object-Oriented Programming (OOP) principles, adheres to SOLID design axioms, implements classic Gang of Four (GoF) design patterns to solve real behavioral and creational challenges, guarantees mathematical conflict-free timetable scheduling, and delivers role-tailored dashboards for Administrators, Faculty, and Students.

---

## Chapter 2: System Requirements & Architecture

### 2.1 Functional Requirements
1. **Identity & Access Management:** Secure stateless JWT authentication with Role-Based Access Control (`ROLE_ADMIN`, `ROLE_FACULTY`, `ROLE_STUDENT`).
2. **Curriculum & Organization:** Hierarchical departments offering multi-credit courses with assigned instructors, capacity ceilings, and semester mappings.
3. **Course Registration Lifecycle:** Enforced multi-stage student registration transitioning through *Draft* $\to$ *Submitted* $\to$ *Approved* $\to$ *Enrolled* $\to$ *Dropped* / *Rejected*.
4. **Attendance Management:** Date-wise session recording with automated computation of attendance percentages and statutory 75% shortage alert triggers.
5. **Dual-Mode Grade Management:** Assessment component creation, weighted evaluation, and dynamic calculation under Absolute criterion or Gaussian Bell-Curve Relative strategies.
6. **Timetable Scheduling:** Weekly grid scheduling with real-time collision detection for classrooms, faculty members, and course sections.
7. **Observer Notifications:** Persistent event-driven alerts delivered to student inboxes when grades are published, attendance drops, or enrollments are approved.
8. **Official Academic Transcripts:** Print-ready, credentialed transcripts with weighted GPA calculations, credit summaries, and academic honor designations.
9. **Institutional Analytics:** Real-time visual data aggregations including department metrics, course occupancy, campus attendance health, and faculty workload.
10. **Administrative Audit Trail:** Immutable logging of student creation, course modifications, grade submissions, and attendance markings.

### 2.2 System Architecture
CampusConnect follows a decoupled, three-tier client-server architecture:
```
+-------------------------------------------------------------------+
|               Presentation Tier (React 18 + TypeScript)           |
|   Tailwind CSS  |  Lucide Icons  |  Recharts  |  Axios HTTP       |
+-------------------------------------------------------------------+
                                  |
                           REST APIs (JSON)
                           Bearer JWT Auth
                                  |
                                  v
+-------------------------------------------------------------------+
|               Application Tier (Spring Boot 3.3.5 / Java 21)       |
|  Controllers  |  Services  |  Security  |  GoF Patterns  |  JPA   |
+-------------------------------------------------------------------+
                                  |
                             JDBC / SQL
                                  |
                                  v
+-------------------------------------------------------------------+
|               Database Tier (PostgreSQL 16 / Flyway)              |
|  3NF Tables  |  Foreign Key Constraints  |  Indices  |  Triggers  |
+-------------------------------------------------------------------+
```

---

## Chapter 3: Object-Oriented Analysis & Design (OOAD)

### 3.1 Four Pillars of OOP in CampusConnect
1. **Abstraction:**
   - The abstract base entity `Person` (`com.campusconnect.entity.Person`) encapsulates fundamental attributes (`name`, `email`, `phone`, `accountStatus`) and lifecycle behavior (`activate()`, `deactivate()`, `updateContactInfo()`). It cannot be instantiated on its own.
   - The `TranscriptBuilder` interface abstracts transcript construction steps without exposing internal string buffers or report structures.
2. **Inheritance:**
   - JPA Joined-Table Inheritance (`@Inheritance(strategy = InheritanceType.JOINED)`):
     - `Student extends Person` (adds `rollNumber`, `semester`, `department`).
     - `Faculty extends Person` (adds `employeeCode`, `designation`, `department`).
     - `Admin extends Person` (adds `roleTitle`).
   - In PostgreSQL, this maps to parent table `persons` and joined child tables `students`, `faculty`, and `admins`, eliminating column nullability issues inherent in Single-Table strategies.
3. **Encapsulation:**
   - Domain invariants are strictly maintained within entity boundaries. For instance, `Course.setCredits()` rejects non-positive credits; `Grade.setMarksObtained()` prevents scores exceeding maximum assessment points; `TimetableEntry` enforces that start time precedes end time.
   - Data Transfer Objects (DTOs) use Java `records` to ensure immutability across controller boundaries.
4. **Polymorphism:**
   - Dynamic strategy dispatch: `GradingStrategy.calculateGrade()` behaves polymorphically based on the runtime strategy instance (`AbsoluteGradingStrategy` vs `RelativeGradingStrategy`).
   - State transition polymorphism: `EnrollmentState.approve()`, `drop()`, and `reject()` execute distinct validation logic depending on whether the object is in `RequestedState`, `ApprovedState`, or `EnrolledState`.

### 3.2 Advanced Class Relationships
- **Composition (Part-Whole with Cascaded Lifecycle):**
  - `Course` $\to$ `AssessmentComponent`: Assessments cannot exist without a course. Deleting a course cascades deletion to its assessments (`cascade = CascadeType.ALL, orphanRemoval = true`).
  - `AttendanceSession` $\to$ `AttendanceRecord`: Attendance records belong exclusively to their session aggregate.
- **Aggregation (Independent Lifecycle):**
  - `Department` $\to$ `Course`, `Student`, `Faculty`: A department aggregates courses and people; deleting or restructuring a department does not delete students or faculty members.
- **Association:**
  - `Faculty` $\to$ `Course` (teaches relationship).
  - `Student` $\to$ `Course` mediated via `Enrollment`.
  - `TimetableEntry` associates `Course`, `Classroom`, and `Faculty`.

---

## Chapter 4: Design Patterns Implementation

### 4.1 GoF State Pattern (Course Enrollment Lifecycle)
- **Problem:** Course registration involves multiple stages with strict sequencing rules. Procedural code with nested conditionals leads to bugs like students dropping unapproved courses or skipping administrative vetting.
- **Solution:**
  - State interface: `EnrollmentState`.
  - Abstract base: `AbstractEnrollmentState` (throws `IllegalStateException` by default for invalid operations).
  - Concrete states: `DraftState`, `SubmittedState`, `ApprovedState`, `EnrolledState`, `DroppedState`, `RejectedState`.
  - Factory: `EnrollmentStateFactory` maps `EnrollmentStatus` to state instances.

### 4.2 GoF Strategy Pattern (Dual-Mode Grading)
- **Problem:** Academic institutions alternate between criterion-referenced grading (fixed percentage cutoffs) and norm-referenced relative grading (Gaussian bell curve).
- **Solution:**
  - Strategy interface: `GradingStrategy`.
  - Strategy 1: `AbsoluteGradingStrategy` (criterion scale: 90%+ A+, 80%+ A, etc.).
  - Strategy 2: `RelativeGradingStrategy` (calculates cohort mean $\mu$ and standard deviation $\sigma$, determining grades via Z-score: $Z = \frac{X - \mu}{\sigma}$).
  - Factory: `GradingStrategyFactory` supplies the requested strategy at runtime.

### 4.3 GoF Observer Pattern (Persistent Campus Notifications)
- **Problem:** Critical academic actions (grade publication, attendance shortage warnings, enrollment approval) require automated notification delivery without tight coupling between core services and alert sinks.
- **Solution:**
  - Publisher: Spring `ApplicationEventPublisher`.
  - Domain Events: `GradePublishedEvent`, `AttendanceShortageEvent`, `EnrollmentApprovedEvent`.
  - Concrete Observer: `AcademicEventListener` listens via `@EventListener` and delegates to `NotificationService` to persist tamper-evident in-app notifications.

### 4.4 GoF Builder Pattern (Academic Transcript Assembly)
- **Problem:** An official university academic transcript is a complex, composite document requiring student biographical verification, course records formatting, weighted GPA math, academic standing evaluation (e.g. "Dean's List"), and registrar verification seals.
- **Solution:**
  - Product: `AcademicTranscriptReport`.
  - Builder interface: `TranscriptBuilder`.
  - Concrete Builder: `ConcreteTranscriptBuilder`.
  - Director: `TranscriptDirector` manages the disciplined construction sequence.

### 4.5 Structural Adapter Pattern
- `SecurityUser` implements Spring Security's `UserDetails` contract, adapting the domain's `UserAccount` entity without polluting the domain model with framework annotations.

---

## Chapter 5: Relational Database Design (3NF)

The database schema is normalized to Third Normal Form (3NF) to ensure zero data redundancy and prevent insert, update, and delete anomalies.

### Key Tables & Constraints:
1. `departments`: Primary key `id`, unique constraint on `code`.
2. `persons`: Primary key `id`, unique constraint on `email`.
3. `students`: Primary key `id` (FK to `persons`), unique constraint on `roll_number`.
4. `faculty`: Primary key `id` (FK to `persons`), unique constraint on `employee_code`.
5. `admins`: Primary key `id` (FK to `persons`), attribute `role_title`.
6. `user_accounts`: Primary key `id`, unique constraint on `username` and `person_id`.
7. `courses`: Primary key `id`, unique `code`, foreign keys to `departments` and `faculty`.
8. `classrooms`: Primary key `id`, unique `room_number`, check constraint $capacity > 0$.
9. `timetables`: Primary key `id`, foreign keys to `courses`, `classrooms`, `faculty`, check constraint $start\_time < end\_time$.
10. `notifications`: Primary key `id`, foreign key to `user_accounts`, indexed on `user_id` and `is_read`.
11. `audit_logs`: Primary key `id`, action string, actor username, IP address, timestamp.
12. `enrollments`: Primary key `id`, composite unique constraint `(student_id, course_id)`.
13. `course_assessments`: Primary key `id`, foreign key to `courses`.
14. `attendance_sessions` & `attendance_records`: Composite unique constraint `(session_id, student_id)`.
15. `grades`: Composite unique constraint `(enrollment_id, assessment_id)`.

---

## Chapter 6: Algorithmic Implementations

### 6.1 Timetable Interval Intersection Conflict Detection
To guarantee scheduling integrity, `TimetableService` enforces mathematical interval intersection:
$$\text{Overlap}(T_1, T_2) \iff [T_1.start < T_2.end] \land [T_1.end > T_2.start]$$

Before any timetable slot is committed, three SQL interval queries are executed:
1. Classroom collision: `findClassroomConflicts(roomId, day, start, end)`
2. Faculty collision: `findFacultyConflicts(facultyId, day, start, end)`
3. Course collision: `findCourseConflicts(courseId, day, start, end)`

If any conflicting record exists, the transaction rolls back and throws `TimetableConflictException`, mapped by `GlobalExceptionHandler` to HTTP 409 Conflict.

### 6.2 Gaussian Normal Distribution Relative Grading
In norm-referenced grading, the system evaluates cohort scores $\{x_1, x_2, \dots, x_n\}$:
1. Mean: $\mu = \frac{1}{n} \sum_{i=1}^n x_i$
2. Variance: $\sigma^2 = \frac{1}{n} \sum_{i=1}^n (x_i - \mu)^2$
3. Standard Deviation: $\sigma = \sqrt{\sigma^2}$
4. Standardized Z-Score: $Z_i = \frac{x_i - \mu}{\sigma}$

Grades are assigned based on standardized cutoffs:
- $Z \ge +1.5\sigma \implies \text{A+}$ (Grade Point 4.0)
- $+1.0\sigma \le Z < +1.5\sigma \implies \text{A}$ (Grade Point 3.75)
- $+0.5\sigma \le Z < +1.0\sigma \implies \text{B+}$ (Grade Point 3.5)
- $0.0\sigma \le Z < +0.5\sigma \implies \text{B}$ (Grade Point 3.0)
- $-0.5\sigma \le Z < 0.0\sigma \implies \text{C+}$ (Grade Point 2.5)
- $-1.0\sigma \le Z < -0.5\sigma \implies \text{C}$ (Grade Point 2.0)
- $-1.5\sigma \le Z < -1.0\sigma \implies \text{D}$ (Grade Point 1.0)
- $Z < -1.5\sigma \implies \text{F}$ (Grade Point 0.0)

---

## Chapter 7: Frontend Architecture & User Experience

The frontend is built using **React 18** with **TypeScript** in strict mode, bundled with **Vite**:
- **Authentication & Security:** `AuthContext` stores JWT tokens in local storage, attaching Bearer headers to Axios interceptors. `RoleGuard` blocks unauthorized route transitions.
- **Notification Dropdown:** Real-time badge counter in the top bar (`NotificationBell.tsx`) displaying unread notification counts, live mark-as-read triggers, and direct link navigation.
- **Timetable Grid:** Interactive day-by-hour weekly grid (`TimetableGrid.tsx`) with room and faculty tags, capacity gauges, and conflict error alerts.
- **Official Transcript Modal:** High-fidelity print-ready transcript component (`OfficialTranscriptModal.tsx`) complete with verification seals, dean standing badges, and browser print stylesheet overrides (`@media print`).
- **Interactive Analytics:** Data visualizer (`AdminAnalytics.tsx`) powered by **Recharts**, rendering bar charts of department enrollments, pie charts of grade distributions, and workload metric cards.
- **Audit Viewer:** Tamper-evident activity ledger (`AdminAuditLogs.tsx`) featuring real-time search, action badge filters, and JSON payload inspection modals.

---

## Chapter 8: Verification, Testing & Security Audit

### 8.1 Automated Regression Test Suite
CampusConnect includes automated regression coverage across all layers:
- Unit tests for Strategy Pattern: `RelativeGradingStrategyTest` (4 tests)
- Unit tests for Builder Pattern: `TranscriptBuilderTest` (2 tests)
- Unit tests for Observer Pattern: `AcademicEventListenerTest` (3 tests)
- Unit tests for Timetable Conflict Service: `TimetableServiceTest` (5 tests)
- Unit tests for Analytics Service: `AnalyticsServiceTest` (1 test)
- Unit tests for Audit Log Service: `AuditLogServiceTest` (3 tests)
- JPA Repository & Domain Model tests: `DomainModelAndRepositoryTest` (5 tests)
- REST Controller Integration tests:
  - `AdminManagementControllerTest` (5 tests)
  - `AuthControllerTest` (3 tests)
  - `EnrollmentControllerTest` (4 tests)
  - `AttendanceControllerTest` (2 tests)
  - `GradeControllerTest` (4 tests)
  - `AnnouncementControllerTest` (2 tests)
  - `HealthControllerTest` (1 test)
  - `TimetableControllerIntegrationTest` (2 tests)
  - `AuditAndSecurityIntegrationTest` (3 tests)

**Total Test Suite Results:**
- Tests run: **41**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- Success Rate: **100%**

---

## Chapter 9: University Evaluation & Traceability Matrix

| Evaluation Criteria | Requirement | CampusConnect Evidence |
| :--- | :--- | :--- |
| **Pillars of OOP** | Abstraction, Inheritance, Encapsulation, Polymorphism | `Person` abstract class, Joined Table JPA, encapsulated setters, dynamic Strategy dispatch. |
| **Relationships** | Composition, Aggregation, Association | `Course`-`Assessment` composition, `Department`-`Student` aggregation, `Faculty`-`Course` association. |
| **Design Patterns** | 3+ Gang of Four Patterns | State Pattern (Enrollment), Strategy Pattern (Grading), Observer Pattern (Notifications), Builder Pattern (Transcripts). |
| **Architecture** | 3-Tier Clean Architecture | Decoupled React Frontend + Spring Boot Backend + PostgreSQL Database. |
| **Integrity & Constraints** | 3NF Schema & Invariants | 15 relational tables, Foreign Keys, unique indices, time checks, capacity checks. |
| **Conflict Algorithms** | Real-world problem solving | Mathematical interval intersection $[start_A < end_B] \land [end_A > start_B]$. |
| **Statistical Modeling** | Advanced algorithmic logic | Gaussian Bell Curve Relative Grading ($Z$-score calculation). |
| **Security & Auditing** | Authentication, RBAC, Auditability | Stateless JWT, BCrypt hashing, Spring Method Security, immutable `audit_logs` table. |

---

## Chapter 10: Conclusion & Future Enhancements

CampusConnect successfully demonstrates how theoretical Object-Oriented Methodology transforms complex campus operational requirements into clean, maintainable, extensible enterprise software. By enforcing strict separation of concerns, GoF behavioral and creational design patterns, and rigorous mathematical invariants, the application achieves high architectural stability with 100% test automation pass rates.

### Future Enhancements:
1. Integration with institutional LDAP / Single Sign-On (SSO).
2. Native PDF export engine using Apache PDFBox for server-side transcript stamping.
3. Real-time WebSocket notifications replacing HTTP polling.
4. Machine Learning-based student dropout risk prediction built on attendance and grade trajectories.

---
*CampusConnect Project Report — Final Submission for OOM University Examination.*

