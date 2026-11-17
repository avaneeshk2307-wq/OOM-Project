# CampusConnect: Missing Information & Customization Guide (3-Member Academic Team)

This document catalogs all editable placeholders embedded within the submission package. Before final submission to your university examination committee, replace the bracketed placeholders (`[...]`) with your three team members' names, roll numbers, guide, and institutional details.

---

## 1. Inventory of Editable Placeholders

| Placeholder Token | Description | Example Replacement Value | Files Where It Appears |
|:---|:---|:---|:---|
| `[Student 1 Name]` | Full legal name of Student Member 1 (Lead Architecture) | Avaneesh Kumar | Report, Certificate, Declaration, PPTX, Guides |
| `[Roll Number 1]` | University roll / matriculation number of Student 1 | 23BCS041 | Report, Certificate, Declaration, PPTX, Guides |
| `[Student 2 Name]` | Full legal name of Student Member 2 (Design & Frontend) | [Student 2 Full Name] | Report, Certificate, Declaration, PPTX, Guides |
| `[Roll Number 2]` | University roll / matriculation number of Student 2 | [Roll Number 2] | Report, Certificate, Declaration, PPTX, Guides |
| `[Student 3 Name]` | Full legal name of Student Member 3 (Persistence & QA) | [Student 3 Full Name] | Report, Certificate, Declaration, PPTX, Guides |
| `[Roll Number 3]` | University roll / matriculation number of Student 3 | [Roll Number 3] | Report, Certificate, Declaration, PPTX, Guides |
| `[Department Name]` | Academic department | Department of Computer Science & Engineering | Report (`.docx`, `.pdf`), PPTX, Guides |
| `[University / Institution Name]` | University or college name | State Technical University | Report (`.docx`, `.pdf`), PPTX, Guides |
| `[Faculty Guide Name]` | Name of project supervisor / faculty guide | Dr. [Faculty Guide Name] | Report (`.docx`, `.pdf`), PPTX, Guides |
| `[Head of Department]` | Name of the Department Head | Prof. [HOD Name] | Report Certificate (`.docx`, `.pdf`) |
| `[Academic Session]` | Academic calendar academic year | 2026 – 2027 | Report Cover, Certificate, PPTX |
| `[Semester / Year]` | Current semester and academic year | 4th Semester / 2nd Year B.Tech | Report Cover, PPTX Slide 1 |
| `[Submission Date]` | Formal date of submission / viva defense | November 17, 2026 | Report Cover, Certificate, PPTX |
| `[Campus / City]` | Geographic location of institution | Campus City, State, India | Candidate Declaration (`.docx`, `.pdf`) |

---

## 2. Three-Member Team Responsibility Matrix

The submission materials allocate balanced, verifiable responsibilities across the three student members:

```
+---------------------------------------------------------------------------------------------------------+
| THREE-MEMBER CONTRIBUTION BREAKDOWN                                                                     |
+-------------------+----------------------------+-----------------------------+--------------------------+
| Member Details    | Primary Domain             | Functional Modules          | Documentation & QA       |
+-------------------+----------------------------+-----------------------------+--------------------------+
| Student Member 1  | Backend Architecture &     | - Spring Security & JWT     | - SRS & Architecture     |
| Name: [Student 1] | Core OOM Patterns          | - GoF Strategy Pattern      | - Security IDOR Remed.   |
| Roll: [Roll 1]    |                            | - Audit Logging Interceptor | - Chapters 1, 2, 7, 8    |
|                   |                            | - Auth & Admin Controllers  | - Presentation Slides 1-6|
+-------------------+----------------------------+-----------------------------+--------------------------+
| Student Member 2  | Frontend Engineering &     | - React 18 / TypeScript UI  | - Use Case & Activity    |
| Name: [Student 2] | UI State Workflow          | - GoF State Machine Pattern | - State Diagram Modeling |
| Roll: [Roll 2]    |                            | - Enrollment Wizard UI      | - Chapters 3, 4, 6, 11   |
|                   |                            | - Timetable Conflict UI     | - Presentation Slides 7-12|
+-------------------+----------------------------+-----------------------------+--------------------------+
| Student Member 3  | Relational Persistence,    | - PostgreSQL 16 Schema / 3NF| - ER Modeling & DDL      |
| Name: [Student 3] | Testing & QA Lead          | - GoF Builder Transcripts   | - Test Execution (57/18) |
| Roll: [Roll 3]    |                            | - Flyway Migrations (V1/V2) | - Chapters 5, 9, 10, 12  |
|                   |                            | - Automated Test Suite      | - Presentation Slides 13-18|
+-------------------+----------------------------+-----------------------------+--------------------------+
```

---

## 3. How to Update Placeholders Across Deliverables

### 3.1 Microsoft Word Report (`CampusConnect_OOM_Report.docx`)
1. Open `CampusConnect_Submission/03_Project_Report/CampusConnect_OOM_Report.docx` in Microsoft Word or LibreOffice Writer.
2. Press **`Ctrl + H`** (or **`Cmd + H`** on macOS) to open the **Find and Replace** dialog.
3. Replace each bracketed placeholder:
   - `[Student 1 Name]` $\to$ Your actual name
   - `[Roll Number 1]` $\to$ Your actual roll number
   - `[Student 2 Name]` $\to$ Member 2's name
   - `[Roll Number 2]` $\to$ Member 2's roll number
   - `[Student 3 Name]` $\to$ Member 3's name
   - `[Roll Number 3]` $\to$ Member 3's roll number
   - `[University / Institution Name]` $\to$ Your college/university name
   - `[Faculty Guide Name]` $\to$ Your guide's name
4. Save the document (`Ctrl + S`).

### 3.2 PowerPoint Presentation (`CampusConnect_OOM_Presentation.pptx`)
1. Open `CampusConnect_Submission/04_Presentation/CampusConnect_OOM_Presentation.pptx` in Microsoft PowerPoint or Google Slides.
2. Press **`Ctrl + H`** to perform Find and Replace across slides.
3. Verify Slide 1 (Title Card with 3 student cards) and Slide 17 (Team Matrix).
4. Save the presentation (`Ctrl + S`).
