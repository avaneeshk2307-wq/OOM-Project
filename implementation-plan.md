# MASTER PROMPT: CAMPUSCONNECT
## Smart Campus Management System Using Object-Oriented Methodology (OOM)

Act as a senior software architect, Java developer, full-stack engineer, database architect, UI/UX designer, UML specialist, software tester, and Object-Oriented Analysis and Design (OOAD) expert.

I am developing a university-level academic project for my Object-Oriented Methodology (OOM) course.

I want to develop a complete, professional, fully functional web-based Campus Management System called **CampusConnect**.

Your responsibility is to plan, design, implement, test, document, and prepare the complete project for academic submission and viva evaluation.

This is not just a frontend project. It must be a real full-stack application with:
- React frontend.
- Java Spring Boot backend.
- PostgreSQL database.
- Real REST APIs.
- Authentication and role-based authorization.
- Meaningful object-oriented design.
- UML diagrams.
- Automated tests.
- Project report.
- Presentation content.
- Complete source code.

The most important academic requirement is that OOM concepts must be implemented in actual Java source code, not just described in documentation.

==================================================
# 1. PROJECT OVERVIEW
==================================================

Project Name: CampusConnect

Project Title: Smart Campus Management System Using Object-Oriented Methodology

Project Type: Full-Stack Web Application

Architecture: Layered Modular Monolithic Architecture

Target Users:

1. ROLE_ADMIN
2. ROLE_FACULTY
3. ROLE_STUDENT

## Problem Statement

Traditional campus management often involves separate systems or manual processes for managing student records, faculty information, courses, enrollment, attendance, academic performance, and announcements.

This creates problems such as:
- Duplicate records.
- Time-consuming administrative processes.
- Difficult access to academic information.
- Manual attendance management.
- Inefficient communication.
- Lack of centralized academic information.

## Proposed Solution

CampusConnect provides a centralized platform that allows administrators to manage campus operations, faculty to manage academic activities, and students to access their academic information.

The application must have separate role-based dashboards and secure access control.

==================================================
# 2. TECHNOLOGY STACK
==================================================

Use the following technology stack.

## Frontend

- React.js
- Vite
- TypeScript with strict mode
- Tailwind CSS
- React Router DOM
- Axios
- TanStack Query
- React Hook Form
- Zod
- Recharts
- Lucide React

## Backend

- Java 21
- Spring Boot 3.x (choose a currently supported compatible release)
- Spring Web
- Spring Data JPA
- Spring Security
- Hibernate
- JWT Authentication
- Maven
- Jakarta Bean Validation
- MapStruct
- Lombok (use selectively)

Use Spring Boot dependency management for compatible Hibernate and Spring Security versions. Do not manually force incompatible versions.

## Database

- PostgreSQL
- Flyway database migrations

## Testing

Backend:
- JUnit 5
- Mockito
- AssertJ
- Spring Boot Test
- MockMvc

Frontend:
- Vitest
- React Testing Library

Database integration:
- H2 for basic isolated repository tests.
- PostgreSQL Testcontainers for important integration tests where Docker is available.

## Documentation and Development

- Git
- GitHub
- PlantUML
- Mermaid where useful
- Swagger / OpenAPI
- Docker Compose
- Antigravity IDE

Do not add unnecessary technologies that make the project difficult to install or explain.

==================================================
# 3. SYSTEM ARCHITECTURE
==================================================

Use a layered modular monolith.

Architecture:

React Frontend
    |
    | HTTP / REST API
    |
Spring Boot Backend
    |
    |-- Controller Layer
    |
    |-- Service Layer
    |
    |-- Domain / Entity Layer
    |
    |-- Repository Layer
    |
PostgreSQL Database

## Controller Layer

Responsibilities:
- Handle HTTP requests.
- Validate request data.
- Return appropriate response codes.
- Delegate business operations to services.
- Avoid business logic inside controllers.

## Service Layer

Responsibilities:
- Implement business rules.
- Manage transactions.
- Coordinate domain objects and repositories.
- Perform authorization checks where appropriate.
- Coordinate complex workflows.

## Domain Layer

Responsibilities:
- Represent real-world campus objects.
- Encapsulate business rules.
- Maintain valid object states.
- Implement appropriate domain behavior.

## Repository Layer

Responsibilities:
- Database operations.
- Queries.
- Persistence.
- Data retrieval.

## DTO Layer

Use Java Records where appropriate.

