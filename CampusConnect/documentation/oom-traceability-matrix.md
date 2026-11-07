# CampusConnect — Object-Oriented Methodology (OOM) Traceability Matrix

This document provides exhaustive, end-to-end traceability between theoretical Object-Oriented Programming (OOP) concepts, software design patterns, SOLID principles, and their concrete implementation within the **CampusConnect** codebase across both **Phase 1** and **Phase 2**.

---

## 1. Core Object-Oriented Pillars

| OOP Concept | Architectural Purpose | Concrete Code Implementation | Source Location |
| :--- | :--- | :--- | :--- |
| **Abstraction** | Hide complex identity, authorization, and lifecycle details while exposing uniform interfaces. | `Person` abstract class defines common attributes (`name`, `email`, `phone`, `accountStatus`) and lifecycle methods without allowing direct instantiation. `TranscriptBuilder` provides an abstract interface for assembling complex academic documents without exposing internal data structures. | `entity/Person.java`<br>`patterns/builder/TranscriptBuilder.java` |
| **Inheritance** | Eliminate code duplication and model genuine hierarchical domain relationships. | `Student`, `Faculty`, and `Admin` extend `Person` using JPA `@Inheritance(strategy = InheritanceType.JOINED)`. Subclasses inherit common identity while providing specialized domain state (`rollNumber`, `employeeCode`, `roleTitle`). Verified in database via separate child tables joined on primary key `id`. | `entity/Person.java`<br>`entity/Student.java`<br>`entity/Faculty.java`<br>`entity/Admin.java` |
| **Encapsulation** | Protect class invariants by hiding internal representation and controlling mutations through encapsulated business methods. | 1. Non-negative credit checks in `Course.setCredits()`.<br>2. `Enrollment.submit()`, `approve()`, `enroll()`, `drop()` enforce transition validation.<br>3. `Grade.setMarks()` ensures marks do not exceed `assessment.maxMarks`.<br>4. `Classroom.setCapacity()` validates $capacity > 0$.<br>5. Immutable records (`CampusAnalyticsResponse`, `AuditLogResponse`, `GradingResult`). | `entity/Course.java`<br>`entity/Enrollment.java`<br>`entity/Grade.java`<br>`entity/Classroom.java`<br>`dto/*` |
| **Polymorphism** | Dynamic dispatch of behavior at runtime depending on the underlying concrete object type. | 1. Polymorphic queries via `PersonRepository.findById()` returning either `Student` or `Faculty`.<br>2. State transitions via `EnrollmentState.approve()`, `drop()`, etc.<br>3. Pluggable grading strategies: `GradingStrategy.calculateGrade()` dynamically dispatching to `AbsoluteGradingStrategy` or `RelativeGradingStrategy`. | `patterns/state/*`<br>`patterns/strategy/*`<br>`service/GradeServiceImpl.java` |

---

## 2. Advanced Object Relationships

| Relationship Type | Definition & Invariant | Concrete Implementation | Source Location |
| :--- | :--- | :--- | :--- |
| **Composition** | Strong "part-of" relationship where child objects cannot exist independently of the parent aggregate root (cascaded persistence & orphan removal). | 1. `Course` owns `AssessmentComponent`: Deleting a course permanently removes all associated assessments (`cascade = ALL, orphanRemoval = true`).<br>2. `AttendanceSession` owns `AttendanceRecord`: The session aggregate root controls adding and updating student records through `recordStudentAttendance()`. | `entity/Course.java`<br>`entity/AttendanceSession.java`<br>`entity/AttendanceRecord.java` |
| **Aggregation** | "Has-a" relationship with independent lifecycles. Child objects survive if parent container is deleted. | `Department` aggregates `Course`, `Student`, and `Faculty`. Deleting a department does not destroy the physical faculty or courses; it unassigns the organizational link. Similarly, `TranscriptDirector` aggregates a `TranscriptBuilder`. | `entity/Department.java`<br>`entity/Course.java`<br>`patterns/builder/TranscriptDirector.java` |
| **Association** | Structural link between two independent classes representing interactions. | 1. `Faculty` teaches `Course` (`@ManyToOne` link).<br>2. `Student` registers for `Course` via `Enrollment`.<br>3. `Grade` bridges `Enrollment` and `AssessmentComponent`.<br>4. `TimetableEntry` associates `Course`, `Classroom`, and `Faculty`.<br>5. `Notification` associates with recipient `UserAccount`. | `entity/Course.java`<br>`entity/Enrollment.java`<br>`entity/Grade.java`<br>`entity/TimetableEntry.java`<br>`entity/Notification.java` |

