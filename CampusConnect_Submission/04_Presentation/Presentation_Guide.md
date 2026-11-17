# CampusConnect: Presentation & Viva Voce Guide

**Project Title:** CampusConnect — Smart Campus Management System  
**Academic Subject:** Object-Oriented Methodology (OOM)  
**Slide Deck:** `CampusConnect_OOM_Presentation.pptx` (18 Slides, 16:9 Widescreen)  
**Target Duration:** 15 – 20 Minutes (followed by 5 – 10 minutes of Viva Voce Q&A)

---

## 1. Presentation Structure & Timing Allocation

| Slide # | Slide Title | Recommended Time | Core Focus |
|:---:|:---|:---:|:---|
| **1** | Title Slide & Candidate Introduction | 0.5 min | Introduce self, guide, institution, and project title. |
| **2** | Executive Summary & Problem Motivation | 1.0 min | Pain points of fragmented campus management. |
| **3** | System Objectives & Role Governance | 1.0 min | Admin, Faculty, and Student responsibilities. |
| **4** | System Architecture & Technology Stack | 1.0 min | React 19, Spring Boot 3.3.0, PostgreSQL, JWT. |
| **5** | Object-Oriented Principles: SOLID & GoF | 1.5 min | Practical mapping of SOLID and design patterns. |
| **6** | Domain Modeling: Polymorphic Inheritance | 1.0 min | Person base entity and JOINED JPA strategy. |
| **7** | Use Case Diagram (Actors & Boundaries) | 1.0 min | Walk through Figure 4.1, actors, <<include>>. |
| **8** | Class Diagram (Structural Design) | 1.5 min | Walk through Figure 4.2, associations, dependencies. |
| **9** | Sequence Diagram (JWT Authentication) | 1.5 min | Walk through Figure 4.3, token issuance & dispatch. |
| **10** | Activity Diagram (Course Enrollment) | 1.0 min | Walk through Figure 4.4, decision forks & capacity. |
| **11** | State Machine (Enrollment Lifecycle) | 1.0 min | Walk through Figure 4.5, discrete states & triggers. |
| **12** | Component & Deployment Diagrams | 1.0 min | Walk through Figures 4.6 & 4.7, 3-tier topology. |
| **13** | Entity-Relationship (ER) Diagram & Normalization | 1.0 min | Walk through Figure 4.8, 3NF & BCNF proofs. |
| **14** | Security Engineering: IDOR Vulnerability Resolution | 1.5 min | Before/After security fix with SecurityUtil. |
| **15** | Quality Assurance & Testing: 112/112 Verified Tests | 1.0 min | 57 backend, 18 frontend, 36 live API tests. |
| **16** | Live Demonstration Highlights & Workflows | 1.0 min | 5-stage live demo execution path. |
| **17** | System Limitations & Future Scope | 0.5 min | Realistic constraints & architectural roadmap. |
| **18** | Conclusion & Viva Voce Q&A | 0.5 min | Closing statement & invitation for defense questions. |
| **Total** | **Full Presentation** | **~17.0 min** | **Comprehensive, disciplined academic delivery** |

---

## 2. Slide-by-Slide Speaker Notes & Script

### Slide 1: Title Slide
- **Speaker Script:**  
  *"Respected members of the evaluation committee, esteemed guide [Faculty Guide Name], and faculty members. Good morning/afternoon. My name is [Student Name], Roll Number [Roll Number]. Today, I am proud to present the design, implementation, and quality assurance audit of my Object-Oriented Methodology project: **CampusConnect — Smart Campus Management System**."*
- **Key Takeaways to Highlight:** Full-stack enterprise architecture, strict object-oriented design, production-grade empirical verification.

---

### Slide 2: Executive Summary & Problem Motivation
- **Speaker Script:**  
  *"In modern academic institutions, operations are frequently hampered by fragmented record-keeping. Attendance registers, spreadsheet grade sheets, and manual course enrollments create operational delays, human errors, and severe security risks—most notably Insecure Direct Object References (IDOR), where students can view or alter peer data. CampusConnect was engineered to resolve these challenges through clean object-oriented architecture, multi-tiered role-based security, and verifiable data integrity."*

---

### Slide 3: System Objectives & Role Governance Architecture
- **Speaker Script:**  
  *"The system provides three strictly segregated operational portals:*
  1. *The **Administrator Portal** oversees system-wide governance: user provisioning with BCrypt encryption, department and course structuring, and master timetable scheduling.*
  2. *The **Faculty Portal** empowers instructors to inspect real-time course rosters, submit daily attendance (Present, Absent, Late, Excused), and enter continuous evaluation grades.*
  3. *The **Student Portal** enables self-service course registration, real-time academic transcript inspection, and attendance monitoring with proactive low-attendance warnings."*

---

