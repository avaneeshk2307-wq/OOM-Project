# Diagram 2: Class Diagram Description

## 1. Diagram Title
**CampusConnect Domain Model & Pattern Class Diagram**

## 2. Diagram Purpose & Academic Importance
The Class Diagram is the foundational structural backbone of Object-Oriented Methodology. It models the static structural design of the software, detailing domain entity schemas, visibility modifiers, attribute typings, behavioral methods, generalization (inheritance) hierarchies, interface realizations, and associations with exact multiplicities.

## 3. Key Object-Oriented Concepts Illustrated
1. **Inheritance & Abstraction (`Person` Hierarchy)**:
   - `Person` is an abstract base entity capturing shared demographic attributes (`id`, `name`, `email`, `phone`, timestamps).
   - `Admin`, `Faculty`, and `Student` inherit from `Person` via JPA `InheritanceType.JOINED`, sharing primary key foreign key identity while adding specialized fields (`accessLevel`, `employeeCode`, `rollNumber`).
2. **Polymorphic Design Patterns**:
   - **State Pattern (`EnrollmentState`)**: The `Enrollment` entity delegates lifecycle transitions to `EnrollmentState`, realized by `DraftState`, `SubmittedState`, `ApprovedState`, `EnrolledState`, `DroppedState`, and `RejectedState`.
   - **Strategy Pattern (`GradingStrategy`)**: Grade calculation algorithms are decoupled into `PercentageGradingStrategy`, `LetterGradingStrategy`, and `GpaGradingStrategy`.
3. **Multiplicity & Relational Associations**:
   - `Department "1" o-- "0..*"`: Aggregation where courses, faculty, and students belong to a department.
   - `AttendanceSession "1" *-- "0..*"`: Composition where `AttendanceRecord` instances cannot exist without their parent session.
   - `Course "1" -- "0..*"`: Association to `Enrollment`, `Timetable`, and `CourseAssessment`.

## 4. Source Code Mapping
- Base Entity: `com.campusconnect.model.Person`
- Specializations: `com.campusconnect.model.Admin`, `Faculty`, `Student`
- Domain Entities: `Course`, `Department`, `Enrollment`, `Timetable`, `Classroom`, `AttendanceSession`, `CourseAssessment`
- Design Patterns: `com.campusconnect.pattern.state.*`, `com.campusconnect.pattern.strategy.*`
