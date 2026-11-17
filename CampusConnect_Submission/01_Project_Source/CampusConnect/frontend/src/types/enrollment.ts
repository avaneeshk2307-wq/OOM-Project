export type EnrollmentStatus = 
  | 'DRAFT' 
  | 'SUBMITTED' 
  | 'APPROVED' 
  | 'ENROLLED' 
  | 'DROPPED' 
  | 'REJECTED';

export interface Enrollment {
  id: number;
  studentId: number;
  studentName: string;
  rollNumber: string;
  courseId: number;
  courseCode: string;
  courseTitle: string;
  credits: number;
  semester: number;
  status: EnrollmentStatus;
  registeredAt: string;
}