Do not expose database entities directly through REST APIs.

## Security Layer

- JWT authentication.
- Spring Security.
- Role-based authorization.
- Protected endpoints.
- Method-level security.

Use constructor dependency injection throughout.

==================================================
# 4. USER ROLES AND FUNCTIONAL REQUIREMENTS
==================================================

Create three independent dashboards.

## A. ADMIN DASHBOARD

The administrator manages the campus.

### Dashboard Overview
- Total students.
- Total faculty.
- Total courses.
- Total departments.
- Total enrollments.
- Attendance statistics.
- Recent activities.
- Department-wise statistics.

Fetch all statistics from the backend.

### Student Management
- Add student.
- View students.
- Edit student details.
- Delete student with appropriate dependency checks.
- Search by name and roll number.
- Filter by department and semester.
- View student profile.

### Faculty Management
- Add faculty.
- View faculty.
- Update faculty details.
- Delete faculty subject to dependency rules.
- Assign faculty to courses.

### Department Management
- Create department.
- View departments.
- Update department.
- Delete department when permitted.
- View department details.

### Course Management
- Create courses.
- View courses.
- Update courses.
- Delete courses subject to constraints.
- Set course credits.
- Set semester.
- Assign faculty.
- Set maximum enrollment capacity.
- View enrolled students.

### Enrollment Management
- View all enrollments.
- View enrollment status.
- Approve or reject enrollment where applicable.
- Cancel enrollment.
- Prevent duplicate enrollment.

### Attendance Management
- View attendance records.
- Filter by course, student, and date.
- View attendance summaries.

### Grade Management
- View academic grades.
- View student performance.
- Generate academic summaries.

### Announcements
- Create announcements.
- Edit announcements.
- Delete announcements.
- Publish announcements to selected roles.

## B. STUDENT DASHBOARD

Students must access only their own private academic information.

Features:

- Student dashboard.
- Personal profile.
- Roll number.
- Department and semester.
- Enrolled courses.
- Course registration.
- Course information.
- Attendance percentage.
- Attendance history.
- Grades and marks.
- Academic performance charts.
- Weekly timetable.
- Campus announcements.
- Profile management.
- Logout.

Students must not be allowed to change attendance or grades.

Students must not access another student's academic information.

## C. FACULTY DASHBOARD

Features:

- Faculty dashboard.
- Faculty profile.
- Assigned courses.
- Assigned student lists.
- Mark attendance.
- View attendance history.
- Enter student grades.
- Update grades within authorized rules.
- View student performance.
- View attendance shortage warnings.
- View teaching timetable.
- Publish course announcements.
- Logout.

Faculty must only manage their assigned courses and students.

==================================================
# 5. OBJECT-ORIENTED METHODOLOGY REQUIREMENTS
==================================================

THIS IS THE MOST IMPORTANT SECTION FOR THE OOM EVALUATION.

Implement all four pillars of OOP in actual Java source code.

## A. ENCAPSULATION

- Use private fields.
- Use controlled constructors.
- Avoid unrestricted public setters.
- Validate data.
- Protect important object state.
- Implement meaningful business methods.

Example:

student.promoteToNextSemester();

Do not allow arbitrary invalid changes to student state.

## B. INHERITANCE

Implement a meaningful class hierarchy.

Base abstract class:

Person

Common attributes:
- id
- name
- email
- phone
- accountStatus
- createdAt

Subclasses:

1. Student
2. Faculty
3. Admin

Use JPA JOINED inheritance strategy where appropriate.

Example:

Person
  |
  |-- Student
  |
  |-- Faculty
  |
  |-- Admin

Each subclass must contain its own relevant attributes.

Student:
- rollNumber
- semester
- department

Faculty:
- employeeCode
- designation
- department

Admin:
- accessLevel

Ensure the JPA mapping works correctly with Spring Data JPA.

## C. ABSTRACTION

Use abstract classes and interfaces.

Examples:
- GradingStrategy
- NotificationService
- ReportBuilder
- ReportExporter

Expose only necessary behavior while hiding implementation details.

## D. POLYMORPHISM

Demonstrate:
- Method overriding.
- Runtime polymorphism.
- Interface-based polymorphism.
- Strategy-based implementations.

Example:

GradingStrategy strategy;

strategy.calculateGrade(...);

The actual implementation should depend on the selected grading strategy.

## E. ASSOCIATION

