# CampusConnect: Database Architecture & Data Dictionary

**Persistence Engine:** Relational Database Management System (PostgreSQL 16 / H2)  
**Schema Normalization:** Third Normal Form (3NF) & Boyce-Codd Normal Form (BCNF)  
**ORM Framework:** Spring Data JPA with Hibernate 6  
**Migration Strategy:** Flyway Versioned Database Migrations (V1 to V4)

---

## 1. Relational Schema Architecture

The CampusConnect relational database is structured to model real-world academic institutions cleanly. Rather than relying on denormalized single tables with nullable columns, the database utilizes a **Joined Table Inheritance** approach for human actors and clean associative junction entities for academic workflows.

```
                    +-------------------+
                    |      persons      |
                    +-------------------+
                      ^       ^       ^
        +-------------+       |       +-------------+
        |                     |                     |
+---------------+     +---------------+     +---------------+
|    admins     |     |   faculties   |     |   students    |
+---------------+     +---------------+     +---------------+
                              |                     |
                              v                     v
+---------------+     +---------------+     +---------------+
|  departments  |<--->|    courses    |<--->|  enrollments  |
+---------------+     +---------------+     +---------------+
                              |                     |
            +-----------------+-----+               |
            |                 |     |               |
            v                 v     v               v
     +--------------+  +----------+ +-----------+ +------------+
     |  timetables  |  |  grades  | |attendances| |audit_logs  |
     +--------------+  +----------+ +-----------+ +------------+
```

---

## 2. Complete Data Dictionary

### Table 1: `persons` (Base Entity for Joined Inheritance)
| Column Name | Data Type | Nullable | Constraints / Default | Description |
|:---|:---|:---:|:---|:---|
| `id` | BIGINT | NO | PRIMARY KEY, AUTO_INCREMENT | Unique identifier for person record. |
| `email` | VARCHAR(255) | NO | UNIQUE | Institutional email used as unique login credential. |
| `password` | VARCHAR(255) | NO | | BCrypt salted cryptographic hash (cost 10). |
| `first_name` | VARCHAR(100) | NO | | Legal given name. |
| `last_name` | VARCHAR(100) | NO | | Legal family surname. |
| `phone_number` | VARCHAR(20) | YES | | Contact telephone number. |
| `role` | VARCHAR(50) | NO | CHECK (role IN ('ADMIN', 'FACULTY', 'STUDENT')) | Security role discriminator. |
| `created_at` | TIMESTAMP | NO | CURRENT_TIMESTAMP | Record creation timestamp. |
| `updated_at` | TIMESTAMP | NO | CURRENT_TIMESTAMP | Record modification timestamp. |

---

### Table 2: `admins` (Administrator Specialization)
| Column Name | Data Type | Nullable | Constraints / Default | Description |
|:---|:---|:---:|:---|:---|
| `id` | BIGINT | NO | PRIMARY KEY, FK -> `persons(id)` ON DELETE CASCADE | Inherited person identifier. |
| `department` | VARCHAR(100) | YES | | Administrative division (e.g. Academic Affairs). |

---

### Table 3: `faculties` (Faculty Instructor Specialization)
| Column Name | Data Type | Nullable | Constraints / Default | Description |
|:---|:---|:---:|:---|:---|
| `id` | BIGINT | NO | PRIMARY KEY, FK -> `persons(id)` ON DELETE CASCADE | Inherited person identifier. |
| `employee_id` | VARCHAR(50) | NO | UNIQUE | Unique employee payroll identifier. |
| `designation` | VARCHAR(100) | YES | | Academic rank (e.g. Associate Professor). |
| `department_id` | BIGINT | NO | FK -> `departments(id)` ON DELETE RESTRICT | Affiliated academic department. |

---

### Table 4: `students` (Student Specialization)
| Column Name | Data Type | Nullable | Constraints / Default | Description |
|:---|:---|:---:|:---|:---|
| `id` | BIGINT | NO | PRIMARY KEY, FK -> `persons(id)` ON DELETE CASCADE | Inherited person identifier. |
| `roll_number` | VARCHAR(50) | NO | UNIQUE | University matriculation registration number. |
| `batch_year` | INT | NO | | Matriculation intake year (e.g. 2023). |
| `current_semester`| INT | NO | CHECK (current_semester BETWEEN 1 AND 8) | Active academic semester. |
| `department_id` | BIGINT | NO | FK -> `departments(id)` ON DELETE RESTRICT | Major degree department. |

---

