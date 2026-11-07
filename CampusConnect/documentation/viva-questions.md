# CampusConnect — Comprehensive Viva Voce Questions & Answers
## Object-Oriented Methodology (OOM) Examination Guide

This document contains 32 comprehensive Viva Voce questions and detailed answers designed to assist in defending the **CampusConnect** project during university evaluations and technical reviews.

---

### Part I: Core Object-Oriented Methodology (OOM) & OOP Concepts

#### Q1: What is the core architectural philosophy of CampusConnect?
**Answer:** CampusConnect is architected using **Clean Architecture** and pure **Object-Oriented Methodology (OOM)**. The domain logic is decoupled from web delivery and persistence mechanisms. Bounded contexts (curriculum, registration, attendance, grading, timetabling, auditing) are modeled through rich domain entities encapsulating business invariants rather than anemic data structures.

#### Q2: How did you implement Abstraction in CampusConnect?
**Answer:** We implemented Abstraction at multiple architectural layers:
1. **Domain Layer:** The abstract class `Person` encapsulates common attributes (`name`, `email`, `phone`, `accountStatus`) and lifecycle behavior (`activate()`, `deactivate()`, `updateContactInfo()`) without allowing direct instantiation. Subclasses `Student`, `Faculty`, and `Admin` provide concrete domain specializations.
2. **Design Pattern Layer:** The `TranscriptBuilder` interface defines high-level document construction steps (`buildStudentInfo()`, `addCourseRecord()`, `buildAcademicSummary()`, `buildOfficialSignature()`), hiding underlying JSON string assembly, formatting, and buffer manipulation from callers.

#### Q3: Which JPA inheritance strategy did you choose for `Person`, and why?
**Answer:** We chose **Joined-Table Inheritance** (`@Inheritance(strategy = InheritanceType.JOINED)`). In this strategy, the base entity `Person` maps to the `persons` table, while `Student`, `Faculty`, and `Admin` map to their own tables (`students`, `faculty`, `admins`) containing only subtype-specific columns and sharing the primary key `id` as a foreign key to `persons`.
- *Why not Single Table?* Single Table creates nullable columns for subtype-specific attributes, violating 3NF and database integrity constraints.
- *Why not Table Per Class?* Table Per Class duplicates base columns across tables and makes polymorphic queries across all persons slow due to SQL `UNION` operations.

#### Q4: How is Encapsulation enforced in your entities?
**Answer:** All entity fields are declared `private` or `protected`. State modifications are controlled through semantic business methods that validate invariants rather than raw setters:
- `Course.setCredits(int credits)` ensures credits are positive ($> 0$).
- `Grade.setMarksObtained(BigDecimal marks)` validates that marks do not exceed `assessment.maxMarks`.
- `Enrollment.submit()`, `approve()`, and `drop()` delegate state transitions to the `EnrollmentState` machine rather than arbitrary string assignments.
- DTOs are declared as Java `records`, ensuring immutability across HTTP boundaries.

#### Q5: Give an example of Polymorphism in CampusConnect.
**Answer:**
1. **Dynamic Strategy Polymorphism:** `GradingStrategy.calculateGrade()` dynamically resolves at runtime to either `AbsoluteGradingStrategy` or `RelativeGradingStrategy` based on user configuration, yielding distinct evaluations through an identical interface.
2. **State Polymorphism:** In the course enrollment lifecycle, calling `state.approve(enrollment)` executes completely different validation logic depending on whether `state` is currently an instance of `RequestedState`, `ApprovedState`, or `EnrolledState`.

#### Q6: Explain the difference between Composition and Aggregation using concrete classes from your project.
**Answer:**
- **Composition ("Part-of" with tied lifecycle):** The relationship between `Course` and `AssessmentComponent`. An assessment component has no meaningful existence outside its course. In JPA, `Course` owns `assessments` with `cascade = CascadeType.ALL` and `orphanRemoval = true`. Deleting a course permanently removes all its assessments. Similarly, `AttendanceSession` composes `AttendanceRecord`.
- **Aggregation ("Has-a" with independent lifecycle):** The relationship between `Department` and `Faculty` or `Student`. A department aggregates teachers and students. If a department is reorganized or deleted, the faculty members and students continue to exist in the university. In `patterns/builder`, `TranscriptDirector` aggregates a `TranscriptBuilder`.

---

### Part II: Gang of Four (GoF) Design Patterns