Implement appropriate relationships such as:
- Faculty and Course.
- Student and Department.
- Student and Enrollment.

## F. AGGREGATION

Demonstrate a meaningful weak whole-part relationship.

Example:
Department and Course.

Courses can remain in academic archives even if department organization changes.

## G. COMPOSITION

Demonstrate strong ownership.

Example:
Course and AssessmentComponent.

Course assessments are owned by the course and have a lifecycle dependent on the parent course.

Use appropriate JPA cascade and orphan-removal behavior.

Explain these relationships clearly in the class diagram.

==================================================
# 6. SOLID AND GRASP PRINCIPLES
==================================================

Implement and document:

1. Single Responsibility Principle.
2. Open/Closed Principle.
3. Liskov Substitution Principle.
4. Interface Segregation Principle.
5. Dependency Inversion Principle.

Also demonstrate GRASP principles where appropriate:

- Information Expert.
- Creator.
- Controller.
- Low Coupling.
- High Cohesion.

Create a documentation section explaining:
- What each principle means.
- Where it is implemented.
- Which Java classes demonstrate it.
- Why the design was chosen.

Do not claim a principle is implemented without evidence in source code.

==================================================
# 7. DESIGN PATTERNS
==================================================

Implement design patterns only where they solve real problems.

## Pattern 1: STRATEGY PATTERN

Use this for grading.

Create:

GradingStrategy interface.

Implement:
- AbsoluteGradingStrategy.
- RelativeGradingStrategy (advanced phase).

The system should select the appropriate grading algorithm without changing the main grading workflow.

This demonstrates:
- Abstraction.
- Polymorphism.
- Open/Closed Principle.
- Composition.

## Pattern 2: STATE PATTERN

Use this for enrollment lifecycle.

Possible states:
- Draft.
- Submitted.
- Approved.
- Enrolled.
- Dropped.
- Rejected.

Implement valid transitions and reject invalid transitions.

Example:

Submitted → Approved → Enrolled

A dropped enrollment must not automatically become enrolled again without a valid workflow.

Create an enrollment state diagram.

## Pattern 3: OBSERVER PATTERN

Use Spring ApplicationEventPublisher and event listeners.

Example events:
- GradePublishedEvent.
- AttendanceShortageEvent.
- EnrollmentApprovedEvent.

Listeners should perform meaningful actions, such as creating notifications.

Implement persistent notifications in the advanced phase.

## Pattern 4: BUILDER PATTERN

Use only if justified.

Create an AcademicReportBuilder for assembling complex academic reports or transcripts.

If this pattern is implemented, demonstrate:
- Construction of a complex report.
- Separation of report creation from report representation.
- An actual working report-generation workflow.

Do not add patterns merely to increase their count.

==================================================
# 8. DATABASE DESIGN
==================================================

Use PostgreSQL and normalize the database to 3NF where appropriate.

Core tables:

1. persons
2. students
3. faculty
4. admins
5. user_accounts
6. departments
7. courses
8. enrollments
9. course_assessments
10. attendance_sessions
11. attendance_records
12. grades
13. announcements

Additional tables:

14. classrooms
15. timetables
16. notifications
17. audit_logs

## Important relationships

- Person to Student/Faculty/Admin.
- Department to Students.
- Department to Faculty.
- Department to Courses.
- Faculty to Courses.
- Student to Enrollments.
- Course to Enrollments.
- Course to Assessments.
- Course to Attendance Sessions.
- Attendance Session to Attendance Records.
- Enrollment to Grades.
- Course to Timetable.
- Classroom to Timetable.

## Database constraints

Implement:
- Primary keys.
- Foreign keys.
- Unique constraints.
- NOT NULL constraints.
- Check constraints.
- Useful indexes.
- Appropriate deletion rules.

Important uniqueness rules:

- Unique student roll number.
- Unique faculty employee code.
- Unique account email.
- Unique course code.
- Unique student-course enrollment.
- Unique session-student attendance record.
- Unique enrollment-assessment grade record.

Important validation:

- Marks cannot be negative.
- Marks cannot exceed maximum assessment marks.
- Course capacity must be positive.
- Credits must be positive.
- Attendance status must be valid.
- Semester must be valid.
- Time slots must have valid start and end times.

Compare marks against assessment maximums in the service layer. Do not attempt an ordinary SQL CHECK constraint that reads another table.

Calculate attendance percentages dynamically from attendance records instead of storing duplicate percentage values.

Use Flyway migrations.

