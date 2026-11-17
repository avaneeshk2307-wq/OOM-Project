# CampusConnect: Object-Oriented Methodology (OOM) Concept Mapping & Academic Traceability

**Document Version:** 2.0.0  
**Academic Subject:** Object-Oriented Methodology (OOM) / Software Engineering  
**Project:** CampusConnect — Smart Campus Management System  
**Team Composition:** Three (3) Student Members  

---

## 1. Overview & Verification Baseline

This document provides a rigorous, verifiable mapping between theoretical Object-Oriented Methodology (OOM) axioms, Gang of Four (GoF) design patterns, SOLID principles, and their concrete implementation within the **CampusConnect** codebase.

All mappings in this document reference actual, compiling Java classes in `CampusConnect/backend/src/main/java/com/campusconnect`.

---

## 2. Four Pillars of Object-Oriented Programming

| Pillar | Academic Definition | Concrete Implementation Class | Source File Path | Architectural Justification & Business Invariant |
|:---|:---|:---|:---|:---|
| **Encapsulation** | Bundling of data and methods that operate on that data within a single unit, hiding internal state representations through strict access modifiers (`private`, `protected`) and enforcing domain business invariants via controlled mutators. | `Student.java`, `Course.java`, `Enrollment.java` | `backend/src/main/java/com/campusconnect/entity/Student.java` | Internal fields (`semester`, `enrollments`) are strictly private. The method `promoteToNextSemester()` enforces the academic invariant that a student cannot exceed semester 12. |
| **Inheritance** | Mechanism where a specialized subclass acquires all fields and behaviors of a generalized superclass, promoting code reuse and establishing an *is-a* conceptual hierarchy. | `Person.java` $\to$ `Student.java`, `Faculty.java`, `Admin.java` | `backend/src/main/java/com/campusconnect/entity/Person.java` | Relational JPA `@Inheritance(strategy = InheritanceType.JOINED)` maps common person attributes (`name`, `email`, `phone`, `accountStatus`) to the base table `persons` while extending specialized role tables. |
| **Abstraction** | Representing essential features of a real-world entity without including background implementation details, establishing a contract for derived classes. | `Person.java` (Abstract Class), `EnrollmentState.java` (Interface), `GradingStrategy.java` (Interface) | `backend/src/main/java/com/campusconnect/patterns/strategy/GradingStrategy.java` | `GradingStrategy` abstracts grade computation into a clean method contract: `GradingResult evaluate(double score, List<Double> cohortScores)`. The calling service has zero awareness of mathematical algorithms. |
| **Polymorphism** | Ability of different classes to respond to the same message or method call in class-specific ways, resolved dynamically at runtime (Dynamic Method Dispatch). | `AbsoluteGradingStrategy.java`, `RelativeGradingStrategy.java`, `AbstractEnrollmentState.java` | `backend/src/main/java/com/campusconnect/patterns/strategy/RelativeGradingStrategy.java` | When `GradeServiceImpl` calls `strategy.evaluate()`, Java dynamically executes either the criterion-based threshold algorithm or the Gaussian normal distribution curve based on the course configuration. |

---

## 3. Object-Oriented Structural Relationships

| Relationship | Type | Multiplicity | Source Entity | Target Entity | Code Implementation Evidence |
|:---|:---|:---:|:---|:---|:---|
| **Inheritance (*is-a*)** | Generalization | N/A | `Student`, `Faculty`, `Admin` | `Person` | `public class Student extends Person { ... }` with `@PrimaryKeyJoinColumn(name = "id")`. |
| **Composition (*part-of, strict lifetime*)** | Strong Composition | $1 \to 1..*$ | `Course` | `AssessmentComponent` | `@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)` — Deleting a course permanently cascades to its assessment breakdown. |
| **Composition (*part-of, strict lifetime*)** | Strong Composition | $1 \to 0..*$ | `AttendanceSession` | `AttendanceRecord` | `@OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)` — Student attendance records cannot exist without an active attendance session. |
| **Aggregation (*has-a, shared lifetime*)** | Shared Aggregation | $1 \to 0..*$ | `Department` | `Faculty` | `@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "department_id")` — Faculty members belong to a department, but their employment lifecycle exists independently. |
| **Aggregation (*has-a, shared lifetime*)** | Shared Aggregation | $1 \to 0..*$ | `Department` | `Course` | `@ManyToOne @JoinColumn(name = "department_id")` — Courses are categorized under departments, but survive academic department reorganizations. |
| **Association (*uses-a, structural link*)** | Bidirectional Link | $1 \to 0..*$ | `Student` | `Enrollment` | `@OneToMany(mappedBy = "student")` associates a student with their course enrollment records. |
| **Dependency (*transient usage*)** | Usage Dependency | N/A | `AdminStudentController` | `StudentService` | Constructor injection establishes an operational dependency where the controller relies on the service interface. |
| **Dependency (*transient usage*)** | Usage Dependency | N/A | `GradingStrategyFactory` | `GradingStrategy` | The factory instantiates and returns a transient `GradingStrategy` instance based on `GradingScheme`. |

