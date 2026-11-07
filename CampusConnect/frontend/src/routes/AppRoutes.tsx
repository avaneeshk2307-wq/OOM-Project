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
import { FacultyOverview } from '../pages/faculty/FacultyOverview';
import { StudentOverview } from '../pages/student/StudentOverview';
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
          <Route path="enrollments" element={<AdminOverview />} />
          <Route path="announcements" element={<AdminOverview />} />
        </Route>
      </Route>

      {/* Faculty Protected Routes */}
      <Route element={<RoleGuard allowedRoles={['ROLE_ADMIN', 'ROLE_FACULTY']} />}>
        <Route path="/faculty" element={<DashboardLayout />}>
          <Route index element={<FacultyOverview />} />
          <Route path="courses" element={<FacultyOverview />} />
          <Route path="attendance" element={<FacultyOverview />} />
          <Route path="grades" element={<FacultyOverview />} />
          <Route path="announcements" element={<FacultyOverview />} />
        </Route>
      </Route>

      {/* Student Protected Routes */}
      <Route element={<RoleGuard allowedRoles={['ROLE_ADMIN', 'ROLE_STUDENT']} />}>
        <Route path="/student" element={<DashboardLayout />}>
          <Route index element={<StudentOverview />} />
          <Route path="catalog" element={<StudentOverview />} />
          <Route path="courses" element={<StudentOverview />} />
          <Route path="attendance" element={<StudentOverview />} />
          <Route path="grades" element={<StudentOverview />} />
          <Route path="announcements" element={<StudentOverview />} />
        </Route>
      </Route>

      {/* Catch-all redirect */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};
