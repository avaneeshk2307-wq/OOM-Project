-- =========================================================================
-- CampusConnect V1__init_core_schema.sql
-- Normalized 3NF Database Schema for CampusConnect
-- Implements JPA JOINED Inheritance Table Hierarchy and Relational Constraints
-- =========================================================================

-- 1. Departments Table
CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Persons Table (Abstract Superclass in JOINED Inheritance)
CREATE TABLE persons (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(30),
    account_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Students Table (Subclass of Person)
CREATE TABLE students (
    id BIGINT PRIMARY KEY REFERENCES persons(id) ON DELETE CASCADE,
    roll_number VARCHAR(50) NOT NULL UNIQUE,
    semester INT NOT NULL CHECK (semester >= 1 AND semester <= 12),
    department_id BIGINT REFERENCES departments(id) ON DELETE RESTRICT
);

-- 4. Faculty Table (Subclass of Person)
CREATE TABLE faculty (
    id BIGINT PRIMARY KEY REFERENCES persons(id) ON DELETE CASCADE,
    employee_code VARCHAR(50) NOT NULL UNIQUE,
    designation VARCHAR(100) NOT NULL,
    department_id BIGINT REFERENCES departments(id) ON DELETE RESTRICT
);

-- 5. Admins Table (Subclass of Person)
CREATE TABLE admins (
    id BIGINT PRIMARY KEY REFERENCES persons(id) ON DELETE CASCADE,
    access_level VARCHAR(50) NOT NULL DEFAULT 'SUPER_ADMIN'
);

-- 6. User Accounts Table (Authentication & Security)
CREATE TABLE user_accounts (
    id BIGSERIAL PRIMARY KEY,
    person_id BIGINT NOT NULL UNIQUE REFERENCES persons(id) ON DELETE CASCADE,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 7. Courses Table
CREATE TABLE courses (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    credits INT NOT NULL CHECK (credits > 0),
    semester INT NOT NULL CHECK (semester >= 1),
    capacity INT NOT NULL CHECK (capacity > 0),
    department_id BIGINT REFERENCES departments(id) ON DELETE RESTRICT,
    faculty_id BIGINT REFERENCES faculty(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 8. Course Assessments (Composition relationship owned by Course)
CREATE TABLE course_assessments (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    max_marks NUMERIC(5,2) NOT NULL CHECK (max_marks > 0),
    weightage_percentage NUMERIC(5,2) NOT NULL CHECK (weightage_percentage > 0 AND weightage_percentage <= 100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 9. Enrollments Table (Association between Student and Course with State lifecycle)
CREATE TABLE enrollments (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    course_id BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    semester INT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    registered_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_student_course_semester UNIQUE (student_id, course_id, semester)
);

-- 10. Attendance Sessions Table (Composition owned by Course)
CREATE TABLE attendance_sessions (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    session_date DATE NOT NULL,
    topic VARCHAR(200),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 11. Attendance Records Table (Composition owned by Attendance Session)
CREATE TABLE attendance_records (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL REFERENCES attendance_sessions(id) ON DELETE CASCADE,
    student_id BIGINT NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    status VARCHAR(30) NOT NULL DEFAULT 'PRESENT',
    remarks VARCHAR(255),
    CONSTRAINT uq_session_student UNIQUE (session_id, student_id)
);

-- 12. Grades Table (Evaluation result per Enrollment & Assessment)
CREATE TABLE grades (
    id BIGSERIAL PRIMARY KEY,
    enrollment_id BIGINT NOT NULL REFERENCES enrollments(id) ON DELETE CASCADE,
    assessment_id BIGINT NOT NULL REFERENCES course_assessments(id) ON DELETE CASCADE,
    marks_obtained NUMERIC(5,2) NOT NULL CHECK (marks_obtained >= 0),
    letter_grade VARCHAR(10),
    graded_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_enrollment_assessment UNIQUE (enrollment_id, assessment_id)
);

-- 13. Announcements Table
CREATE TABLE announcements (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    target_role VARCHAR(50), -- NULL = ALL, or ROLE_ADMIN, ROLE_FACULTY, ROLE_STUDENT
    course_id BIGINT REFERENCES courses(id) ON DELETE CASCADE,
    author_id BIGINT REFERENCES persons(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Helpful Query Optimization Indexes
CREATE INDEX idx_students_roll ON students(roll_number);
CREATE INDEX idx_faculty_code ON faculty(employee_code);
CREATE INDEX idx_courses_code ON courses(code);
CREATE INDEX idx_enrollments_student ON enrollments(student_id);
CREATE INDEX idx_enrollments_course ON enrollments(course_id);
CREATE INDEX idx_attendance_records_student ON attendance_records(student_id);
CREATE INDEX idx_attendance_sessions_course ON attendance_sessions(course_id, session_date);
CREATE INDEX idx_grades_enrollment ON grades(enrollment_id);
