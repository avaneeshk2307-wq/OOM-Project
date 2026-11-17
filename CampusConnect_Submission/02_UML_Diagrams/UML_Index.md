# CampusConnect — UML Architectural Diagrams Index

This directory contains the complete set of eight Object-Oriented Methodology (OOM) UML diagrams for the **CampusConnect Smart Campus Management System**. Every diagram is based strictly on the verified source code, JPA models, and database migrations.

---

## Diagrams Directory Structure

- `PlantUML_Source/`: Fully editable PlantUML (`.puml`) source code files.
- `Rendered_PNG/`: High-resolution rendered graphical representations (`.png`).
- `Diagram_Descriptions/`: Formal academic descriptions, relationship breakdowns, and source code mappings (`.md`).

---

## Master Diagrams Catalog

| # | Diagram Name | Diagram Type | Source File | Rendered Image | Detailed Description |
| :-: | :--- | :--- | :--- | :--- | :--- |
| **1** | **System Use Case Model** | Behavioral / Use Case | [`01_use_case_diagram.puml`](PlantUML_Source/01_use_case_diagram.puml) | [`01_use_case_diagram.png`](Rendered_PNG/01_use_case_diagram.png) | [`01_use_case_description.md`](Diagram_Descriptions/01_use_case_description.md) |
| **2** | **Domain Model & Pattern Class Diagram** | Structural / Class | [`02_class_diagram.puml`](PlantUML_Source/02_class_diagram.puml) | [`02_class_diagram.png`](Rendered_PNG/02_class_diagram.png) | [`02_class_description.md`](Diagram_Descriptions/02_class_description.md) |
| **3** | **Authentication & JWT Sequence Diagram** | Interaction / Sequence | [`03_sequence_diagram_auth.puml`](PlantUML_Source/03_sequence_diagram_auth.puml) | [`03_sequence_diagram_auth.png`](Rendered_PNG/03_sequence_diagram_auth.png) | [`03_sequence_description.md`](Diagram_Descriptions/03_sequence_description.md) |
| **4** | **Student Enrollment Activity Workflow** | Behavioral / Activity | [`04_activity_diagram_enrollment.puml`](PlantUML_Source/04_activity_diagram_enrollment.puml) | [`04_activity_diagram_enrollment.png`](Rendered_PNG/04_activity_diagram_enrollment.png) | [`04_activity_description.md`](Diagram_Descriptions/04_activity_description.md) |
| **5** | **Enrollment State Machine Lifecycle** | Behavioral / State Machine | [`05_state_machine_enrollment.puml`](PlantUML_Source/05_state_machine_enrollment.puml) | [`05_state_machine_enrollment.png`](Rendered_PNG/05_state_machine_enrollment.png) | [`05_state_machine_description.md`](Diagram_Descriptions/05_state_machine_description.md) |
| **6** | **Multi-Tier Component Architecture** | Structural / Component | [`06_component_diagram.puml`](PlantUML_Source/06_component_diagram.puml) | [`06_component_diagram.png`](Rendered_PNG/06_component_diagram.png) | [`06_component_description.md`](Diagram_Descriptions/06_component_description.md) |
| **7** | **Physical & Virtual Deployment Topology** | Structural / Deployment | [`07_deployment_diagram.puml`](PlantUML_Source/07_deployment_diagram.puml) | [`07_deployment_diagram.png`](Rendered_PNG/07_deployment_diagram.png) | [`07_deployment_description.md`](Diagram_Descriptions/07_deployment_description.md) |
| **8** | **Relational Database ER Diagram** | Data / Relational Model | [`08_er_diagram.puml`](PlantUML_Source/08_er_diagram.puml) | [`08_er_diagram.png`](Rendered_PNG/08_er_diagram.png) | [`08_er_description.md`](Diagram_Descriptions/08_er_description.md) |

---

## Academic Assessment Highlights

1. **Class Diagram Alignment**: All entity attributes (`accessLevel`, `registeredAt`, `weightagePercentage`) precisely mirror JPA entity fields and Flyway SQL migration columns.
2. **Behavioral Accuracy**: Sequence and Activity diagrams follow the real code implementation paths, including Spring Security BCrypt hashing and exception handling gates.
3. **State Pattern Demonstration**: The State Machine diagram maps 1:1 to the `com.campusconnect.pattern.state` package.
4. **Relational Integrity**: The ER diagram includes composite unique constraints (`UQ(student_id, course_id, semester)`, etc.) verified against `V1__init_schema.sql`.
