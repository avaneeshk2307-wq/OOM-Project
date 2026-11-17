# CampusConnect: Missing Information & Customization Guide

This document catalogs all editable placeholders embedded within the submission package. Before final submission to your university examination committee, replace the bracketed placeholders (`[...]`) with your personal, guide, and institutional details.

---

## 1. Inventory of Editable Placeholders

| Placeholder Token | Description | Example Replacement Value | Files Where It Appears |
|:---|:---|:---|:---|
| `[Student Name]` | Full legal name of the candidate | Johnathan Doe | Report (`.docx`, `.pdf`), PPTX, Guides |
| `[Roll Number]` | University matriculation / roll number | 21CSB042 / 2022UGCS015 | Report (`.docx`, `.pdf`), PPTX, Guides |
| `[Department Name]` | Academic department | Computer Science & Engineering | Report (`.docx`, `.pdf`), PPTX, Guides |
| `[University / Institution Name]` | University or college name | State University of Technology | Report (`.docx`, `.pdf`), PPTX, Guides |
| `[Faculty Guide Name]` | Name of project supervisor / guide | Dr. Robert A. Smith | Report (`.docx`, `.pdf`), PPTX, Guides |
| `[Head of Department]` | Name of the Department Head | Dr. Alan M. Turing | Report Certificate (`.docx`, `.pdf`) |
| `[Academic Session]` | Academic calendar academic year | 2025 – 2026 | Report Cover, Certificate, PPTX |
| `[Semester / Year]` | Current semester and academic year | 7th Semester / 4th Year | Report Cover, PPTX Slide 1 |
| `[Submission Date]` | Formal date of submission / defense | November 15, 2026 | Report Cover, Certificate, PPTX |
| `[Campus / City]` | Geographic location of institution | City Center Campus, New Delhi | Candidate Declaration (`.docx`, `.pdf`) |

---

## 2. How to Update Placeholders Across Deliverables

### 2.1 Microsoft Word Report (`CampusConnect_OOM_Report.docx`)
1. Open `CampusConnect_Submission/03_Project_Report/CampusConnect_OOM_Report.docx` in Microsoft Word or LibreOffice Writer.
2. Press **`Ctrl + H`** (or **`Cmd + H`** on macOS) to open the **Find and Replace** dialog.
3. In **Find what**, type `[Student Name]`. In **Replace with**, type your actual name. Click **Replace All**.
4. Repeat for `[Roll Number]`, `[Department Name]`, `[University / Institution Name]`, `[Faculty Guide Name]`, `[Head of Department]`, `[Academic Session]`, `[Semester / Year]`, and `[Submission Date]`.
5. Save the document (`Ctrl + S`).

---

### 2.2 PowerPoint Presentation (`CampusConnect_OOM_Presentation.pptx`)
1. Open `CampusConnect_Submission/04_Presentation/CampusConnect_OOM_Presentation.pptx` in Microsoft PowerPoint or Google Slides.
2. Press **`Ctrl + H`** to open **Find and Replace**.
3. Replace `[Student Name]`, `[Roll Number]`, `[Department Name]`, and `[Faculty Guide Name]`.
4. Inspect Slide 1 (Title), Slide 18 (Closing / Q&A), and notes in `Presentation_Guide.md`.
5. Save the presentation (`Ctrl + S`).

---

### 2.3 Automated Script Update (Recommended for PDF & Regenerating DOCX/PPTX)
If you wish to regenerate the PDF, DOCX, and PPTX with your actual details automatically:

1. Open `scratch/build_docx_report.py`, `scratch/build_pdf_report.py`, and `scratch/build_presentation.py`.
2. Update the string placeholders in the cover page and metadata blocks.
3. Run the regeneration commands:
   ```bash
   python scratch/build_docx_report.py
   python scratch/build_pdf_report.py
   python scratch/build_presentation.py
   ```
4. All three deliverables (`.docx`, `.pdf`, `.pptx`) will immediately recompile with your exact personal credentials in place!

---

### 2.4 Markdown Documentation Files (`*.md`)
If updating markdown files across `04_Presentation/`, `05_Testing_Documentation/`, `06_Technical_Documentation/`, and `07_Submission_Checklist/`:
1. Open the workspace in VS Code or any text editor.
2. Press **`Ctrl + Shift + H`** (Global Find and Replace across files).
3. Search for each placeholder token (e.g. `[Student Name]`) and replace with your value.
