# CampusConnect: Smart Campus Management System

> **Object-Oriented Methodology (OOM) Major University Project Submission**  
> **Architecture:** Full-Stack Decoupled N-Tier Web Application  
> **Technologies:** Java 21, Spring Boot 3.3.0, Spring Security, Spring Data JPA, React 19, TypeScript, Tailwind CSS, PostgreSQL 16, Vite, PlantUML

---

## 1. Project Overview

**CampusConnect** is a comprehensive, enterprise-grade Smart Campus Management System engineered strictly under the **Object-Oriented Methodology (OOM)** paradigm. Designed to eliminate operational friction and data silos within higher education institutions, CampusConnect unifies academic governance, student self-service, faculty course administration, attendance tracking, and continuous grading into a single, cohesive, role-governed web application.

The core design centers around an extensible domain hierarchy anchored by the abstract `Person` entity, specialized into `Admin`, `Faculty`, and `Student` utilizing JPA's `InheritanceType.JOINED` strategy. The system delivers multi-tier defense-in-depth security, utilizing stateless cryptographically signed JSON Web Tokens (JWT) and service-layer identity ownership guards that prevent Insecure Direct Object Reference (IDOR) vulnerabilities.

---

## 2. Key Architectural Highlights

- **Domain-Driven Object-Oriented Modeling:** Polymorphic inheritance hierarchies, encapsulation, clean abstractions, and high cohesion across all modules.
- **SOLID Design Principles:** Real-world realization of Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, and Dependency Inversion.
- **Gang of Four (GoF) Patterns:** Extensive implementation of the Repository, Service Layer, Data Transfer Object (DTO), Adapter, Factory, and Singleton patterns.
- **Stateless Defense-in-Depth Security:** Spring Security filter chain with HMAC-SHA256 JWT tokens, role-based method security (`@PreAuthorize`), and automated IDOR ownership guards in `SecurityUtil`.
- **Third Normal Form (3NF) Database Design:** 11 normalized relational tables, zero partial or transitive dependencies, managed via Flyway database migrations (V1 to V4).
- **Responsive Single-Page Application (SPA):** Built with React 19, TypeScript, Vite, and Tailwind CSS, featuring client-side role guards, instant feedback modals, and dynamic navigation.
- **Empirical Quality Assurance:** 112/112 verified automated tests (57 backend unit/integration tests, 18 frontend component tests, 36 live API integration tests).

---

## 3. Technology Stack & Versions

| Layer | Technology | Version | Purpose |
|:---|:---|:---|:---|
| **Backend Language** | Java (OpenJDK) | 21 LTS | Core backend object-oriented implementation |
| **Backend Framework** | Spring Boot | 3.3.0 | Application framework, DI container, MVC REST controllers |
| **Security Framework** | Spring Security | 6.x | Authentication, authorization, filter chains, CORS |
| **Token Provider** | JJWT (`jjwt-api`, `jjwt-impl`) | 0.12.5 | Cryptographic JWT signing and HMAC-SHA256 validation |
| **Persistence / ORM** | Spring Data JPA / Hibernate | 6.x | Object-relational mapping, repositories, transactions |
| **Database Migration** | Flyway Core | 10.x | Version-controlled database schema migrations |
| **Database (Dev/Test)** | H2 In-Memory Database | Embedded | Rapid local development and integration testing |
| **Database (Production)** | PostgreSQL | 16.x | Production ACID relational persistence store |
| **Frontend Framework** | React | 19.x | Component-based reactive user interface |
| **Frontend Language** | TypeScript | 5.x | Static typing, interface definitions, compile-time safety |
| **Frontend Bundler** | Vite | 6.x | High-speed frontend build and dev tooling |
| **Styling & Icons** | Tailwind CSS & Lucide Icons | Latest | Responsive utility styling and modern UI icons |
| **Testing Backend** | JUnit 5, Mockito, AssertJ | Latest | Backend unit, integration, and security mock testing |
| **Testing Frontend** | Jest, React Testing Library | Latest | Frontend component and role-guard unit testing |

---

## 4. Repository & Submission Structure