### Table 5: `departments` (Academic Units)
| Column Name | Data Type | Nullable | Constraints / Default | Description |
|:---|:---|:---:|:---|:---|
| `id` | BIGINT | NO | PRIMARY KEY, AUTO_INCREMENT | Department identifier. |
| `code` | VARCHAR(20) | NO | UNIQUE | Standard department code (e.g. `CS`, `ME`, `EE`). |
| `name` | VARCHAR(255) | NO | | Formal academic title. |
| `description` | TEXT | YES | | Departmental scope and facilities. |
| `head_of_dept` | VARCHAR(150) | YES | | Name of designated Department Head. |

---

### Table 6: `courses` (Curriculum Offerings)
| Column Name | Data Type | Nullable | Constraints / Default | Description |
|:---|:---|:---:|:---|:---|
| `id` | BIGINT | NO | PRIMARY KEY, AUTO_INCREMENT | Course offering identifier. |
| `code` | VARCHAR(50) | NO | UNIQUE | Standard course code (e.g. `CS301`). |
| `title` | VARCHAR(255) | NO | | Official course title. |
| `description` | TEXT | YES | | Course syllabus summary. |
| `credits` | INT | NO | CHECK (credits > 0) | Academic credit weight (typically 1 to 6). |
| `semester` | INT | NO | CHECK (semester BETWEEN 1 AND 8) | Prescribed semester curriculum slot. |
| `department_id` | BIGINT | NO | FK -> `departments(id)` ON DELETE RESTRICT | Sponsoring academic department. |
| `faculty_id` | BIGINT | YES | FK -> `faculties(id)` ON DELETE SET NULL | Assigned lead instructor. |
| `is_active` | BOOLEAN | NO | DEFAULT TRUE | Course active registration flag. |

---

### Table 7: `enrollments` (Student Course Registrations)
| Column Name | Data Type | Nullable | Constraints / Default | Description |
|:---|:---|:---:|:---|:---|
| `id` | BIGINT | NO | PRIMARY KEY, AUTO_INCREMENT | Enrollment record identifier. |
| `student_id` | BIGINT | NO | FK -> `students(id)` ON DELETE CASCADE | Registered student reference. |
| `course_id` | BIGINT | NO | FK -> `courses(id)` ON DELETE RESTRICT | Enrolled course reference. |
| `enrollment_date`| TIMESTAMP | NO | CURRENT_TIMESTAMP | Registration initiation timestamp. |
| `status` | VARCHAR(50) | NO | CHECK (status IN ('PENDING', 'ACTIVE', 'DROPPED', 'COMPLETED', 'REJECTED')) | Enrollment lifecycle state. |
| *Composite* | UNIQUE | NO | UNIQUE (student_id, course_id) | Prevents duplicate course enrollment. |

---

### Table 8: `attendances` (Daily Lecture Attendance)
| Column Name | Data Type | Nullable | Constraints / Default | Description |
|:---|:---|:---:|:---|:---|
| `id` | BIGINT | NO | PRIMARY KEY, AUTO_INCREMENT | Attendance entry identifier. |
| `student_id` | BIGINT | NO | FK -> `students(id)` ON DELETE CASCADE | Attending student reference. |
| `course_id` | BIGINT | NO | FK -> `courses(id)` ON DELETE CASCADE | Course section reference. |
| `attendance_date`| DATE | NO | | Date of lecture session. |
| `status` | VARCHAR(50) | NO | CHECK (status IN ('PRESENT', 'ABSENT', 'LATE', 'EXCUSED')) | Attendance status flag. |
| `remarks` | VARCHAR(255) | YES | | Optional instructor notes. |
| *Composite* | UNIQUE | NO | UNIQUE (student_id, course_id, attendance_date) | Prevents duplicate logging per day. |

---

### Table 9: `grades` (Academic Evaluation Records)
| Column Name | Data Type | Nullable | Constraints / Default | Description |
|:---|:---|:---:|:---|:---|
| `id` | BIGINT | NO | PRIMARY KEY, AUTO_INCREMENT | Grade record identifier. |
| `student_id` | BIGINT | NO | FK -> `students(id)` ON DELETE CASCADE | Evaluated student reference. |
| `course_id` | BIGINT | NO | FK -> `courses(id)` ON DELETE CASCADE | Assessed course offering. |
| `grade` | VARCHAR(5) | NO | (e.g. 'A+', 'A', 'B', 'C', 'F') | Standard letter grade awarded. |
| `grade_points` | DOUBLE | NO | CHECK (grade_points BETWEEN 0.0 AND 10.0) | Numerical grade point equivalent. |
| `semester` | INT | NO | | Semester in which course was taken. |
| `academic_year` | VARCHAR(50) | NO | | Academic term year (e.g. '2025-2026'). |
| `comments` | TEXT | YES | | Qualitative instructor feedback. |
| *Composite* | UNIQUE | NO | UNIQUE (student_id, course_id, semester) | One grade entry per student per course. |

