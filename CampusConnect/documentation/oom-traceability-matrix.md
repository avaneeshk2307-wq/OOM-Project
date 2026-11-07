# CampusConnect — Object-Oriented Methodology (OOM) Traceability Matrix

This document provides complete traceability between theoretical Object-Oriented Programming (OOP) concepts, software design patterns, SOLID principles, and their concrete implementation within the **CampusConnect** codebase. It is designed to assist during academic evaluations, code walkthroughs, and viva examinations.

---

## 1. Core Object-Oriented Pillars

| OOP Concept | Architectural Purpose | Concrete Code Implementation | Source Location |
| :--- | :--- | :--- | :--- |
| **Abstraction** | Hide complex identity and lifecycle details while exposing uniform interfaces. | `Person` abstract class defines shared attributes (`name`, `email`, `phone`, `accountStatus`) without allowing direct instantiation. | `backend/src/main/java/com/campusconnect/entity/Person.java` |
| **Inheritance** | Eliminate code duplication and model genuine hierarchical domain relationships. | `Student`, `Faculty`, and `Admin` extend `Person` using JPA `@Inheritance(strategy = InheritanceType.JOINED)`. Subclasses inherit common identity while providing specialized domain state (`rollNumber`, `employeeCode`, `roleTitle`). | `Person.java`, `Student.java`, `Faculty.java`, `Admin.java` |
| **Encapsulation** | Protect class invariants by hiding internal representation and controlling mutations through business methods. | Non-negative credit checks in `Course.setCredits()`, non-empty invariants, state transitions through `Enrollment.submit()`, and `Grade.setMarks()` validating against `assessment.maxMarks`. | `Course.java`, `Enrollment.java`, `Grade.java` |
| **Polymorphism** | Dynamic dispatch of behavior at runtime depending on the underlying concrete object type. | 1. JPA polymorphic querying via `PersonRepository.findById()` returning either `Student` or `Faculty`.<br>2. Polymorphic state transitions in `EnrollmentState.submit()`, `approve()`, etc.<br>3. Pluggable grade evaluation in `GradingStrategy.calculateGrade()`. | `DomainModelAndRepositoryTest.java`, `patterns/state/*`, `patterns/strategy/*` |

---

## 2. Advanced Object Relationships

| Relationship Type | Definition & Invariant | Concrete Implementation | Source Location |
| :--- | :--- | :--- | :--- |
| **Composition** | Strong "part-of" relationship where child objects cannot exist independently of the parent aggregate root (cascaded persistence & orphan removal). | 1. `Course` owns `AssessmentComponent`: Deleting a course permanently removes all associated assessments (`cascade = ALL, orphanRemoval = true`).<br>2. `AttendanceSession` owns `AttendanceRecord`: The session aggregate root controls adding and updating student records through `recordStudentAttendance()`. | `Course.java`<br>`AttendanceSession.java`<br>`AttendanceRecord.java` |
| **Aggregation** | "Has-a" relationship with independent lifecycles. Child objects survive if parent container is deleted. | `Department` aggregates `Course`, `Student`, and `Faculty`. Deleting a department does not destroy the physical people or courses; it unassigns the organizational link. | `Department.java`<br>`Course.java` |
| **Association** | Structural link between two independent classes representing interactions. | 1. `Faculty` teaches `Course` (`@ManyToOne` link).<br>2. `Student` registers for `Course` via `Enrollment`.<br>3. `Grade` bridges `Enrollment` and `AssessmentComponent`. | `Course.java`<br>`Enrollment.java`<br>`Grade.java` |

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
- **Concrete Strategy (Phase 1):** `AbsoluteGradingStrategy` (criterion-referenced scale: A+ >= 90%, A >= 80%, B+ >= 75%, B >= 70%, C+ >= 65%, C >= 60%, D >= 50%, F < 50%).
- **Phase 2 Extensibility:** Allows seamless addition of `RelativeGradingStrategy` (norm-referenced bell curve) adhering to the Open-Closed Principle without modifying a single line of existing code.
- **Code Locations:** `backend/src/main/java/com/campusconnect/patterns/strategy/`

### 3.3 Adapter Pattern (Structural)
- **Problem Solved:** Integrates the domain's `UserAccount` entity with Spring Security's required `UserDetails` framework contract.
- **Concrete Adapter:** `SecurityUser` implements `UserDetails`, wrapping `UserAccount`.
- **Code Location:** `backend/src/main/java/com/campusconnect/security/SecurityUser.java`

---

## 4. SOLID Principles Traceability

| Principle | Description | Concrete Implementation in CampusConnect |
| :--- | :--- | :--- |
| **Single Responsibility Principle (SRP)** | A class should have one, and only one, reason to change. | Separate controllers, services, and repositories per bounded context: `EnrollmentController` handles HTTP routing, `EnrollmentService` enforces business rules, `EnrollmentRepository` executes queries, and `EnrollmentState` handles lifecycle rules. |
| **Open/Closed Principle (OCP)** | Software entities should be open for extension, but closed for modification. | New grading strategies (such as Relative Grading in Phase 2) or new enrollment states can be introduced by implementing interfaces without modifying existing evaluation code. |
| **Liskov Substitution Principle (LSP)** | Subtypes must be substitutable for their base types without altering system correctness. | Any subclass of `Person` (`Student`, `Faculty`, `Admin`) can be handled polymorphically through `PersonRepository` and `UserAccount.getPerson()` without breaking invariants. |
| **Interface Segregation Principle (ISP)** | Clients should not be forced to depend upon interfaces they do not use. | Clean, specialized Spring Data repository interfaces (`AttendanceSessionRepository`, `GradeRepository`, `AnnouncementRepository`) providing only relevant query methods. |
| **Dependency Inversion Principle (DIP)** | High-level modules should depend on abstractions, not on concrete details. | Services depend on interface abstractions (`GradingStrategy`, `CourseRepository`, `EnrollmentService`) injected via Spring's IoC container rather than direct instantiation (`new`). |

---

## 5. University Evaluation Checklist

- [x] **Relational Integrity**: 3NF normalized schema with PostgreSQL and Flyway migration `V1__init_core_schema.sql`.
- [x] **Inheritance Proof**: JPA Joined table inheritance verified in `DomainModelAndRepositoryTest.testJoinedInheritanceHierarchy()`.
- [x] **Composition Proof**: Cascaded lifecycle of course assessments verified in `DomainModelAndRepositoryTest.testCourseAssessmentComposition()`.
- [x] **State Pattern Proof**: Enforced lifecycle transition and exception handling verified in `EnrollmentControllerTest.java`.
- [x] **Strategy Pattern Proof**: Dynamic calculation of letter grades and grade points verified in `GradeControllerTest.java`.
- [x] **Attendance Warning Proof**: Verification of 75% shortage alert logic tested in `AttendanceControllerTest.testStudentAttendanceSummaryWithLowAttendanceWarning()`.