---

## 3. GoF Design Patterns Implemented

### 3.1 GoF State Pattern (Behavioral)
- **Problem Solved:** Prevents brittle nested `if-else` or `switch` statements across course registration workflows and strictly prevents illegal state transitions (e.g., dropping before enrolling, or skipping administrative approval).
- **Interface:** `EnrollmentState`
- **Abstract Base:** `AbstractEnrollmentState` (provides safe default exception throwing for illegal transitions).
- **Concrete States:**
  - `DraftState`: Initial student application.
  - `SubmittedState`: Locked application under administrative review.
  - `ApprovedState`: Prerequisites and capacity verified.
  - `EnrolledState`: Confirmed roster participant; attendance and grades enabled.
  - `DroppedState`: Student voluntarily withdrew or seat released.
  - `RejectedState`: Administrative application denial.
- **Factory:** `EnrollmentStateFactory` maps `EnrollmentStatus` enum to the appropriate state handler.
- **Code Locations:** `backend/src/main/java/com/campusconnect/patterns/state/`

### 3.2 GoF Strategy Pattern (Behavioral)
- **Problem Solved:** Decouples academic grading policies from domain entities and persistence layers, allowing different evaluation algorithms to be switched transparently at runtime.
- **Interface:** `GradingStrategy` (`GradingResult calculateGrade(BigDecimal marksObtained, BigDecimal maxMarks)`)
- **Concrete Strategy 1 (Phase 1):** `AbsoluteGradingStrategy` (criterion-referenced scale: A+ >= 90%, A >= 80%, B+ >= 75%, B >= 70%, C+ >= 65%, C >= 60%, D >= 50%, F < 50%).
- **Concrete Strategy 2 (Phase 2):** `RelativeGradingStrategy` (norm-referenced Gaussian bell curve: computes cohort mean $\mu$ and standard deviation $\sigma$, determining grades via Z-score: $Z \ge +1.5 \implies \text{A+}$, $Z \ge +1.0 \implies \text{A}$, $Z \ge +0.5 \implies \text{B+}$, $Z \ge 0.0 \implies \text{B}$, $Z \ge -0.5 \implies \text{C+}$, $Z \ge -1.0 \implies \text{C}$, $Z \ge -1.5 \implies \text{D}$, $Z < -1.5 \implies \text{F}$).
- **Strategy Factory:** `GradingStrategyFactory` dynamically selects strategy based on `GradingMode.ABSOLUTE` vs `GradingMode.RELATIVE`.
- **Code Locations:** `backend/src/main/java/com/campusconnect/patterns/strategy/`

### 3.3 GoF Observer Pattern (Behavioral)
- **Problem Solved:** Decouples event producers (Grade submission, Attendance marking, Enrollment approval) from consumers (Notification service, Student inboxes), preventing direct coupling and supporting asynchronous message distribution.
- **Subject / Event Publisher:** Spring Framework's `ApplicationEventPublisher`.
- **Domain Events:**
  - `GradePublishedEvent`: Emitted when faculty records a student assessment grade.
  - `AttendanceShortageEvent`: Emitted when a student's attendance drops below the statutory 75% threshold.
  - `EnrollmentApprovedEvent`: Emitted when admin approves a course registration.
- **Concrete Observer:** `AcademicEventListener` annotated with `@EventListener`, capturing events and delegating to `NotificationService` to persist persistent alerts.
- **Code Locations:** `backend/src/main/java/com/campusconnect/event/`, `backend/src/main/java/com/campusconnect/service/NotificationServiceImpl.java`

### 3.4 GoF Builder Pattern (Creational)
- **Problem Solved:** Constructing a complex, official, multi-part academic transcript report (header, student bio, semester-by-semester courses, credit calculations, weighted GPA, academic honors/standing, verification hashes, registrar signatures) step-by-step.
- **Product:** `AcademicTranscriptReport`
- **Builder Interface:** `TranscriptBuilder`
- **Concrete Builder:** `ConcreteTranscriptBuilder`
- **Director:** `TranscriptDirector` (encapsulates assembly sequence: `buildStudentInfo()`, `addCourseRecord()`, `buildAcademicSummary()`, `buildOfficialSignature()`).
- **Code Locations:** `backend/src/main/java/com/campusconnect/patterns/builder/`

