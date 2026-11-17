# CampusConnect: RESTful API Specification

**Base URL:** `http://localhost:8080/api/v1`  
**Protocol:** HTTP/1.1 over TLS (HTTPS in Production)  
**Authentication Header:** `Authorization: Bearer <JSON_WEB_TOKEN>`  
**Content-Type:** `application/json`

---

## 1. Authentication & Session Management

### 1.1 User Login
- **Endpoint:** `POST /api/v1/auth/login`
- **Access Level:** Public (No token required)
- **Description:** Authenticates credentials using BCrypt hashing and issues an HMAC-SHA256 signed JWT token.
- **Request Body:**
  ```json
  {
    "email": "student@campusconnect.edu",
    "password": "Student@123"
  }
  ```
- **Responses:**
  - `200 OK`:
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHVkZW50QGNhbXB1c2Nvbm5lY3QuZWR1Iiwicm9sZXMiOlsicm9sZV9zdHVkZW50Il0sImlhdCI6MTczMTYwMDAwMCwiZXhwIjoxNzMxNjg2NDAwfQ...",
      "type": "Bearer",
      "id": 3,
      "email": "student@campusconnect.edu",
      "firstName": "Jane",
      "lastName": "Doe",
      "role": "STUDENT"
    }
    ```
  - `401 Unauthorized`: Invalid credentials supplied.

---

## 2. Department Management

### 2.1 Retrieve All Departments
- **Endpoint:** `GET /api/v1/departments`
- **Access Level:** Authenticated (ADMIN, FACULTY, STUDENT)
- **Response `200 OK`:**
  ```json
  [
    {
      "id": 1,
      "code": "CS",
      "name": "Computer Science & Engineering",
      "description": "Department of Computer Science and Engineering",
      "headOfDepartment": "Dr. Alan Turing"
    }
  ]
  ```

### 2.2 Create Department
- **Endpoint:** `POST /api/v1/departments`
- **Access Level:** Restricted (`ROLE_ADMIN`)
- **Request Body:**
  ```json
  {
    "code": "ECE",
    "name": "Electronics & Communication Engineering",
    "description": "Department of ECE",
    "headOfDepartment": "Dr. Claude Shannon"
  }
  ```
- **Response `201 Created`:** Returns created department object with generated `id`.

---

## 3. Course Catalog & Curriculum

### 3.1 Retrieve Paginated Courses
- **Endpoint:** `GET /api/v1/courses`
- **Query Parameters:**
  - `page`: Zero-based page index (default: `0`)
  - `size`: Items per page (default: `10`)
  - `departmentId` *(optional)*: Filter by academic department ID
  - `active` *(optional)*: Filter by boolean active status (`true` / `false`)
- **Access Level:** Authenticated (ADMIN, FACULTY, STUDENT)
- **Response `200 OK`:**
  ```json
  {
    "content": [
      {
        "id": 1,
        "code": "CS301",
        "title": "Object-Oriented Software Design",
        "description": "Comprehensive design patterns and UML modeling",
        "credits": 4,
        "semester": 5,
        "departmentId": 1,
        "departmentName": "Computer Science & Engineering",
        "facultyId": 2,
        "facultyName": "Dr. Robert Smith",
        "active": true
      }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 18,
    "totalPages": 2,
    "last": false
  }
  ```

### 3.2 Create New Course
- **Endpoint:** `POST /api/v1/courses`
- **Access Level:** Restricted (`ROLE_ADMIN`)
- **Request Body:**
  ```json
  {
    "code": "CS302",
    "title": "Database Management Systems",
    "description": "Relational algebra, SQL, and normalization",
    "credits": 4,
    "semester": 5,
    "departmentId": 1,
    "facultyId": 2,
    "active": true
  }
  ```
- **Response `201 Created`:** Course successfully registered in database.

---

## 4. Enrollment Lifecycle Management

### 4.1 Retrieve Enrollments by Student (IDOR-Protected)
- **Endpoint:** `GET /api/v1/enrollments/student/{studentId}`
- **Access Level:** 
  - `ROLE_ADMIN`: Allowed for any `studentId`.
  - `ROLE_FACULTY`: Allowed if faculty member teaches a course in which the student is enrolled.
  - `ROLE_STUDENT`: Allowed strictly if `studentId` matches the authenticated caller's own ID.
- **Security Interception:** Mismatching student IDs immediately return `403 Forbidden`.
- **Response `200 OK`:**
  ```json
  [
    {
      "id": 1,
      "studentId": 1,
      "studentRollNumber": "CS2026001",
      "studentName": "Jane Doe",
      "courseId": 1,
      "courseCode": "CS301",
      "courseTitle": "Object-Oriented Software Design",
      "enrollmentDate": "2026-08-15T09:00:00",
      "status": "ACTIVE"
    }
  ]
  ```

### 4.2 Submit Course Enrollment Request
- **Endpoint:** `POST /api/v1/enrollments`
- **Access Level:** `ROLE_STUDENT`, `ROLE_ADMIN`
- **Request Body:**
  ```json
  {
    "studentId": 1,
    "courseId": 1
  }
  ```
- **Response `201 Created`:** Enrollment initialized in `PENDING` status.

### 4.3 Update Enrollment Status
- **Endpoint:** `PUT /api/v1/enrollments/{id}/status`
- **Access Level:** Restricted (`ROLE_ADMIN`)
- **Request Body:**
  ```json
  {
    "status": "ACTIVE"
  }
  ```
- **Response `200 OK`:** Status transitioned; unlocks attendance and grading workflows.

---

## 5. Attendance Management

### 5.1 Retrieve Student Attendance Records (IDOR-Protected)
- **Endpoint:** `GET /api/v1/attendance/student/{studentId}`
- **Access Level:** Enforced via `SecurityUtil` (Admin, assigned Faculty, or self Student).
- **Response `200 OK`:**
  ```json
  [
    {
      "id": 101,
      "studentId": 1,
      "courseId": 1,
      "courseCode": "CS301",
      "attendanceDate": "2026-11-10",
      "status": "PRESENT",
      "remarks": "On time"
    }
  ]
  ```

### 5.2 Log Lecture Attendance
- **Endpoint:** `POST /api/v1/attendance`
- **Access Level:** `ROLE_FACULTY`, `ROLE_ADMIN`
- **Request Body:**
  ```json
  {
    "studentId": 1,
    "courseId": 1,
    "attendanceDate": "2026-11-14",
    "status": "PRESENT",
    "remarks": "Participated in lab exercise"
  }
  ```
- **Response `201 Created`:** Attendance logged and calculated into aggregate metrics.

---

## 6. Academic Grades & Evaluation

### 6.1 Retrieve Student Grade History (IDOR-Protected)
- **Endpoint:** `GET /api/v1/grades/student/{studentId}`
- **Access Level:** Strict ownership guard via `SecurityUtil`.
- **Response `200 OK`:**
  ```json
  [
    {
      "id": 42,
      "studentId": 1,
      "courseId": 1,
      "courseCode": "CS301",
      "courseTitle": "Object-Oriented Software Design",
      "grade": "A",
      "gradePoints": 9.0,
      "semester": 5,
      "academicYear": "2025-2026",
      "comments": "Outstanding performance in UML design defense."
    }
  ]
  ```

### 6.2 Submit Course Grade
- **Endpoint:** `POST /api/v1/grades`
- **Access Level:** `ROLE_FACULTY`, `ROLE_ADMIN`
- **Request Body:**
  ```json
  {
    "studentId": 1,
    "courseId": 1,
    "grade": "A",
    "gradePoints": 9.0,
    "semester": 5,
    "academicYear": "2025-2026",
    "comments": "Excellent final term submission"
  }
  ```
- **Response `201 Created`:** Grade recorded in student academic transcript.

---

## 7. Master Timetables & Scheduling

### 7.1 Retrieve Timetable by Course
- **Endpoint:** `GET /api/v1/timetables/course/{courseId}`
- **Access Level:** Authenticated
- **Response `200 OK`:**
  ```json
  [
    {
      "id": 12,
      "courseId": 1,
      "courseCode": "CS301",
      "facultyId": 2,
      "facultyName": "Dr. Robert Smith",
      "dayOfWeek": "MONDAY",
      "startTime": "09:00:00",
      "endTime": "10:30:00",
      "roomNumber": "LH-301",
      "building": "Turing Engineering Hall"
    }
  ]
  ```

### 7.2 Create Timetable Slot
- **Endpoint:** `POST /api/v1/timetables`
- **Access Level:** Restricted (`ROLE_ADMIN`)
- **Supported Time Formats:** `HH:mm` (e.g. `09:30`) or `HH:mm:ss` (e.g. `09:30:00`).
- **Validation Constraints:**
  - `startTime` must precede `endTime`.
  - No faculty schedule collision on same `dayOfWeek` and time bracket.
  - No room schedule collision on same `dayOfWeek` and time bracket.
- **Request Body:**
  ```json
  {
    "courseId": 1,
    "facultyId": 2,
    "dayOfWeek": "MONDAY",
    "startTime": "09:00",
    "endTime": "10:30",
    "roomNumber": "LH-301",
    "building": "Turing Engineering Hall"
  }
  ```
- **Response `201 Created`:** Schedule slot allocated.
- **Response `400 Bad Request`:** Returned if `startTime >= endTime` or if schedule clash detected.

---

## 8. Standard HTTP Status Code Reference

| Status Code | Meaning | Context in CampusConnect |
|:---:|:---|:---|
| `200 OK` | Request Succeeded | Successful entity retrieval or update operation. |
| `201 Created` | Resource Created | Successful POST creation of entity (User, Course, Timetable). |
| `400 Bad Request` | Validation Error | Malformed payload, invalid format, or business invariant violation. |
| `401 Unauthorized` | Missing / Invalid Token | Expired JWT token, missing `Authorization` header, or wrong credentials. |
| `403 Forbidden` | Access Denied | Role violation or IDOR tamper attempt intercepted by `SecurityUtil`. |
| `404 Not Found` | Resource Absent | Requested ID does not exist in database. |
| `500 Server Error` | Unhandled Exception | Internal runtime failure (caught by global `GlobalExceptionHandler`). |