#### Q7: Why did you use the State Pattern for Course Enrollment instead of simple `if-else` statements?
**Answer:** Course registration involves multiple interdependent stages (*Draft*, *Submitted*, *Approved*, *Enrolled*, *Dropped*, *Rejected*). Handling transitions using conditional logic (`if (status == SUBMITTED) ... else if (status == APPROVED) ...`) creates high cyclomatic complexity, tight coupling, and vulnerability to illegal transitions (such as dropping before enrolling).
The **GoF State Pattern** encapsulates state-specific behavior in discrete classes implementing `EnrollmentState`. Each state explicitly defines valid outgoing transitions and inherits default exception handling from `AbstractEnrollmentState`, making illegal operations structurally impossible.

#### Q8: How did you implement the Strategy Pattern for grading?
**Answer:** Grading policies vary across departments and courses. We declared a common interface `GradingStrategy` with method `calculateGrade(BigDecimal marksObtained, BigDecimal maxMarks)`. We then implemented two concrete strategies:
1. `AbsoluteGradingStrategy`: Standard criterion-referenced grading (90%+ A+, 80%+ A, etc.).
2. `RelativeGradingStrategy`: Norm-referenced Gaussian normal curve calculation based on cohort mean $\mu$, standard deviation $\sigma$, and standardized Z-scores.
The `GradingStrategyFactory` provides the appropriate strategy dynamically based on the request's `GradingMode`. This adheres strictly to the Open-Closed Principle (OCP).

#### Q9: Explain how the Observer Pattern operates in your application.
**Answer:** The Observer Pattern decouples domain event producers from alert sinks:
- **Event Producers:** When faculty submit grades (`GradeServiceImpl`), when attendance drops below 75% (`AttendanceServiceImpl`), or when admins approve enrollment (`EnrollmentServiceImpl`), they publish domain events using Spring's `ApplicationEventPublisher`.
- **Concrete Observer:** `AcademicEventListener` listens for these events via `@EventListener` methods (`onGradePublished`, `onAttendanceShortage`, `onEnrollmentApproved`).
- **Notification Persistence:** The observer invokes `NotificationService` to persist tamper-evident records into the `notifications` database table and alerts the student's top-bar notification bell in real time.

#### Q10: Why was the Builder Pattern necessary for Academic Transcripts?
**Answer:** An official university transcript is a composite, multi-part document comprising student biographical data, multi-semester course records, credit calculations, cumulative weighted GPA math, academic standing evaluations (e.g. "Dean's List", "Good Standing", "Probation"), and registrar verification signatures.
Using constructor telescoping or flat factory methods would result in confusing constructors with dozens of arguments. The **GoF Builder Pattern** (`TranscriptBuilder`, `ConcreteTranscriptBuilder`) constructs the document step-by-step, coordinated by `TranscriptDirector`.

#### Q11: Did you use the Adapter Pattern? Where?
**Answer:** Yes. Spring Security requires an implementation of the framework's `UserDetails` interface. To avoid polluting our clean domain entity `UserAccount` with framework-specific methods (`getAuthorities()`, `isAccountNonLocked()`), we implemented `SecurityUser` as a Structural Adapter wrapping `UserAccount`.

---

### Part III: SOLID Principles

#### Q12: How does CampusConnect satisfy the Single Responsibility Principle (SRP)?
**Answer:** Every class has exactly one reason to change:
- `TimetableController` only handles HTTP requests and serialization.
- `TimetableService` enforces scheduling interval conflict logic.
- `TimetableRepository` manages database queries.
- `AuditLogService` exclusively handles security logging without mixing domain business logic.

#### Q13: How does your grading engine adhere to the Open/Closed Principle (OCP)?
**Answer:** The grading module is open for extension but closed for modification. When we introduced norm-referenced Relative Bell Curve grading in Phase 2, we created `RelativeGradingStrategy` without modifying a single line of `AbsoluteGradingStrategy` or breaking existing grade tests.

#### Q14: Explain Liskov Substitution Principle (LSP) in your domain model.
**Answer:** Any method expecting a `Person` reference can receive a `Student`, `Faculty`, or `Admin` instance without altering correctness. Polymorphic methods on `Person` (such as `activate()` or `updateContactInfo()`) work consistently across all subclasses without throwing unexpected type-cast exceptions.