==================================================
# 9. AUTHENTICATION AND SECURITY
==================================================

Implement:

- Login.
- JWT authentication.
- BCrypt password hashing.
- Role-based authorization.
- Protected routes.
- Spring Security.
- Method-level authorization.
- Input validation.
- Secure error responses.

Security rules:

1. Admin can access administrative operations.
2. Faculty can access only assigned courses.
3. Students can access only their own academic records.
4. Students cannot modify grades.
5. Students cannot modify attendance.
6. Faculty cannot modify unauthorized courses.
7. No plaintext passwords.
8. No sensitive information in API responses.
9. No hardcoded production secrets.

Protect against IDOR vulnerabilities by deriving the authenticated user identity from the security context and checking resource ownership on the backend.

Use a documented JWT storage and expiration strategy.

Explain how logout works with stateless JWT. Use short token expiration or implement token revocation/refresh-token management if required.

Frontend route guards are not a replacement for backend authorization.

==================================================
# 10. FRONTEND DESIGN REQUIREMENTS
==================================================

Build a modern professional university SaaS dashboard.

Design inspiration:
- Linear.
- Modern university portals.
- Professional administrative dashboards.

Design principles:

- Clean interface.
- Minimal layout.
- Responsive design.
- Consistent spacing.
- Professional typography.
- Modern navigation.
- Reusable components.
- Good accessibility.

Suggested colors:

- Navy blue.
- Indigo.
- White.
- Light gray.
- Green for success.
- Amber for warnings.
- Red for errors.

Use:
- Sidebar navigation.
- Top navigation.
- Dashboard cards.
- Searchable tables.
- Filters.
- Pagination.
- Modals.
- Forms.
- Confirmation dialogs.
- Toast notifications.
- Loading states.
- Empty states.
- Error states.
- Charts.
- Responsive layouts.

Create separate layouts for:
- Admin.
- Student.
- Faculty.

Make every button functional.

Connect frontend components to actual backend APIs.

Do not use static numbers or mock data for completed features.

==================================================
# 11. REST API DESIGN
==================================================

Use REST API versioning:

/api/v1/

Authentication:
- POST /api/v1/auth/login
- GET /api/v1/auth/me
- POST /api/v1/auth/logout

Students:
- POST /api/v1/students
- GET /api/v1/students
- GET /api/v1/students/{id}
- PUT /api/v1/students/{id}
- DELETE /api/v1/students/{id}

Faculty:
- POST /api/v1/faculty
- GET /api/v1/faculty
- GET /api/v1/faculty/{id}
- PUT /api/v1/faculty/{id}
- DELETE /api/v1/faculty/{id}

Departments:
- POST /api/v1/departments
- GET /api/v1/departments
- PUT /api/v1/departments/{id}
- DELETE /api/v1/departments/{id}

Courses:
- POST /api/v1/courses
- GET /api/v1/courses
- GET /api/v1/courses/{id}
- PUT /api/v1/courses/{id}
- DELETE /api/v1/courses/{id}

Enrollments:
- POST /api/v1/enrollments/register
- GET /api/v1/enrollments
- GET /api/v1/enrollments/student/{studentId}
- GET /api/v1/enrollments/course/{courseId}

Attendance:
- POST /api/v1/attendance/sessions
- GET /api/v1/attendance
- GET /api/v1/attendance/student/{studentId}
- GET /api/v1/attendance/course/{courseId}

Grades:
- POST /api/v1/grades
- GET /api/v1/grades/student/{studentId}
- GET /api/v1/grades/course/{courseId}

Announcements:
- POST /api/v1/announcements
- GET /api/v1/announcements
- PUT /api/v1/announcements/{id}
- DELETE /api/v1/announcements/{id}

Timetable:
- POST /api/v1/timetable
- GET /api/v1/timetable
- GET /api/v1/timetable/student/{studentId}
- GET /api/v1/timetable/faculty/{facultyId}

Adapt endpoints if necessary to maintain correct security, consistency, and REST conventions.

Use:
- DTOs.
- Pagination.
- Validation.
- Correct HTTP status codes.
- Centralized exception handling.
- Swagger/OpenAPI documentation.

==================================================
# 12. TWO-PHASE IMPLEMENTATION STRATEGY
==================================================

IMPORTANT:

Develop the system in exactly two major implementation phases.

Do not begin advanced development until the entire core system has been implemented and verified.

