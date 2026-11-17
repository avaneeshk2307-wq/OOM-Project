# Diagram 5: State Machine Diagram Description

## 1. Diagram Title
**Enrollment Entity Finite State Machine Lifecycle**

## 2. Diagram Purpose & Academic Importance
The State Machine Diagram documents all discrete states an entity can occupy during its lifecycle in response to external events. In CampusConnect, this diagram formalizes the **State Design Pattern** implementation governing academic registrations, enforcing strict transition contracts.

## 3. States and Valid Transitions
1. **`DRAFT`**: Initial staging state during student course exploration.
   - Trigger: `submit()` $ightarrow$ Transitions to `SUBMITTED`.
2. **`SUBMITTED`**: Active pending application submitted to institutional queue.
   - Trigger: `approve()` [by Admin] $ightarrow$ Transitions to `APPROVED`.
   - Trigger: `reject()` [by Admin] $ightarrow$ Transitions to `REJECTED`.
3. **`APPROVED`**: Prerequisite and standing verification cleared.
   - Trigger: `finalizeEnrollment()` [by Admin] $ightarrow$ Transitions to `ENROLLED` (increments seat counter).
   - Trigger: `reject()` $ightarrow$ Transitions to `REJECTED`.
4. **`ENROLLED`**: Active academic enrollment; student receives attendance and grade evaluations.
   - Trigger: `drop()` [Student or Admin before deadline] $ightarrow$ Transitions to `DROPPED` (decrements seat counter).
5. **`DROPPED`** & **`REJECTED`**: Terminal final states (`[*]`).

## 4. Source Code Mapping
- State Interface: `com.campusconnect.pattern.state.EnrollmentState`
- Concrete States: `DraftState`, `SubmittedState`, `ApprovedState`, `EnrolledState`, `DroppedState`, `RejectedState`
- Context Entity: `com.campusconnect.model.Enrollment`
