# Diagram 1: Use Case Diagram Description

## 1. Diagram Title
**CampusConnect System Use Case Model — Actor Interaction & Functional Scope**

## 2. Diagram Purpose & Academic Importance
In Object-Oriented Methodology (OOM), the Use Case Diagram serves as the primary artifact for functional requirements elicitation and system boundary definition. It visualizes the high-level services that CampusConnect exposes to its external actors while formally establishing the institutional security boundaries.

## 3. System Actors
1. **Administrator (`ROLE_ADMIN`)**: Elevated institutional authority responsible for onboarding departments, students, faculty, assigning instructional courses, approving and finalizing registrations, scheduling classrooms and timetables, and auditing security logs.
2. **Faculty Member (`ROLE_FACULTY`)**: Instructional staff responsible for managing assigned courses, tracking lecture attendance sessions, defining course assessment criteria, and recording continuous assessment marks.
3. **Student (`ROLE_STUDENT`)**: Enrolled learner who queries the academic curriculum, submits registration requests, inspects attendance thresholds, reviews grades, and accesses personalized weekly schedules.

## 4. Key Use Cases and Relationships
- **Authentication & Profile**: Shared across all three actors. Every user must present valid credentials to receive a signed JWT token before invoking protected use cases.
- **Course Registration Workflow**:
  - `Register for Course` **<<include>>** `Browse Course Catalog`: A student cannot initiate registration without resolving a valid catalog offering.
  - `Drop Enrolled Course` **<<extend>>** `Register for Course`: Course dropping conditionally extends the active enrollment lifecycle prior to drop deadlines.
- **Administrative Curriculum Management**:
  - `Assign Course Instructor` **<<include>>** `Manage Curriculum Courses`: Section allocation requires an active course definition.
  - `Promote Student Semester` **<<extend>>** `Manage Student Roster`: Semester promotion is an optional, periodic extension of student lifecycle oversight.
- **Instructional Grading**:
  - `Enter Student Grades` **<<include>>** `Create Course Assessments`: Grade entry depends on previously defined assessment components and weightage.

## 5. Source Code Mapping
- Frontend Views: `src/pages/auth/LoginPage.tsx`, `src/pages/admin/*`, `src/pages/faculty/*`, `src/pages/student/*`
- Backend Controllers: `AdminStudentController`, `AdminFacultyController`, `AdminCourseController`, `EnrollmentController`, `AttendanceController`, `GradeController`, `TimetableController`
