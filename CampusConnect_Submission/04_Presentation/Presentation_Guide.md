# CampusConnect: Presentation & Viva Voce Guide (3-Member Academic Team)

**Project Title:** CampusConnect — Smart Campus Management System  
**Academic Subject:** Object-Oriented Methodology (OOM)  
**Slide Deck:** `CampusConnect_OOM_Presentation.pptx` (18 Slides, 16:9 Widescreen)  
**Team Composition:** Three (3) Student Members  
**Target Duration:** 18 – 20 Minutes (followed by 10 minutes of Viva Voce Q&A)  

---

## 1. Team Presentation Structure & Time Allocation

| Slide # | Slide Title | Primary Presenter | Duration | Core Academic Focus |
|:---:|:---|:---:|:---:|:---|
| **1** | Title Slide & Candidate Introduction | **Student 1** | 1.0 min | Team intro, institution, guide, and project scope. |
| **2** | Problem Statement & Motivation | **Student 1** | 1.0 min | Fragmentation, manual errors, timetable clashes. |
| **3** | System Objectives & Scope | **Student 1** | 1.0 min | Role governance, automated state machines, boundary limits. |
| **4** | Existing vs. Proposed System Comparison | **Student 1** | 1.0 min | Paper/Excel vs unified ACID PostgreSQL + RBAC. |
| **5** | SDLC Methodology & Phased Plan | **Student 1** | 1.0 min | Iterative Hybrid model justification, Phase 1 & 2 roadmap. |
| **6** | Software Requirements Specification (SRS) | **Student 1** | 1.0 min | FR-01 to FR-16, NFRs (Security, Performance, Integrity). |
| **7** | System Architecture & Modern Tech Stack | **Student 2** | 1.0 min | Layered Modular Monolith (React 18 + Spring Boot 3.3.5). |
| **8** | Use Case Diagram: Actors & Boundaries | **Student 2** | 1.0 min | Admin, Faculty, Student actors and system boundary. |
| **9** | Class Diagram: Structural Relationships | **Student 2** | 1.5 min | `Person` hierarchy, associations, multiplicity, patterns. |
| **10** | Four Pillars of OOP in CampusConnect | **Student 2** | 1.5 min | Encapsulation, Inheritance, Abstraction, Polymorphism. |
| **11** | Object Relationships & GoF Design Patterns| **Student 2** | 1.5 min | Composition, Aggregation, State, Strategy, Builder, Observer. |
| **12** | Sequence & Activity Diagrams | **Student 2** | 1.0 min | JWT authentication dispatch & Course enrollment flow. |
| **13** | Database Architecture & ER Diagram | **Student 3** | 1.0 min | 17 normalized tables, 3NF, JPA Joined Inheritance. |
| **14** | Implementation & Technology Stack | **Student 3** | 1.0 min | Java 21, Spring Security 6.3, JJWT, TypeScript 5.6. |
| **15** | Main Modules & Role-Based Portals | **Student 3** | 1.0 min | Admin (/admin), Faculty (/faculty), Student (/student). |
| **16** | Testing & Quality Assurance (100% Pass) | **Student 3** | 1.5 min | 112 empirical tests (57 backend, 18 UI, 36 API), IDOR fixes. |
| **17** | 3-Member Matrix, Maintenance & Scope | **Student 3** | 1.0 min | Contribution matrix, 4 maintenance types, future roadmap. |
| **18** | Conclusion & Viva Voce Defense | **All Members** | 1.0 min | Academic summary, deliverables sign-off, viva invitation. |
| **Total** | **Full Group Presentation** | **3 Members** | **~18.5 min** | **Comprehensive, disciplined academic defense** |

---

## 2. Slide-by-Slide Speaker Scripts

### Part I: Foundations & Requirements (Presenter: Student Member 1)

#### Slide 1: Title Slide & Introduction
- **Speaker Script:**  
  *"Respected members of the examination committee, esteemed project guide [Faculty Guide Name], and faculty members. Good morning/afternoon. We are Team CampusConnect, comprising [Student 1 Name], [Student 2 Name], and [Student 3 Name]. Today, we present the analysis, design, implementation, and quality assurance audit of our Object-Oriented Methodology project: **CampusConnect — Smart Campus Management System**."*

#### Slide 2: Problem Statement & Motivation
- **Speaker Script:**  
  *"Contemporary academic institutions often manage operations using fragmented software and spreadsheets. This leads to severe friction: timetable double-booking, enrollment beyond classroom capacity, delayed notification of attendance deficits, and critical security vulnerabilities like Insecure Direct Object References (IDOR). CampusConnect was engineered to solve these challenges using formal Object-Oriented Design principles."*

