export interface TranscriptHeader {
  universityName: string;
  documentTitle: string;
  transcriptNumber: string;
  issueDate: string;
  verificationHash: string;
}

export interface StudentBio {
  studentId: number;
  fullName: string;
  rollNumber: string;
  departmentCode: string;
  departmentName: string;
  currentSemester: number;
  email: string;
}

export interface CourseGradeEntry {
  courseCode: string;
  courseTitle: string;
  credits: number;
  marksObtained: number;
  letterGrade: string;
  gradePoint: number;
  remark: string;
}

export interface SemesterTranscript {
  semester: number;
  courses: CourseGradeEntry[];
  semesterCredits: number;
  semesterGpa: number;
}

export interface TranscriptSummary {
  totalCreditsAttempted: number;
  totalCreditsEarned: number;
  cumulativeGpa: number;
  academicStanding: string;
  totalCoursesCompleted: number;
}

export interface TranscriptSignatures {
  registrarName: string;
  officialSeal: string;
  verificationUrl: string;
}

export interface AcademicTranscriptReport {
  header: TranscriptHeader;
  studentBio: StudentBio;
  semesterRecords: SemesterTranscript[];
  summary: TranscriptSummary;
  signatures: TranscriptSignatures;
}
