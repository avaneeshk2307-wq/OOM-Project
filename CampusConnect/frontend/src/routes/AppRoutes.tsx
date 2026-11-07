import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { LoginPage } from '../pages/auth/LoginPage';
import { UnauthorizedPage } from '../pages/auth/UnauthorizedPage';
import { RoleGuard } from '../components/layout/RoleGuard';
import { DashboardLayout } from '../components/layout/DashboardLayout';
import { AdminOverview } from '../pages/admin/AdminOverview';
import { DepartmentManagement } from '../pages/admin/DepartmentManagement';
import { CourseManagement } from '../pages/admin/CourseManagement';
import { FacultyManagement } from '../pages/admin/FacultyManagement';
import { StudentManagement } from '../pages/admin/StudentManagement';
import { AdminEnrollmentManagement } from '../pages/admin/AdminEnrollmentManagement';
import { AdminAnnouncements } from '../pages/admin/AdminAnnouncements';
import { FacultyOverview } from '../pages/faculty/FacultyOverview';
import { StudentOverview } from '../pages/student/StudentOverview';
import { StudentCatalog } from '../pages/student/StudentCatalog';
import { StudentEnrollments } from '../pages/student/StudentEnrollments';
import { AttendanceMarking } from '../pages/faculty/AttendanceMarking';
import { GradeEntry } from '../pages/faculty/GradeEntry';
import { StudentAttendance } from '../pages/student/StudentAttendance';
import { StudentGrades } from '../pages/student/StudentGrades';
import { AdminTimetable } from '../pages/admin/AdminTimetable';
import { AdminAnalytics } from '../pages/admin/AdminAnalytics';
import { AdminAuditLogs } from '../pages/admin/AdminAuditLogs';
import { FacultyTimetable } from '../pages/faculty/FacultyTimetable';
import { StudentTimetable } from '../pages/student/StudentTimetable';
import LandingPage from '../pages/LandingPage';

export const AppRoutes: React.FC = () => {
  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/" element={<LandingPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/unauthorized" element={<UnauthorizedPage />} />

      {/* Admin Protected Routes */}
      <Route element={<RoleGuard allowedRoles={['ROLE_ADMIN']} />}>
        <Route path="/admin" element={<DashboardLayout />}>
          <Route index element={<AdminOverview />} />
          <Route path="departments" element={<DepartmentManagement />} />
          <Route path="courses" element={<CourseManagement />} />
          <Route path="faculty" element={<FacultyManagement />} />
          <Route path="students" element={<StudentManagement />} />
          <Route path="enrollments" element={<AdminEnrollmentManagement />} />
          <Route path="timetable" element={<AdminTimetable />} />
          <Route path="analytics" element={<AdminAnalytics />} />
          <Route path="audit-logs" element={<AdminAuditLogs />} />
          <Route path="announcements" element={<AdminAnnouncements />} />
        </Route>
      </Route>

      {/* Faculty Protected Routes */}
      <Route element={<RoleGuard allowedRoles={['ROLE_ADMIN', 'ROLE_FACULTY']} />}>
        <Route path="/faculty" element={<DashboardLayout />}>
          <Route index element={<FacultyOverview />} />
          <Route path="courses" element={<FacultyOverview />} />
          <Route path="timetable" element={<FacultyTimetable />} />
          <Route path="attendance" element={<AttendanceMarking />} />
          <Route path="grades" element={<GradeEntry />} />
          <Route path="announcements" element={<FacultyOverview />} />
        </Route>
      </Route>

      {/* Student Protected Routes */}
      <Route element={<RoleGuard allowedRoles={['ROLE_ADMIN', 'ROLE_STUDENT']} />}>
        <Route path="/student" element={<DashboardLayout />}>
          <Route index element={<StudentOverview />} />
          <Route path="timetable" element={<StudentTimetable />} />
          <Route path="catalog" element={<StudentCatalog />} />
          <Route path="courses" element={<StudentEnrollments />} />
          <Route path="attendance" element={<StudentAttendance />} />
          <Route path="grades" element={<StudentGrades />} />
          <Route path="announcements" element={<StudentOverview />} />
        </Route>
      </Route>

      {/* Catch-all redirect */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};