#### Slide 3: System Objectives & Scope
- **Speaker Script:**  
  *"Our objectives were twofold: First, create a secure, role-governed platform for Administrators, Faculty, and Students. Second, automate complex academic rules—such as interval-based timetable clash detection, 6-stage enrollment workflows, and plug-and-play grading schemes. Ancillary functions like tuition payments and physical biometric devices were delimited as out-of-scope to focus rigorously on core academic governance."*

#### Slide 4: Existing vs. Proposed System
- **Speaker Script:**  
  *"As summarized in our comparison matrix, legacy systems rely on manual coordination with zero auditability. CampusConnect introduces automated validation via state machines, mathematical interval clash detection, and an immutable administrative audit interceptor backed by an ACID-compliant PostgreSQL database."*

#### Slide 5: SDLC Methodology
- **Speaker Script:**  
  *"We adopted an Iterative Hybrid model. Pure Waterfall is too rigid for real academic edge cases, while pure Agile lacks the formal upfront UML documentation required in academic engineering. We structured development into two phases: Phase 1 Core System Delivery, followed by Phase 2 Advanced Patterns (State, Strategy, Observer, Builder), with automated regression testing guarding every milestone."*

#### Slide 6: Software Requirements Specification (SRS)
- **Speaker Script:**  
  *"The system satisfies 16 functional requirements (FR-01 to FR-16) covering authentication, curriculum management, attendance tracking, and grading, supported by 8 non-functional requirements prioritizing sub-200ms response latency, HMAC-SHA512 token security, and strict relational integrity."*

---

### Part II: Architecture, UML & Object-Oriented Design (Presenter: Student Member 2)

#### Slide 7: System Architecture & Modern Tech Stack
- **Speaker Script:**  
  *"Thank you, [Student 1]. Moving to system architecture, CampusConnect employs a Layered Modular Monolith. The presentation tier is built with React 18, TypeScript 5.6, and Tailwind CSS. The application tier runs Java 21 LTS with Spring Boot 3.3.5 and Spring Security 6.3. The persistence tier utilizes PostgreSQL 16 with Flyway versioned migrations."*

#### Slide 8: Use Case Diagram
- **Speaker Script:**  
  *"Our Use Case Diagram defines three primary actors: Administrator, Faculty, and Student. Administrators manage institutional master catalogs and timetable allocations. Faculty record attendance sessions and continuous assessment grades. Students register for courses, monitor attendance deficits, and view academic transcripts."*

#### Slide 9: Class Diagram & Structural Architecture
- **Speaker Script:**  
  *"Our domain model centers on the abstract base class `Person`, extended polymorphically by `Student`, `Faculty`, and `Admin`. The class diagram faithfully maps the Java entity model: `Department` aggregates `Course` and `Faculty`, while `Course` strongly composes `AssessmentComponent`, ensuring cascaded deletion lifecycle rules."*

#### Slide 10: Four Pillars of OOP in CampusConnect
- **Speaker Script:**  
  *"We have rigorously implemented all four OOP pillars:*
  1. *Encapsulation: Fields are private; `Student.promoteToNextSemester()` enforces semester limits (1–12).*
  2. *Inheritance: `Person` hierarchy utilizes JPA `@Inheritance(strategy = InheritanceType.JOINED)`.*
  3. *Abstraction: `Person` is abstract; `GradingStrategy` abstracts calculation logic.*
  4. *Polymorphism: Dynamic method dispatch in strategy evaluation and enrollment state transitions."*

#### Slide 11: Object Relationships & GoF Design Patterns
- **Speaker Script:**  
  *"Beyond the four pillars, we implemented five GoF design patterns:*
  - *State Pattern: 6-state enrollment lifecycle (`DraftState`, `SubmittedState`, `ApprovedState`, `EnrolledState`, etc.).*
  - *Strategy Pattern: `AbsoluteGradingStrategy` (fixed scale) and `RelativeGradingStrategy` (Gaussian curve).*
  - *Builder Pattern: `TranscriptDirector` and `TranscriptBuilder` assembling official transcripts.*
  - *Observer Pattern: `AcademicEventListener` dispatching persistent notifications on grade publication.*
  - *Factory Pattern: Centralized instantiation via `GradingStrategyFactory`."*

#### Slide 12: Sequence & Activity Diagrams
- **Speaker Script:**  
  *"Our Sequence Diagram traces the JWT authentication lifecycle from React submission through Spring Security filters to token issuance. Our Activity Diagram illustrates the course enrollment flow, showing decision points for prerequisite verification and seat capacity availability."*