--------------------------------------------------
PHASE 1: CORE CAMPUSCONNECT — MANDATORY
--------------------------------------------------

The purpose of Phase 1 is to create a complete working academic management system.

Implement the following:

### 1. Project Setup

- Initialize React and Vite.
- Initialize Java Spring Boot.
- Configure PostgreSQL.
- Configure Flyway.
- Configure frontend/backend communication.
- Create the project directory structure.

### 2. Core Authentication

- Login page.
- JWT authentication.
- Admin role.
- Faculty role.
- Student role.
- Protected backend APIs.
- Protected frontend routes.

### 3. Core OOP Domain Model

Implement:
- Person.
- Student.
- Faculty.
- Admin.
- Department.
- Course.
- Enrollment.
- AssessmentComponent.
- AttendanceSession.
- AttendanceRecord.
- Grade.

Demonstrate:
- Encapsulation.
- Abstraction.
- Inheritance.
- Polymorphism.
- Association.
- Aggregation.
- Composition.

### 4. Admin Module

Implement:
- Admin dashboard.
- Student CRUD.
- Faculty CRUD.
- Department CRUD.
- Course CRUD.
- Enrollment management.
- Attendance viewing.
- Grade viewing.
- Announcement management.

### 5. Student Module

Implement:
- Student dashboard.
- Profile.
- Course catalog.
- Course enrollment.
- Enrolled courses.
- Attendance.
- Grades.
- Announcements.

### 6. Faculty Module

Implement:
- Faculty dashboard.
- Assigned courses.
- Student lists.
- Attendance marking.
- Grade entry.
- Course announcements.

### 7. Core Business Logic

Implement:
- Duplicate enrollment prevention.
- Course capacity validation.
- Attendance calculations.
- Grade validation.
- Role authorization.
- Ownership validation.
- Enrollment lifecycle validation.

### 8. Essential Design Patterns

Implement and demonstrate:
- Strategy Pattern for grading.
- State Pattern for enrollment.

Implement Observer where useful for publishing academic events and triggering basic notification behavior.

### 9. Core Testing

Write tests for:

- Authentication.
- Authorization.
- Student creation.
- Course creation.
- Enrollment.
- Duplicate enrollment prevention.
- Attendance marking.
- Attendance calculations.
- Grade validation.
- Invalid state transitions.

### 10. Core Documentation

Create:
- README.md.
- Architecture documentation.
- Database documentation.
- Use Case Diagram.
- Class Diagram.
- Sequence Diagram.
- Activity Diagram.
- State Diagram.
- ER Diagram.
- OOM traceability matrix.

### PHASE 1 COMPLETION CRITERIA

Do not declare Phase 1 complete until:

1. Frontend starts successfully.
2. Backend starts successfully.
3. PostgreSQL connects successfully.
4. All three roles can log in.
5. All core dashboards work.
6. CRUD operations work.
7. Enrollment works.
8. Attendance works.
9. Grades work.
10. Role authorization works.
11. Actual database persistence works.
12. Automated tests have been executed.
13. UML diagrams match the actual code.
14. README contains working setup instructions.

If any core feature is incomplete, fix it before Phase 2.

--------------------------------------------------
PHASE 2: ADVANCED CAMPUSCONNECT — ENHANCEMENT
--------------------------------------------------

Start this phase only after successful completion of Phase 1.

The objective is to improve the project with advanced software engineering features.

### 1. Advanced Timetable Management

- Classroom management.
- Faculty schedule management.
- Conflict detection.
- Classroom availability.
- Overlapping time-slot validation.
- Weekly timetable interface.

Prevent conflicts involving:
- Faculty.
- Classrooms.
- Courses.
- Time slots.

### 2. Advanced Grading System

Implement:
- AbsoluteGradingStrategy.
- RelativeGradingStrategy.
- Grade calculations.
- Assessment weightage.
- Final grade calculation.
- Student performance analytics.

Make grading strategies interchangeable using polymorphism.

### 3. Notification System

Implement Observer Pattern using Spring events.

Events:
- GradePublishedEvent.
- AttendanceShortageEvent.
- EnrollmentApprovedEvent.

Create a notification center.

Persist notifications in PostgreSQL.

Display notifications in relevant dashboards.

### 4. Academic Transcript Generation

Implement a meaningful Builder Pattern if appropriate.

Create:
- Academic transcript.
- Student performance report.
- Course-wise academic report.
- Department report.