#### Q15: How is the Interface Segregation Principle (ISP) demonstrated?
**Answer:** Rather than creating a single bloated "UniversityService" interface, we created focused, domain-specific interfaces: `AuditLogService`, `NotificationService`, `TranscriptBuilder`, and distinct Spring Data repositories (`ClassroomRepository`, `TimetableRepository`, `GradeRepository`).

#### Q16: How does Dependency Inversion Principle (DIP) apply to CampusConnect?
**Answer:** High-level services do not depend on low-level database implementations. Instead, services depend on interface abstractions (`GradingStrategy`, `TranscriptBuilder`, `AuditLogService`, JPA repository interfaces) injected via Spring's Inversion of Control (IoC) container.

---

### Part IV: Algorithmic Logic & Business Rules

#### Q17: What is the mathematical formula for Timetable Interval Conflict Detection?
**Answer:** Two time intervals $[start_A, end_A]$ and $[start_B, end_B]$ collide if and only if:
$$\text{Conflict}(A, B) \iff (start_A < end_B) \land (end_A > start_B)$$
`TimetableService` executes this check across three dimensions on the target day of the week:
1. Same classroom.
2. Same faculty member.
3. Same course section.
If any conflict exists, the system halts execution with a `TimetableConflictException` (HTTP 409 Conflict).

#### Q18: How does the Relative Bell Curve grading algorithm compute student grades?
**Answer:**
1. Compute the cohort arithmetic mean: $\mu = \frac{1}{n} \sum x_i$
2. Compute variance and standard deviation: $\sigma = \sqrt{\frac{1}{n} \sum (x_i - \mu)^2}$
3. For each student, compute the Z-score: $Z = \frac{x_i - \mu}{\sigma}$
4. Map the Z-score to standardized grade boundaries:
   - $Z \ge +1.5\sigma \implies \text{A+}$
   - $+1.0\sigma \le Z < +1.5\sigma \implies \text{A}$
   - $+0.5\sigma \le Z < +1.0\sigma \implies \text{B+}$
   - $0.0\sigma \le Z < +0.5\sigma \implies \text{B}$
   - $-0.5\sigma \le Z < 0.0\sigma \implies \text{C+}$
   - $-1.0\sigma \le Z < -0.5\sigma \implies \text{C}$
   - $-1.5\sigma \le Z < -1.0\sigma \implies \text{D}$
   - $Z < -1.5\sigma \implies \text{F}$

#### Q19: How is the 75% attendance shortage threshold monitored?
**Answer:** Whenever faculty records an attendance session in `AttendanceServiceImpl`, the system queries all historical records for the enrolled student in that course. If $\frac{\text{attended sessions}}{\text{total conducted sessions}} < 0.75$, the system triggers an `AttendanceShortageEvent`, notifying the student of mandatory disciplinary attendance shortages.

---

### Part V: Database Design & Normalization

#### Q20: Prove that your database schema satisfies Third Normal Form (3NF).
**Answer:**
1. **1NF (First Normal Form):** All attributes contain atomic scalar values (no repeating groups, comma-separated lists, or arrays). Every table has a designated primary key.
2. **2NF (Second Normal Form):** Satisfies 1NF and contains no partial functional dependencies. In composite-key tables like `enrollments` or `grades`, all non-key attributes depend on the complete key.
3. **3NF (Third Normal Form):** Satisfies 2NF and contains no transitive functional dependencies ($X \to Y \to Z$). For example, student department names are not stored in the `students` table; only the foreign key `department_id` is stored, referencing the `departments` table.

#### Q21: What composite unique constraints did you define, and why?
**Answer:**
- `enrollments`: Unique constraint on `(student_id, course_id)` prevents a student from registering for the same course twice in the same semester.
- `attendance_records`: Unique constraint on `(session_id, student_id)` prevents multiple attendance entries for the same student in a single lecture session.
- `grades`: Unique constraint on `(enrollment_id, assessment_id)` prevents duplicate marks for the same quiz or exam.

---

### Part VI: Security, Auditability & Architecture

#### Q22: How does stateless JWT authentication work in CampusConnect?
**Answer:** When a user logs in via `POST /api/v1/auth/login`, `AuthServiceImpl` verifies credentials using BCrypt. Upon success, `JwtTokenProvider` generates a cryptographically signed HMAC-SHA512 token containing the username, role, issued timestamp, and 24-hour expiration. On subsequent requests, `JwtAuthenticationFilter` intercepts the HTTP header, validates the signature, and sets the authenticated principal in Spring's `SecurityContextHolder`.