### 3.5 Structural Adapter Pattern
- **Problem Solved:** Integrates the domain's `UserAccount` entity with Spring Security's required `UserDetails` framework contract.
- **Concrete Adapter:** `SecurityUser` implements `UserDetails`, wrapping `UserAccount`.
- **Code Location:** `backend/src/main/java/com/campusconnect/security/SecurityUser.java`

---

## 4. Algorithmic Invariants

### 4.1 Timetable Mathematical Interval Intersection Conflict Detection
To eliminate class scheduling conflicts, `TimetableService` enforces strict interval intersection checking:
$$\text{Conflict}(A, B) \iff [A.start < B.end] \land [A.end > B.start]$$
The system checks three independent conflict dimensions:
1. **Classroom Conflict**: Room $R$ cannot host two overlapping classes on day $D$.
2. **Faculty Conflict**: Instructor $F$ cannot teach two courses simultaneously.
3. **Course Conflict**: Course $C$ cannot be scheduled in multiple venues simultaneously.
4. **Capacity Validation**: Classroom capacity must satisfy course enrollment requirement ($room.capacity \ge course.capacity$).

---

## 5. SOLID Principles Traceability

| Principle | Description | Concrete Implementation in CampusConnect |
| :--- | :--- | :--- |
| **Single Responsibility Principle (SRP)** | A class should have one, and only one, reason to change. | Separate controllers, services, and repositories per bounded context: `EnrollmentController` handles HTTP routing, `EnrollmentService` enforces business rules, `EnrollmentRepository` executes queries, and `EnrollmentState` handles lifecycle rules. `AuditLogService` exclusively handles security and audit tracking. |
| **Open/Closed Principle (OCP)** | Software entities should be open for extension, but closed for modification. | New grading strategies (such as Relative Normal Distribution grading) were introduced without altering `Grade` entity or `GradeController`. New builders can be plugged into `TranscriptDirector` to emit PDF or XML transcripts. |
| **Liskov Substitution Principle (LSP)** | Subtypes must be substitutable for their base types without altering system correctness. | Any subclass of `Person` (`Student`, `Faculty`, `Admin`) can be handled polymorphically through `PersonRepository` and `UserAccount.getPerson()` without breaking invariants. |
| **Interface Segregation Principle (ISP)** | Clients should not be forced to depend upon interfaces they do not use. | Clean, specialized interfaces: `AuditLogService`, `NotificationService`, `TranscriptBuilder`, `GradingStrategy`, and dedicated Spring Data repository interfaces. |
| **Dependency Inversion Principle (DIP)** | High-level modules should depend on abstractions, not on concrete details. | Services depend on interface abstractions (`GradingStrategy`, `TranscriptBuilder`, `NotificationService`, `AuditLogService`) injected via Spring's IoC container rather than direct instantiation (`new`). |

---

## 6. University Evaluation Checklist & Verification Proofs

- [x] **Relational Integrity**: 3NF normalized schema with PostgreSQL and Flyway migrations `V1__init_core_schema.sql` and `V2__phase2_advanced_schema.sql`.
- [x] **Inheritance Proof**: JPA Joined table inheritance verified in `DomainModelAndRepositoryTest.testJoinedInheritanceHierarchy()`.
- [x] **Composition Proof**: Cascaded lifecycle of course assessments verified in `DomainModelAndRepositoryTest.testCourseAssessmentComposition()`.
- [x] **State Pattern Proof**: Enforced lifecycle transition and exception handling verified in `EnrollmentControllerTest.java`.
- [x] **Strategy Pattern Proof**: Dynamic calculation of letter grades under Absolute and Relative Bell Curve verified in `RelativeGradingStrategyTest.java`.
- [x] **Observer Pattern Proof**: Persistent notification generation on grade publication, attendance shortage, and enrollment approval verified in `AcademicEventListenerTest.java`.
- [x] **Builder Pattern Proof**: Academic transcript multi-part assembly with GPA and Dean's List standing verified in `TranscriptBuilderTest.java`.
- [x] **Timetable Conflict Detection Proof**: Interval intersection collision detection returning HTTP 409 Conflict verified in `TimetableControllerIntegrationTest.java` and `TimetableServiceTest.java`.
- [x] **Audit Trail Proof**: Immutable administrative activity tracking verified in `AuditAndSecurityIntegrationTest.java` and `AuditLogServiceTest.java`.
- [x] **Regression Suite**: 41 backend tests passing (100% success rate), clean frontend production build with zero TypeScript errors.
