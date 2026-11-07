export type AssessmentType = 'QUIZ' | 'ASSIGNMENT' | 'MIDTERM' | 'FINAL_EXAM' | 'PROJECT';

export interface AssessmentResponse {
  id: number;
  courseId: number;
  courseCode: string;
  name: string;
  type: AssessmentType;
  maxMarks: number;
  weightagePercentage: number;
  createdAt: string;
}

export interface CreateAssessmentRequest {
  courseId: number;
  name: string;
  type: AssessmentType;
  maxMarks: number;
  weightagePercentage: number;
}

export interface SubmitGradeRequest {
  enrollmentId: number;
  assessmentId: number;
  marksObtained: number;
  gradingMode?: 'ABSOLUTE' | 'RELATIVE';
}

export interface CourseGradeDistribution {
  courseId: number;
  courseCode: string;
  courseTitle: string;
  totalGradedAssessments: number;
  averagePercentage: number;
  standardDeviation: number;
  highestPercentage: number;
  lowestPercentage: number;
  absoluteDistribution: Record<string, number>;
  relativeDistribution: Record<string, number>;
}

export interface GradeResponse {
  id: number;
  enrollmentId: number;
  studentId: number;
  studentName: string;
  rollNumber: string;
  assessmentId: number;
  assessmentName: string;
  assessmentType: AssessmentType;
  marksObtained: number;
  maxMarks: number;
  letterGrade: string;
  gradePoint: number;
  remark: string;
  gradedAt: string;
}

export interface StudentCourseGradeReport {
  courseId: number;
  courseCode: string;
  courseTitle: string;
  credits: number;
  enrollmentId: number;
  assessments: GradeResponse[];
  totalWeightedScore: number;
  finalGrade: string;
  gradePoint: number;
}
