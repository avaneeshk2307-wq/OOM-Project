export interface DepartmentMetric {
  departmentId: number;
  code: string;
  name: string;
  studentCount: number;
  facultyCount: number;
  courseCount: number;
}

export interface CourseCapacityMetric {
  courseId: number;
  code: string;
  title: string;
  enrolledStudents: number;
  capacity: number;
  utilizationPercentage: number;
}

export interface AttendanceHealthMetric {
  totalSessionRecords: number;
  totalPresentRecords: number;
  campusWidePercentage: number;
  studentsInGoodStanding: number;
  studentsWithShortageAlert: number;
}

export interface FacultyWorkloadMetric {
  facultyId: number;
  facultyName: string;
  departmentCode: string;
  assignedCoursesCount: number;
  totalCreditsTaught: number;
  weeklyScheduledSlots: number;
}

export interface CampusAnalyticsResponse {
  departmentMetrics: DepartmentMetric[];
  courseCapacityMetrics: CourseCapacityMetric[];
  attendanceHealth: AttendanceHealthMetric;
  gradeDistribution: Record<string, number>;
  facultyWorkload: FacultyWorkloadMetric[];
}