#### Q23: How do you prevent unauthorized administrative actions?
**Answer:** We enforce defense-in-depth:
1. **URL-level security:** `SecurityConfig` permits `/api/v1/auth/**` and public landing endpoints while requiring authentication for all `/api/v1/**` paths.
2. **Method-level security:** Controllers use `@PreAuthorize("hasRole('ROLE_ADMIN')")` or `@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FACULTY')")` to restrict endpoints at the Java reflection level.
3. **Frontend Guards:** React router wraps protected routes with `<RoleGuard allowedRoles={[...]} />`.

#### Q24: What is the purpose of the Audit Log system, and how is it implemented?
**Answer:** The `audit_logs` table provides a tamper-evident administrative record. When critical operations occur (creating students, assigning faculty, submitting grades, marking attendance), `AuditLogService.recordLog()` is invoked within an isolated transaction (`@Transactional(propagation = Propagation.REQUIRES_NEW)`), saving the action type, actor username, affected entity, client IP address, and payload description.

---

### Part VII: Frontend Engineering & UI/UX

#### Q25: Why choose React 18 with TypeScript over plain JavaScript?
**Answer:** TypeScript enforces compile-time type safety across API response contracts (`StudentResponse`, `TimetableResponse`, `CampusAnalyticsResponse`). It prevents common runtime bugs like null property access (`Cannot read properties of undefined`) and guarantees data consistency across components.

#### Q26: How does the official transcript print feature work?
**Answer:** In `OfficialTranscriptModal.tsx`, the transcript is structured with specific CSS media queries (`@media print`). When the user clicks "Print Official Transcript", `window.print()` triggers the browser's native print engine. Non-printable navigation bars and buttons are automatically hidden (`print:hidden`), rendering a clean, watermarked document formatted for A4 paper.

#### Q27: How are interactive analytics rendered?
**Answer:** `AdminAnalytics.tsx` integrates the **Recharts** library. It consumes aggregate institutional metrics from `GET /api/v1/analytics/overview` and dynamically renders:
- Bar charts of department student enrollment distribution.
- Pie charts of campus-wide grade allocations (A+, A, B, etc.).
- Occupancy gauges for course seat utilization.
- Faculty teaching load and credit summaries.

---

### Part VIII: Testing & Quality Assurance

#### Q28: How do you know your system does not have regressions?
**Answer:** We executed the automated regression test suite using Maven (`.\mvnw.cmd test`), running 41 automated tests encompassing unit tests for design patterns, service business logic, JPA repository hierarchies, MockMvc controller tests, and security integration tests. All 41 tests passed with **0 errors, 0 failures, and 0 skipped tests**.

#### Q29: What was the hardest bug you resolved during Phase 2 development?
**Answer:** When testing `AcademicEventListenerTest` on Java 26 EA, ByteBuddy class retransformation failed because Mockito was trying to mock a concrete Spring service class (`NotificationService`). We resolved this by applying the **Interface Segregation Principle**: we extracted a clean `NotificationService` interface and implemented `NotificationServiceImpl`. Mockito was then able to mock the interface seamlessly without ByteBuddy reflection issues.

#### Q30: How did you test timetable conflicts?
**Answer:** In `TimetableControllerIntegrationTest.java`, we scheduled an initial lecture on Monday from 10:00 to 11:30 in room `LH-999`. We then issued a second request attempting to schedule an overlapping class in the same room on Monday from 11:00 to 12:30. The test asserted that the backend responded with **HTTP 409 Conflict** and an appropriate conflict explanation.

#### Q31: How does the system handle concurrent course registrations when only 1 seat remains?
**Answer:** `EnrollmentServiceImpl.finalizeEnrollment()` queries the count of currently enrolled students and executes `course.hasAvailableSeats(enrolledCount)` inside an ACID database transaction with JPA pessimistic locking on the target course, preventing race conditions.

#### Q32: What makes CampusConnect suitable for university-level evaluation?
**Answer:** CampusConnect is not a superficial mockup or frontend-only demonstration. It is a fully functional enterprise web application with actual database persistence, mathematically proven conflict detection algorithms, rigorous adherence to all 4 OOP pillars and 5 SOLID principles, and concrete implementations of 4 GoF design patterns backed by automated test suites and complete UML documentation.

---
*End of Viva Voce Preparation Guide.*