```
CampusConnect_Submission/
├── 01_Project_Source/                  # Clean copy of complete application code
│   └── CampusConnect/
│       ├── backend/                    # Java 21 Spring Boot backend application
│       ├── frontend/                   # React 19 TypeScript frontend application
│       ├── docker-compose.yml          # PostgreSQL & pgAdmin container orchestration
│       └── README.md                   # Local workspace developer README
├── 02_UML_Diagrams/                    # All 8 Standard UML Diagrams for OOM Evaluation
│   ├── PlantUML_Source/                # Raw .puml text source files (1 to 8)
│   ├── Rendered_PNG/                   # High-resolution rendered diagram PNG images
│   ├── Diagram_Descriptions/           # Detailed academic explanations for each diagram
│   └── UML_Index.md                    # Master diagram directory and architectural summary
├── 03_Project_Report/                  # Comprehensive University Major Project Report
│   ├── CampusConnect_OOM_Report.docx   # Full formal academic report in Word format (~25-35 pages)
│   ├── CampusConnect_OOM_Report.pdf    # Print-ready compiled PDF report with embedded diagrams
│   └── Report_Assets/                  # High-res diagrams embedded into report chapters
├── 04_Presentation/                    # Evaluation & Defense Presentation Materials
│   ├── CampusConnect_OOM_Presentation.pptx # 18-slide university presentation deck (16:9)
│   ├── Presentation_Guide.md           # Slide-by-slide speaker notes, scripts & Viva Voce Q&A
│   └── Presentation_Assets/            # Diagrams embedded in presentation slides
├── 05_Testing_Documentation/           # Full Testing Audit & Verification Trail
│   ├── Initial_Testing_Audit.md        # Comprehensive baseline audit discovering 8 defects
│   ├── Bug_Fix_Report.md               # Root cause analysis, code fixes, and security patches
│   ├── Final_Regression_Report.md      # Read-only regression verification (112/112 tests passed)
│   └── Test_Summary.md                 # Executive test matrix and QA certification
├── 06_Technical_Documentation/         # Complete Technical Guides
│   ├── README.md                       # Master technical overview (this document)
│   ├── API_Documentation.md            # Comprehensive REST endpoint catalog & schemas
│   ├── Installation_Guide.md           # Step-by-step local Windows/Linux setup guide
│   ├── Database_Documentation.md       # Data dictionary, schema, migrations, 3NF proofs
│   └── Demo_Guide.md                   # Evaluator walkthrough script & default credentials
└── 07_Submission_Checklist/            # Final Submission Verification
    ├── Final_Checklist.md              # University deliverables audit & verification status
    └── Missing_Information.md          # List of editable placeholders ([Student Name], etc.)
```

---

## 5. Quick-Start Instructions

### Prerequisites
- **JDK 21 LTS** installed and configured on `PATH`.
- **Node.js v20+** and **npm** installed.
- **Apache Maven 3.9+** (or use the included `mvnw` wrapper).

### 1. Launch Backend Server
```powershell
cd CampusConnect/backend
# Launch with embedded H2 dev profile (zero external database setup required)
$env:SPRING_PROFILES_ACTIVE="dev"
mvn spring-boot:run
```
*Backend initializes at: `http://localhost:8080`*  
*H2 In-Memory Console available at: `http://localhost:8080/h2-console`*

### 2. Launch Frontend Dev Server
```powershell
cd CampusConnect/frontend
npm install
npm run dev
```
*Frontend interface accessible at: `http://localhost:5173`*

### 3. Log In with Default Seed Accounts
- **Administrator:** `admin@campusconnect.edu` / `Admin@123`
- **Faculty:** `faculty@campusconnect.edu` / `Faculty@123`
- **Student:** `student@campusconnect.edu` / `Student@123`

---

## 6. Academic Assessment Highlights

For examiners and reviewers evaluating this project under the Object-Oriented Methodology (OOM) curriculum:
1. **Structural Excellence:** Review `02_UML_Diagrams/02_class_diagram.puml` and `Chapter 4` of the project report to inspect the polymorphic inheritance hierarchy.
2. **Behavioral Invariants:** Inspect `02_UML_Diagrams/04_activity_diagram_enrollment.puml` and `05_state_machine_enrollment.puml` for formal lifecycle transitions.
3. **Security Engineering:** Review `CampusConnect/backend/src/main/java/com/campusconnect/security/SecurityUtil.java` and `05_Testing_Documentation/Bug_Fix_Report.md` to examine the IDOR security mitigation.
4. **Empirical Quality:** All 112 verification tests pass with 100% success rate without mock regressions.
