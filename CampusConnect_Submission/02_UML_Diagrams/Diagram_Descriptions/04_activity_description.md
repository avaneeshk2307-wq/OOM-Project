# Diagram 4: Activity Diagram Description

## 1. Diagram Title
**Student Course Enrollment & Administrative Verification Activity Workflow**

## 2. Diagram Purpose & Academic Importance
In OOM, Activity Diagrams capture procedural workflows and operational step-by-step logic. This diagram models the end-to-end enrollment workflow, illustrating decision branches, concurrency, error recovery conditions, and swimlane role handoffs between the Student and Administrator.

## 3. Workflow Logic and Decision Gates
1. **Student Initiation**: Authenticated student queries the course catalog.
2. **Duplicate Check Decision Node**: The backend verifies if `(student_id, course_id, semester)` already exists.
   - *If Duplicate*: Throws `DuplicateEnrollmentException`, responds with **HTTP 409 Conflict**, displays user alert, and terminates flow.
   - *If Unique*: Proceeds to seat availability check.
3. **Capacity Check Decision Node**: Verifies `enrolledCount < capacity`.
   - *If Full*: Responds with **HTTP 400 Bad Request**, displaying "Course capacity reached".
   - *If Seats Available*: Instantiates `Enrollment` in `SUBMITTED` state and logs an immutable audit trail entry.
4. **Administrator Swimlane**:
   - Administrator reviews submitted applications via `/admin/enrollments`.
   - Admin approves prerequisites (`SUBMITTED -> APPROVED`).
   - Admin confirms seat finalization (`APPROVED -> ENROLLED`), which atomically increments `enrolledCount`.
   - If declined at any stage, transitions to `REJECTED`.

## 4. Source Code Mapping
- Controller: `com.campusconnect.controller.EnrollmentController.java`
- Service: `com.campusconnect.service.EnrollmentServiceImpl.java`
- Frontend: `src/pages/student/StudentCatalog.tsx`, `src/pages/admin/AdminEnrollmentManagement.tsx`