---

## 4. Gang of Four (GoF) Design Patterns

```
+--------------------------------------------------------------------------------------------------+
| DESIGN PATTERNS IMPLEMENTED IN CAMPUSCONNECT                                                     |
+-------------------+-----------------------------------+------------------------------------------+
| Pattern Name      | Concrete Classes Involved         | Architectural Purpose in CampusConnect   |
+-------------------+-----------------------------------+------------------------------------------+
| State Pattern     | EnrollmentState (Interface)       | Encapsulates enrollment workflow rules.  |
| (Behavioral)      | AbstractEnrollmentState (Base)    | Prevents illegal transitions (e.g.       |
|                   | DraftState, SubmittedState,       | DROPPED -> APPROVED). Eliminates deep    |
|                   | ApprovedState, EnrolledState,     | if-else conditional anti-patterns.       |
|                   | DroppedState, RejectedState       |                                          |
+-------------------+-----------------------------------+------------------------------------------+
| Strategy Pattern  | GradingStrategy (Interface)       | Decouples academic grade evaluation from |
| (Behavioral)      | AbsoluteGradingStrategy           | core grade services. Enables dynamic     |
|                   | RelativeGradingStrategy           | switching between absolute criterion     |
|                   | GradingStrategyFactory            | and Gaussian Z-score bell curves.        |
+-------------------+-----------------------------------+------------------------------------------+
| Observer Pattern  | AcademicEventListener             | Decouples event triggers from secondary  |
| (Behavioral)      | GradePublishedEvent               | actions. Dispatches persistent in-app    |
|                   | AttendanceShortageEvent           | notifications when grades publish or     |
|                   | EnrollmentApprovedEvent           | attendance drops below 75%.              |
+-------------------+-----------------------------------+------------------------------------------+
| Builder Pattern   | TranscriptBuilder (Interface)     | Assembles multi-part, credentialed       |
| (Creational)      | ConcreteTranscriptBuilder         | academic transcript objects (student     |
|                   | TranscriptDirector                | profile, semester courses, GPA, honours) |
|                   | AcademicTranscriptReport (Product)| through a step-by-step pipeline.         |
+-------------------+-----------------------------------+------------------------------------------+
| Factory Pattern   | GradingStrategyFactory            | Centralizes conditional instantiation    |
| (Creational)      | EnrollmentStateFactory            | of polymorphic strategies and states.    |
+-------------------+-----------------------------------+------------------------------------------+
| Repository        | 17 Spring Data Interfaces:        | Abstracts data access and SQL operations |
| (Architectural)   | StudentRepository, CourseRepo,    | behind clean object-oriented collection  |
|                   | EnrollmentRepository, etc.        | semantics with custom JPQL queries.      |
+-------------------+-----------------------------------+------------------------------------------+
```

---

## 5. SOLID Principles Evaluation

1. **Single Responsibility Principle (SRP):**
   - *Status:* **Fully Followed**
   - *Evidence:* Domain entities handle state invariants; service implementations (`StudentServiceImpl`) handle business orchestration; controllers handle HTTP protocol dispatch; repositories handle relational persistence.

2. **Open/Closed Principle (OCP):**
   - *Status:* **Fully Followed**
   - *Evidence:* New grading algorithms (e.g., standard percentile grading, Pass/Fail) can be introduced by creating a new `GradingStrategy` implementation without modifying `GradeServiceImpl` or existing strategy classes.

3. **Liskov Substitution Principle (LSP):**
   - *Status:* **Fully Followed**
   - *Evidence:* Any service or repository expecting a `Person` can accept a `Student`, `Faculty`, or `Admin` without altering the correctness of program execution. All subclasses honor the contracts established in `Person`.

4. **Interface Segregation Principle (ISP):**
   - *Status:* **Partially Followed**
   - *Evidence:* Pattern interfaces (`GradingStrategy`, `TranscriptBuilder`, `EnrollmentState`) are highly segregated and focused. However, service interfaces (`StudentService`, `CourseService`) combine multiple CRUD and reporting responsibilities into single contracts rather than segregating read and write operations.

5. **Dependency Inversion Principle (DIP):**
   - *Status:* **Fully Followed**
   - *Evidence:* High-level controllers and services depend upon abstract interfaces (`StudentService`, `EnrollmentRepository`, `GradingStrategy`), not concrete implementations. Spring IoC container injects concrete instances at runtime.
