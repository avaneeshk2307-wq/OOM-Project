export interface Department {
  id: number;
  code: string;
  name: string;
  description?: string;
  studentCount: number;
  facultyCount: number;
  courseCount: number;
  createdAt: string;
}

export interface Course {
  id: number;
  code: string;
  title: string;
  description?: string;
  credits: number;
  semester: number;
  capacity: number;
  enrolledCount: number;
  departmentId?: number;
  departmentName?: string;
  instructorId?: number;
  instructorName?: string;
  createdAt: string;
}

export interface Faculty {
  id: number;
  name: string;
  email: string;
  phone?: string;
  employeeCode: string;
  designation: string;
  departmentId?: number;
  departmentName?: string;
  courseCount: number;
  accountStatus: string;
  createdAt: string;
}

export interface Student {
  id: number;
  name: string;
  email: string;
  phone?: string;
  rollNumber: string;
  semester: number;
  departmentId?: number;
  departmentName?: string;
  enrolledCoursesCount: number;
  accountStatus: string;
  createdAt: string;
}

export interface AdminStats {
  totalStudents: number;
  totalFaculty: number;
  totalCourses: number;
  totalDepartments: number;
  totalEnrollments: number;
  studentsByDepartment: Record<string, number>;
}