---

### Part III: Persistence, Implementation & Quality Assurance (Presenter: Student Member 3)

#### Slide 13: Relational Persistence & ER Model
- **Speaker Script:**  
  *"Thank you, [Student 2]. Turning to data persistence, our database comprises 17 relational tables normalized to Third Normal Form (3NF). We utilize JPA Joined Inheritance, where common columns reside in `persons`, while specialized columns live in `students`, `faculty`, and `admins` joined on primary key `id`. Referential integrity is enforced through foreign keys and composite indexes."*

#### Slide 14: Implementation Details
- **Speaker Script:**  
  *"In our implementation, Java 21 features—such as records for immutable event DTOs and pattern matching—work alongside Spring Security's stateless filter chain. On the frontend, Axios interceptors inject JWT tokens and handle session expirations, while TypeScript interfaces mirror backend DTO contracts to eliminate serialization errors."*

#### Slide 15: Main Modules & Role-Based Portals
- **Speaker Script:**  
  *"The platform hosts three role-based portals: `/admin` for master curriculum, timetable conflict checking, and audit trails; `/faculty` for attendance sessions and modular score entry; and `/student` for self-service course registration and official transcript generation."*

#### Slide 16: Testing & Quality Assurance
- **Speaker Script:**  
  *"Quality assurance was executed through automated multi-tier testing: 57 backend tests in JUnit 5 and Mockito, 18 frontend tests in Vitest and React Testing Library, and 36 live API security tests, yielding a 100% pass rate across 112 tests. Furthermore, we audited and resolved 8 specific defects, including critical IDOR vulnerabilities in enrollment and attendance endpoints."*

#### Slide 17: Team Contribution Matrix & Maintenance
- **Speaker Script:**  
  *"Our team shared balanced responsibilities:*
  - *[Student 1]: Core architecture, Spring Security, JWT, Strategy Pattern, Audit logging.*
  - *[Student 2]: React frontend, UI state guards, State Pattern, Timetable UI, Presentation Guide.*
  - *[Student 3]: PostgreSQL schema, JPA entity mapping, Builder Pattern, Testing Suite & QA audit.*
  *Our maintenance model encompasses corrective bug fixes, adaptive library upgrades, perfective grading enhancements, and preventive static analysis."*

#### Slide 18: Conclusion & Viva Voce
- **Speaker Script (Delivered jointly):**  
  *"In conclusion, CampusConnect successfully delivers a production-ready, full-stack campus management platform adhering strictly to Object-Oriented Methodology and software engineering best practices. We thank the evaluation committee and welcome your questions and live demonstration requests."*

---

## 3. Anticipated Viva Voce Questions & Answers

### For Student Member 1 (Architecture & Security Focus):
1. **Q: Why did you choose JPA JOINED inheritance over SINGLE_TABLE?**  
   *A:* `JOINED` normalizes the database schema into distinct tables (`persons`, `students`, `faculty`, `admins`) joined by primary key `id`. It eliminates nullable columns, enforces relational integrity, and directly mirrors the OOP *is-a* relationship.
2. **Q: How does the Strategy pattern achieve the Open/Closed Principle?**  
   *A:* New grading schemes (e.g. Pass/Fail or standard deviation curves) can be added by implementing the `GradingStrategy` interface without altering `GradeServiceImpl` or existing strategy classes.

### For Student Member 2 (Design Patterns & UI Focus):
1. **Q: How does the State pattern prevent invalid enrollment status transitions?**  
   *A:* Each state is an object implementing `EnrollmentState`. Illegal transitions (e.g., calling `approve()` on an already `DROPPED` enrollment) throw `IllegalStateException` inside `AbstractEnrollmentState`, removing brittle `if-else` conditionals.
2. **Q: Where is runtime polymorphism demonstrated?**  
   *A:* When calling `strategy.evaluate()` or `state.approve()`, Java resolves the method call dynamically at runtime based on the actual concrete object injected, executing class-specific behavior.

### For Student Member 3 (Database, Testing & Builder Focus):
1. **Q: Why use the Builder pattern for transcripts instead of a standard constructor?**  
   *A:* Transcripts are complex composite documents with student metadata, multiple semester courses, cumulative GPA calculations, and security signatures. The Builder pattern constructs this step-by-step through a director without telescoping constructors.
2. **Q: How were IDOR vulnerabilities resolved during testing?**  
   *A:* In `EnrollmentController` and `AttendanceController`, we introduced caller verification where the authenticated JWT principal is checked against the target student ID. Non-owners and unauthorized faculty receive HTTP 403 Forbidden.