Provide a downloadable PDF where feasible.

### 5. Advanced Analytics

Use Recharts.

Display:
- Department-wise student distribution.
- Course enrollment trends.
- Attendance statistics.
- Student performance.
- Faculty workload.

Fetch analytics from backend endpoints.

### 6. Audit Logs

Track important administrative activities:

- Student creation.
- Course creation.
- Faculty assignment.
- Grade updates.
- Attendance updates.
- Enrollment status changes.

### 7. Advanced Testing

Implement:
- PostgreSQL Testcontainers integration tests where Docker is available.
- Complete workflow tests.
- Security tests.
- API integration tests.
- Frontend interaction tests.

### PHASE 2 COMPLETION CRITERIA

- Advanced features work end to end.
- No existing Phase 1 functionality is broken.
- Design patterns have real implementations.
- Advanced tests pass.
- Documentation is updated.
- UML diagrams are updated to reflect actual code.

==================================================
# 13. UML DIAGRAMS — MANDATORY
==================================================

Generate editable PlantUML source files.

Required diagrams:

1. Use Case Diagram.
2. Class Diagram.
3. Sequence Diagram.
4. Activity Diagram.
5. State Diagram.
6. Component Diagram.
7. Deployment Diagram.
8. ER Diagram.

## Class Diagram

Must contain:
- Important classes.
- Attributes.
- Methods.
- Visibility.
- Inheritance.
- Association.
- Aggregation.
- Composition.
- Multiplicity.

## Sequence Diagram

Show the student course enrollment workflow:

Student → React UI → Controller → Service → Repository → Database.

Include successful enrollment and rejected enrollment.

## Activity Diagram

Show the attendance marking workflow.

## State Diagram

Show enrollment lifecycle transitions.

## Component Diagram

Show frontend, backend layers, security, database and integrations.

## Deployment Diagram

Show:
- Browser.
- Frontend.
- Backend server.
- PostgreSQL server.

## ER Diagram

Show tables, primary keys, foreign keys, and relationships.

IMPORTANT:

Diagrams must reflect actual implementation.

Do not invent classes or relationships only for the diagram.

Store editable source files inside the diagrams directory.

==================================================
# 14. OOM TRACEABILITY MATRIX
==================================================

Create:

documentation/oom-traceability-matrix.md

Use a table containing:

| OOM Concept | Java Class/Interface | UML Diagram | Practical Example | Evidence |

Include:

- Encapsulation.
- Inheritance.
- Abstraction.
- Polymorphism.
- Association.
- Aggregation.
- Composition.
- SOLID.
- GRASP.
- Strategy Pattern.
- State Pattern.
- Observer Pattern.
- Builder Pattern, if implemented.

Only mark a concept as completed if it exists in actual source code.

==================================================
# 15. TESTING AND QUALITY ASSURANCE
==================================================

Implement meaningful automated tests.

## Backend

- Unit tests.
- Service tests.
- Controller tests.
- Repository tests.
- Security tests.
- Domain logic tests.
- Integration tests.

## Frontend

- Component tests.
- Form validation tests.
- Authentication tests.
- Protected route tests.
- Loading and error-state tests.

Important scenarios:

1. Valid login.
2. Invalid login.
3. Admin creates student.
4. Admin creates course.
5. Student enrolls in course.
6. Duplicate enrollment is rejected.
7. Faculty marks attendance.
8. Student views attendance.
9. Faculty enters grades.
10. Student cannot change grades.
11. Faculty cannot access unauthorized courses.
12. Invalid enrollment transition is rejected.

Create a testing report with:

- Test case ID.
- Test description.
- Input.
- Expected result.
- Actual result.
- Status.

Never fabricate test results.

==================================================
# 16. PROJECT DIRECTORY STRUCTURE
==================================================

Create this organized structure:

CampusConnect/
│
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── api/
│   │   ├── assets/
│   │   ├── components/
│   │   │   ├── common/
│   │   │   ├── layout/
│   │   │   └── widgets/
│   │   ├── hooks/
│   │   ├── pages/
│   │   │   ├── auth/
│   │   │   ├── admin/
│   │   │   ├── student/
│   │   │   └── faculty/
│   │   ├── routes/
│   │   ├── context/
│   │   ├── types/
│   │   ├── utils/
│   │   └── App.tsx
│   ├── package.json
│   └── vite.config.ts
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/campusconnect/
│   │   │   │   ├── config/
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── enums/
│   │   │   │   ├── event/
│   │   │   │   ├── exception/
│   │   │   │   ├── mapper/
│   │   │   │   ├── patterns/
│   │   │   │   │   ├── strategy/
│   │   │   │   │   ├── state/
│   │   │   │   │   └── builder/
│   │   │   │   ├── repository/
│   │   │   │   ├── security/
│   │   │   │   └── service/
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── db/migration/
│   │   └── test/
│   └── pom.xml
│
├── database/
│   ├── README.md
│   └── sample-data/
│
├── diagrams/
│   ├── use-case-diagram.puml
│   ├── class-diagram.puml
│   ├── sequence-diagram.puml
│   ├── activity-diagram.puml
│   ├── state-diagram.puml
│   ├── component-diagram.puml
│   ├── deployment-diagram.puml
│   └── er-diagram.puml
│
├── documentation/
│   ├── project-report.md
│   ├── architecture.md
│   ├── requirements.md
│   ├── api-documentation.md
│   ├── testing-report.md
│   ├── oom-traceability-matrix.md
│   ├── viva-questions.md
│   └── presentation-content.md
│
├── screenshots/
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md

You may adjust the structure if technically necessary, but keep the project modular and organized.

==================================================
# 17. PROJECT REPORT
==================================================

Prepare a detailed academic project report.

Target: Approximately 25–35 pages, subject to university guidelines.

Use the following structure:

1. Cover Page.
2. Certificate Page.
3. Acknowledgement.
4. Abstract.
5. Table of Contents.
6. Introduction.
7. Problem Statement.
8. Objectives.
9. Scope.
10. Existing System.
11. Proposed System.
12. Functional Requirements.
13. Nonfunctional Requirements.
14. Technology Stack.
15. System Architecture.
16. Object-Oriented Analysis and Design.
17. OOP Principles.
18. SOLID and GRASP Principles.
19. Design Patterns.
20. UML Diagrams.
21. Database Design.
22. Implementation.
23. Screenshots.
24. Testing and Test Cases.
25. Results.
26. Limitations.
27. Future Scope.
28. Conclusion.
29. References.

Report requirements:

- Use professional academic language.
- Explain concepts clearly.
- Include actual screenshots.
- Include relevant UML diagrams.
- Explain actual Java classes.
- Include actual database relationships.
- Include test cases and real results.
- Include OOM traceability matrix.
- Avoid unnecessary filler.

Leave certificate signatures and institutional approval fields blank for genuine approval.

Create editable Markdown source and a PDF-ready final version.

==================================================
# 18. PPT PRESENTATION
==================================================

Prepare a professional 15–16 slide presentation.

Slide structure:

1. Title Slide.
2. Introduction.
3. Problem Statement.
4. Objectives and Scope.
5. Existing vs Proposed System.
6. Technology Stack.
7. System Architecture.
8. Use Case Diagram.
9. Class Diagram.
10. OOP Concepts.
11. SOLID and Design Patterns.
12. ER Diagram and Database Design.
13. Application Screenshots.
14. Implementation and Testing.
15. Future Scope.
16. Conclusion and Thank You.

Design requirements:

- Professional university presentation.
- Consistent typography.
- Clean layouts.
- Minimal text.
- Proper diagrams.
- Actual application screenshots.
- Clear architecture.
- Speaker notes.

Keep all slide content editable.

Do not use fabricated screenshots or results.

==================================================
# 19. README AND SETUP INSTRUCTIONS
==================================================

Create a complete README.md.

Include:

- Project overview.
- Features.
- Technology stack.
- Architecture.
- Directory structure.
- Requirements and prerequisites.
- Java installation requirements.
- Node.js installation requirements.
- PostgreSQL configuration.
- Environment variables.
- Database setup.
- Frontend setup.
- Backend setup.
- Docker Compose setup.
- API documentation.
- Demo credentials.
- Testing commands.
- Screenshots.
- Common errors and solutions.
- Future improvements.

A new developer must be able to run the project using the README.

Provide exact terminal commands for Windows PowerShell where possible.

==================================================
# 20. VIVA AND DEMONSTRATION PREPARATION
==================================================

Create at least 30 viva questions with detailed, understandable answers.

Cover:

- OOP fundamentals.
- Encapsulation.
- Inheritance.
- Abstraction.
- Polymorphism.
- Interfaces.
- Abstract classes.
- Association.
- Aggregation.
- Composition.
- SOLID.
- GRASP.
- Design patterns.
- UML.
- Database relationships.
- REST APIs.
- Spring Boot.
- Hibernate.
- Authentication.
- Authorization.
- Testing.
- Architecture decisions.

Prepare a 3–5 minute demonstration script.

The demonstration should follow this workflow:

1. Admin logs in.
2. Admin creates a course.
3. Admin registers a student and faculty member.
4. Student logs in.
5. Student enrolls in the course.
6. Faculty logs in.
7. Faculty marks attendance.
8. Faculty enters grades.
9. Student views attendance and grades.
10. Explain the OOP implementation using the actual Java code.

Also prepare difficult evaluator questions and explain how to answer them confidently.

==================================================
# 21. STRICT DEVELOPMENT RULES
==================================================

Follow these rules throughout development.

1. Never create a frontend-only fake application.
2. Never claim a feature works without verification.
3. Never use mock data for completed modules.
4. Never leave buttons nonfunctional.
5. Never expose passwords or sensitive information.
6. Never put all Java classes into one file.
7. Never put business logic inside controllers.
8. Never perform database queries inside domain entities.
9. Never add artificial inheritance or interfaces.
10. Never add design patterns just for appearance.
11. Never generate UML diagrams that contradict source code.
12. Never generate fake test results.
13. Never hardcode production credentials.
14. Never expose unrestricted setters on domain entities.
15. Never break Phase 1 functionality while developing Phase 2.
16. Maintain clean naming conventions.
17. Write understandable code.
18. Add comments for complex business logic.
19. Verify database persistence.
20. Prioritize correctness and maintainability over unnecessary features.

Use Git and create commits after meaningful completed milestones.

Do not overwrite existing working code without reviewing it first.

==================================================
# 22. FINAL SUBMISSION DELIVERABLES
==================================================

At the end of development, prepare:

1. Complete frontend source code.
2. Complete backend source code.
3. Database migrations.
4. Sample database data.
5. Working authentication.
6. Three role-based dashboards.
7. All core CRUD operations.
8. Course enrollment.
9. Attendance management.
10. Grade management.
11. Advanced modules where completed.
12. Editable UML source files.
13. ER diagram.
14. Automated tests.
15. API documentation.
16. README.md.
17. Project report source.
18. PDF-ready report.
19. PPT presentation source.
20. Application screenshots.
21. Testing report.
22. OOM traceability matrix.
23. Viva questions and answers.
24. Demonstration script.
25. Final organized submission folder.

Create a final ZIP of the project only after verifying that all required files exist. Do not include unnecessary build folders, node_modules, secrets, or generated temporary files.

==================================================
# 23. HOW YOU MUST WORK WITH ME
==================================================

I am a student developing this project using Antigravity. I need to understand the code and explain it to my evaluator.

Therefore:

- Explain important technical decisions in simple language.
- Tell me what each major class does.
- Explain why each relationship exists.
- Explain how OOP concepts are implemented.
- Give me commands to run and test the application.
- Do not hide errors.
- Tell me what remains incomplete.
- Help me understand rather than blindly generating code.

Follow the two major implementation phases.

First complete Phase 1, verify it, and only then proceed to Phase 2.

After each major milestone, report:

1. What was implemented.
2. Files created or modified.
3. Important classes and their responsibilities.
4. OOP concepts demonstrated.
5. Commands to run.
6. Tests executed.
7. Actual test results.
8. Remaining problems.

Do not silently skip important requirements.

==================================================
# 24. START DEVELOPMENT NOW
==================================================

Start with PHASE 1 — CORE CAMPUSCONNECT.

First inspect my current Antigravity workspace.

Then:

1. Analyze the requirements.
2. Propose the final architecture.
3. Show the complete directory structure.
4. Design the database schema.
5. Identify domain classes and relationships.
6. Prepare the development milestones.
7. Identify the OOP concepts and their actual implementation locations.
8. Explain the setup requirements.

Do not generate the entire application in one uncontrolled operation.

Start with planning and architecture. Then implement Phase 1 module by module.

If a major design decision is unclear, ask me before proceeding.

After Phase 1 is completely functional and verified, prepare a Phase 1 completion report and ask for my approval before beginning Phase 2.

My ultimate goal is a professional, genuinely working, academically strong OOM project that I can confidently demonstrate, explain, and submit with complete source code, report, and presentation.

BEGIN WITH PHASE 1 NOW.