---

### Table 10: `timetables` (Lecture Schedule Allocations)
| Column Name | Data Type | Nullable | Constraints / Default | Description |
|:---|:---|:---:|:---|:---|
| `id` | BIGINT | NO | PRIMARY KEY, AUTO_INCREMENT | Schedule slot identifier. |
| `course_id` | BIGINT | NO | FK -> `courses(id)` ON DELETE CASCADE | Scheduled course offering. |
| `faculty_id` | BIGINT | NO | FK -> `faculties(id)` ON DELETE RESTRICT | Instructing faculty member. |
| `day_of_week` | VARCHAR(20) | NO | CHECK (day_of_week IN ('MONDAY', 'TUESDAY', ...)) | Day of weekly meeting. |
| `start_time` | TIME | NO | | Period start time (HH:mm:ss). |
| `end_time` | TIME | NO | CHECK (end_time > start_time) | Period end time. |
| `room_number` | VARCHAR(50) | NO | | Allocated lecture hall / laboratory. |
| `building` | VARCHAR(100) | YES | | Campus building / complex name. |

---

### Table 11: `announcements` (Institutional Notifications)
| Column Name | Data Type | Nullable | Constraints / Default | Description |
|:---|:---|:---:|:---|:---|
| `id` | BIGINT | NO | PRIMARY KEY, AUTO_INCREMENT | Announcement identifier. |
| `title` | VARCHAR(255) | NO | | Headline title of notification. |
| `content` | TEXT | NO | | Announcement message body. |
| `published_by` | BIGINT | NO | FK -> `persons(id)` | Authoring administrator reference. |
| `target_role` | VARCHAR(50) | NO | 'ALL', 'STUDENT', 'FACULTY' | Audience filter discriminator. |
| `published_at` | TIMESTAMP | NO | CURRENT_TIMESTAMP | Broadcast timestamp. |

---

## 3. Database Normalization Proofs

### 3.1 First Normal Form (1NF) Compliance
- Every table has a well-defined primary key (`id`).
- All column values are strictly atomic (no comma-delimited arrays, JSON blobs, or repeating groups).
- **Proof:** All attributes contain single indivisible values adhering to their relational SQL data types.

### 3.2 Second Normal Form (2NF) Compliance
- System satisfies 1NF.
- All non-key attributes are fully functionally dependent on the entire primary key.
- **Proof:** In junction tables such as `enrollments`, `attendances`, and `grades`, surrogate auto-incrementing primary keys (`id`) are utilized alongside composite unique constraints. No non-key attribute depends on a partial candidate key.

### 3.3 Third Normal Form (3NF) Compliance
- System satisfies 2NF.
- No transitive functional dependencies exist ($X \to Y \to Z$).
- **Proof:** Attributes like `department_name` are never stored directly in `students` or `courses`; they are stored exclusively in `departments` and resolved via foreign keys (`department_id`). Consequently, updating a department name requires modifying exactly one tuple in `departments`, avoiding update anomalies.

### 3.4 Boyce-Codd Normal Form (BCNF) Compliance
- For every non-trivial functional dependency $X \to Y$, $X$ is a superkey.
- **Proof:** In all tables, determinants are unique primary keys or candidate keys (e.g. `roll_number` in `students`, `code` in `courses`, `email` in `persons`).

---

## 4. Flyway Database Migrations History

1. **`V1__init_schema.sql`:**
   - Creates base tables: `persons`, `admins`, `faculties`, `students`, `departments`, `courses`, `enrollments`.
   - Creates foreign keys, unique indexes, and table constraints.
2. **`V2__seed_data.sql`:**
   - Seeds initial administrative accounts, faculty members, demo students, and core computer science curriculum courses.
3. **`V3__add_timetables.sql`:**
   - Adds `timetables`, `attendances`, and `grades` relational schemas with temporal check constraints.
4. **`V4__add_announcements_and_audit.sql`:**
   - Adds `announcements` and `audit_logs` tables with user activity tracking and role filters.