### Slide 4: System Architecture & Modern Technology Stack
- **Speaker Script:**  
  *"To guarantee performance, maintainability, and security, CampusConnect utilizes modern, industry-standard technologies:*
  - *Frontend: React 19 with TypeScript, Tailwind CSS, and Vite, enforcing strict type safety and responsive design.*
  - *Backend: Java 21 LTS with Spring Boot 3.3.0 and Spring Security 6, organized into a clean 3-Tier Layered Architecture.*
  - *Persistence: Spring Data JPA and Hibernate with Flyway versioned migrations (V1 through V4), supporting both in-memory H2 for integration testing and PostgreSQL 16 for production deployment.*
  - *Security: Stateless HMAC-SHA256 JWT tokens with 24-hour expiration."*

---

### Slide 5: Object-Oriented Methodology: SOLID Principles & Design Patterns
- **Speaker Script:**  
  *"The architecture rigorously adheres to the five SOLID principles:*
  - *Single Responsibility: For example, `JwtTokenProvider` manages cryptographic token operations, while `SecurityUtil` is solely responsible for caller identity and ownership validation.*
  - *Open/Closed Principle: Our `Person` domain hierarchy allows new user categories to be introduced without modifying existing authentication filters.*
  - *Liskov Substitution: Any method accepting `Person` seamlessly accepts `Admin`, `Faculty`, or `Student` instances.*
  - *Interface Segregation: We use focused Spring Data repositories rather than bloated persistence interfaces.*
  - *Dependency Inversion: REST Controllers depend on high-level Service Interfaces rather than concrete implementations.*
  *Furthermore, classic Gang of Four patterns—including the Repository, Service Layer, DTO, Adapter, and Singleton patterns—are embedded throughout the codebase."*

---

### Slide 6: Domain Modeling: Polymorphic Inheritance Hierarchy
- **Speaker Script:**  
  *"At the heart of the domain layer is the abstract `Person` entity, extended into `Admin`, `Faculty`, and `Student`. We chose the `@Inheritance(strategy = InheritanceType.JOINED)` strategy. This ensures that common attributes like email and hashed password reside in a base table, while role-specific attributes—such as `roll_number` for students and `employee_id` for faculty—are stored in dedicated normalized tables without sparse null columns."*

---

### Slide 7: Use Case Diagram (Figure 4.1)
- **Speaker Script:**  
  *"As depicted in Figure 4.1, the Use Case Diagram models functional boundaries across our actors. Every protected academic action carries an `<<include>>` dependency on Token Verification. The boundary between student self-service and administrative governance is strictly delineated."*

---

### Slide 8: Class Diagram (Figure 4.2)
- **Speaker Script:**  
  *"Figure 4.2 shows our structural Class Diagram. Notice the joined inheritance from `Person`, the 1-to-N aggregation between `Department` and `Course`, and the associative `Enrollment` entity connecting `Student` and `Course`. Controllers depend strictly on Service interfaces, completely isolating web concerns from persistence logic."*

---

### Slide 9: Sequence Diagram: Authentication Flow (Figure 4.3)
- **Speaker Script:**  
  *"Figure 4.3 illustrates the dynamic runtime interaction during user login and protected dispatch. When the client submits credentials, `AuthController` delegates to Spring's `AuthenticationManager`. Upon successful BCrypt hash verification, `JwtTokenProvider` generates a cryptographically signed token. Subsequent requests pass through `JwtAuthenticationFilter`, which verifies the HMAC signature and populates the `SecurityContext`."*

---

### Slide 10: Activity Diagram: Course Enrollment Workflow (Figure 4.4)
- **Speaker Script:**  
  *"Figure 4.4 visualizes the algorithmic control flow of student course enrollment. Decision nodes evaluate course active status, duplicate registrations, and seat capacity. Successful requests transition into a provisional `PENDING` state within an isolated database transaction, awaiting administrative approval."*

---

### Slide 11: State Machine: Academic Enrollment Lifecycle (Figure 4.5)
- **Speaker Script:**  
  *"Figure 4.5 maps the discrete states of an `Enrollment` instance. An enrollment begins in `PENDING`. Upon administrative approval, it transitions to `ACTIVE`, which unlocks attendance and grading workflows. When the semester concludes, it transitions to `COMPLETED`, permanently locking the earned credits and grade points into the transcript."*

---

### Slide 12: Component & Deployment Topology (Figures 4.6 & 4.7)
- **Speaker Script:**  
  *"Figures 4.6 and 4.7 demonstrate our physical and logical deployment architecture. The React SPA communicates over TLS HTTPS with the Spring Boot container. HikariCP connection pooling maintains resilient JDBC sessions to the PostgreSQL database cluster over Port 5432."*

---

### Slide 13: Relational Persistence & Normalization (Figure 4.8)
- **Speaker Script:**  
  *"Figure 4.8 presents our Entity-Relationship model comprising 11 relational tables. The schema satisfies Third Normal Form (3NF) and Boyce-Codd Normal Form (BCNF). Transitive dependencies are eliminated through foreign key references, and surrogate primary keys prevent partial key dependencies."*

