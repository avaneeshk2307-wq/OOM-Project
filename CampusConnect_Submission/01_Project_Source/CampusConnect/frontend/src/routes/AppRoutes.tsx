import React, { Suspense } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { LoginPage } from '../pages/auth/LoginPage';
import { UnauthorizedPage } from '../pages/auth/UnauthorizedPage';
import { RoleGuard } from '../components/layout/RoleGuard';
import { DashboardLayout } from '../components/layout/DashboardLayout';

// Lazy-loaded Admin Pages
const AdminOverview = React.lazy(() => import('../pages/admin/AdminOverview').then(m => ({ default: m.AdminOverview })));
const DepartmentManagement = React.lazy(() => import('../pages/admin/DepartmentManagement').then(m => ({ default: m.DepartmentManagement })));
const CourseManagement = React.lazy(() => import('../pages/admin/CourseManagement').then(m => ({ default: m.CourseManagement })));
const FacultyManagement = React.lazy(() => import('../pages/admin/FacultyManagement').then(m => ({ default: m.FacultyManagement })));
const StudentManagement = React.lazy(() => import('../pages/admin/StudentManagement').then(m => ({ default: m.StudentManagement })));
const AdminEnrollmentManagement = React.lazy(() => import('../pages/admin/AdminEnrollmentManagement').then(m => ({ default: m.AdminEnrollmentManagement })));
const AdminAnnouncements = React.lazy(() => import('../pages/admin/AdminAnnouncements').then(m => ({ default: m.AdminAnnouncements })));
const AdminTimetable = React.lazy(() => import('../pages/admin/AdminTimetable').then(m => ({ default: m.AdminTimetable })));
const AdminAnalytics = React.lazy(() => import('../pages/admin/AdminAnalytics').then(m => ({ default: m.AdminAnalytics })));
const AdminAuditLogs = React.lazy(() => import('../pages/admin/AdminAuditLogs').then(m => ({ default: m.AdminAuditLogs })));

// Lazy-loaded Faculty Pages
const FacultyOverview = React.lazy(() => import('../pages/faculty/FacultyOverview').then(m => ({ default: m.FacultyOverview })));
const FacultyTimetable = React.lazy(() => import('../pages/faculty/FacultyTimetable').then(m => ({ default: m.FacultyTimetable })));
const AttendanceMarking = React.lazy(() => import('../pages/faculty/AttendanceMarking').then(m => ({ default: m.AttendanceMarking })));
const GradeEntry = React.lazy(() => import('../pages/faculty/GradeEntry').then(m => ({ default: m.GradeEntry })));

// Lazy-loaded Student Pages
const StudentOverview = React.lazy(() => import('../pages/student/StudentOverview').then(m => ({ default: m.StudentOverview })));
const StudentTimetable = React.lazy(() => import('../pages/student/StudentTimetable').then(m => ({ default: m.StudentTimetable })));
const StudentCatalog = React.lazy(() => import('../pages/student/StudentCatalog').then(m => ({ default: m.StudentCatalog })));
const StudentEnrollments = React.lazy(() => import('../pages/student/StudentEnrollments').then(m => ({ default: m.StudentEnrollments })));
const StudentAttendance = React.lazy(() => import('../pages/student/StudentAttendance').then(m => ({ default: m.StudentAttendance })));
const StudentGrades = React.lazy(() => import('../pages/student/StudentGrades').then(m => ({ default: m.StudentGrades })));

const RouteLoadingFallback = () => (
  <div className="min-h-screen flex items-center justify-center bg-slate-50">
    <div className="w-8 h-8 border-3 border-indigo-600 border-t-transparent rounded-full animate-spin"></div>
  </div>
);

export const AppRoutes: React.FC = () => {
  return (
    <Suspense fallback={<RouteLoadingFallback />}>
      <Routes>
        {/* Public Routes */}
        <Route path="/" element={<LoginPage />} />
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
    </Suspense>
  );
};
