# Diagram 8: Entity-Relationship (ER) Diagram Description

## 1. Diagram Title
**CampusConnect Relational Database Schema & Integrity Model**

## 2. Diagram Purpose & Academic Importance
The ER Diagram models the relational database schema, detailing tables, column types, primary key (PK) specifications, foreign key (FK) constraints, candidate keys, and relational cardinalities ensuring ACID compliance and normalized data storage.

## 3. Database Schema Overview
- **Inheritance Mapping (Joined Table Strategy)**:
  - Base table `persons` (`id`, `name`, `email` UQ, `phone`, `created_at`, `updated_at`).
  - Sub-tables `admins`, `faculty`, and `students` link to `persons(id)` via primary key foreign keys.
  - `user_accounts` maintains a 1:1 foreign key relationship to `persons(id)`.
- **Academic Domain Relationships**:
  - `departments` (1:N) $ightarrow$ `faculty`, `students`, `courses`.
  - `courses` (1:N) $ightarrow$ `enrollments`, `timetables`, `attendance_sessions`, `course_assessments`.
  - Composite Unique Constraints:
    - `enrollments`: `UQ(student_id, course_id, semester)` — prevents duplicate course registration.
    - `attendance_records`: `UQ(session_id, student_id)` — prevents duplicate attendance marking.
    - `student_grades`: `UQ(assessment_id, student_id)` — prevents duplicate grade entries.

## 4. Source Code Mapping
- Flyway SQL Migrations: `backend/src/main/resources/db/migration/V1__init_schema.sql`, `V3__phase2_schema.sql`
- JPA Entity Models: `com.campusconnect.model.*`