---

### Slide 14: Security Engineering & IDOR Vulnerability Resolution
- **Speaker Script:**  
  *"During our security audit, we identified three critical Insecure Direct Object Reference (IDOR) vulnerabilities on enrollment, attendance, and grade endpoints where client-supplied `studentId` path parameters were trusted.  
  We resolved this by implementing defense-in-depth ownership verification inside `SecurityUtil`. The system resolves the caller's identity directly from the signed JWT principal. If a student attempts to access another student's record, the request is immediately terminated with HTTP 403 Forbidden. Faculty access is similarly restricted to students actively enrolled in sections they teach."*

---

### Slide 15: Quality Assurance & Testing: 112/112 Verified Tests
- **Speaker Script:**  
  *"Our testing audit validated the application across three tiers:
  - 57 backend unit and integration tests (JUnit 5 & Mockito)
  - 18 frontend component tests (Jest & React Testing Library)
  - 36 live API integration tests verifying all endpoints and security constraints
  Total: 112 out of 112 tests passed with a 100% success rate, confirming zero regressions."*

---

### Slide 16: Live Demonstration Highlights
- **Speaker Script:**  
  *"In our live system demonstration, we execute five key workflows: multi-role authentication with dynamic navigation rendering, department and course provisioning, student course enrollment with administrative approval, faculty attendance entry, and finally, a live security demonstration proving that parameter tampering triggers HTTP 403 Forbidden."*

---

### Slide 17: Limitations & Future Architectural Roadmap
- **Speaker Script:**  
  *"While the core academic governance engine is fully operational, we have identified future enhancements: incorporating payment gateways like Razorpay for automated tuition receipts, integrating IoT biometric scanners for classroom attendance, and developing React Native mobile applications."*

---

### Slide 18: Conclusion & Viva Voce Q&A
- **Speaker Script:**  
  *"In conclusion, CampusConnect demonstrates the power of Object-Oriented Methodology in engineering complex, secure, and maintainable enterprise software. Thank you for your time and consideration. I now warmly invite your questions and feedback for the Viva Voce evaluation."*

---

## 3. Anticipated Viva Voce / Defense Questions & Model Answers

### Q1: Why did you choose JPA Joined Inheritance (`InheritanceType.JOINED`) over Single Table or Table Per Class?
- **Model Answer:**  
  *"We chose `InheritanceType.JOINED` because it maintains strict 3NF database normalization. `SINGLE_TABLE` would require sparse columns with null values (e.g. `roll_number` null for faculty, `employee_id` null for students) and prevents database-level NOT NULL constraints. `TABLE_PER_CLASS` causes schema duplication and inefficient polymorphic queries with UNIONs. JOINED keeps shared attributes in `persons` and specialized attributes in child tables joined by primary key."*

### Q2: How did you fix the IDOR vulnerability, and why didn't you just hide the studentId in the frontend?
- **Model Answer:**  
  *"Hiding UI elements or parameters in the frontend is 'security through obscurity' and provides zero defense against malicious requests sent via cURL or Postman. We fixed IDOR at the backend service layer using `SecurityUtil`. The backend extracts the authenticated user's email directly from the cryptographically signed JWT token in `SecurityContextHolder`, resolves the caller's database record, and verifies ownership. If `callerStudentId != targetStudentId`, Spring Security immediately throws an `AccessDeniedException` returning HTTP 403 Forbidden."*

### Q3: What is the purpose of Data Transfer Objects (DTOs), and why not return JPA Entities directly to the React frontend?
- **Model Answer:**  
  *"Directly exposing JPA Entities causes three major issues:  
  1. Security leaks: Sensitive fields like password hashes or internal audit fields could be serialized into JSON.  
  2. Circular reference exceptions during Jackson JSON serialization (e.g., Student -> Enrollment -> Student infinite recursion).  
  3. Tight coupling: Database schema changes would immediately break the frontend API contract.  
  DTOs act as clean boundary objects that encapsulate only the exact data required by the presentation layer."*

### Q4: How does your system handle timetable scheduling conflicts?
- **Model Answer:**  
  *"In `TimetableService`, before saving a lecture slot, the system validates two invariants: first, `startTime` must be strictly before `endTime`. Second, it queries `TimetableRepository` to ensure neither the assigned faculty member nor the specified lecture hall (`roomNumber`) has an overlapping lecture slot on the same day of the week."*

### Q5: What is the difference between Authentication and Authorization in CampusConnect?
- **Model Answer:**  
  *"Authentication verifies identity—confirming who the user is using BCrypt salted password hashing and issuing a signed JWT token. Authorization verifies permissions—determining what an authenticated user is permitted to do based on their assigned role (ADMIN, FACULTY, STUDENT) and resource ownership rules enforced by Spring Security's `@PreAuthorize` and `SecurityUtil`."*
