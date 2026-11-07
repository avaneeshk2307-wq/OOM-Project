export type AttendanceStatus = 'PRESENT' | 'ABSENT' | 'LATE';

export interface RecordEntryRequest {
  studentId: number;
  status: AttendanceStatus;
  remarks?: string;
}

export interface AttendanceRecordResponse {
  id: number;
  studentId: number;
  studentName: string;
  rollNumber: string;
  status: AttendanceStatus;
  remarks: string;
}

export interface AttendanceSessionResponse {
  id: number;
  courseId: number;
  courseCode: string;
  courseTitle: string;
  sessionDate: string;
  topic: string;
  totalRecords: number;
  presentCount: number;
  createdAt: string;
  records: AttendanceRecordResponse[];
}

export interface StudentSessionHistoryItem {
  sessionId: number;
  sessionDate: string;
  topic: string;
  status: AttendanceStatus;
  remarks: string;
}

export interface StudentCourseAttendanceSummary {
  courseId: number;
  courseCode: string;
  courseTitle: string;
  credits: number;
  totalSessions: number;
  attendedSessions: number;
  percentage: number;
  lowAttendanceWarning: boolean;
  history: StudentSessionHistoryItem[];
}
