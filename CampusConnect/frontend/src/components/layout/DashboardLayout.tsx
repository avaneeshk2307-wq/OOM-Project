import React from 'react';
import { Outlet, useNavigate, useLocation, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { NotificationBell } from './NotificationBell';
import { 
  GraduationCap, 
  LogOut, 
  Users, 
  BookOpen, 
  Building2, 
  CalendarCheck, 
  Award, 
  Bell, 
  LayoutDashboard,
  Briefcase,
  Calendar
} from 'lucide-react';

export const DashboardLayout: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const getRoleLinks = () => {
    switch (user?.role) {
      case 'ROLE_ADMIN':
        return [
          { name: 'Overview', path: '/admin', icon: LayoutDashboard },
          { name: 'Students', path: '/admin/students', icon: Users },
          { name: 'Faculty', path: '/admin/faculty', icon: Briefcase },
          { name: 'Departments', path: '/admin/departments', icon: Building2 },
          { name: 'Courses', path: '/admin/courses', icon: BookOpen },
          { name: 'Enrollments', path: '/admin/enrollments', icon: Award },
          { name: 'Timetable', path: '/admin/timetable', icon: Calendar },
          { name: 'Announcements', path: '/admin/announcements', icon: Bell },
        ];
      case 'ROLE_FACULTY':
        return [
          { name: 'Overview', path: '/faculty', icon: LayoutDashboard },
          { name: 'Assigned Courses', path: '/faculty/courses', icon: BookOpen },
          { name: 'My Schedule', path: '/faculty/timetable', icon: Calendar },
          { name: 'Mark Attendance', path: '/faculty/attendance', icon: CalendarCheck },
          { name: 'Enter Grades', path: '/faculty/grades', icon: Award },
          { name: 'Announcements', path: '/faculty/announcements', icon: Bell },
        ];
      case 'ROLE_STUDENT':
        return [
          { name: 'Overview', path: '/student', icon: LayoutDashboard },
          { name: 'Class Schedule', path: '/student/timetable', icon: Calendar },
          { name: 'Course Catalog', path: '/student/catalog', icon: BookOpen },
          { name: 'My Enrollments', path: '/student/courses', icon: Award },
          { name: 'Attendance Record', path: '/student/attendance', icon: CalendarCheck },
          { name: 'Grades & Results', path: '/student/grades', icon: Award },
          { name: 'Announcements', path: '/student/announcements', icon: Bell },
        ];
      default:
        return [];
    }
  };

  const roleBadgeStyle = () => {
    switch (user?.role) {
      case 'ROLE_ADMIN':
        return 'bg-purple-100 text-purple-800 border-purple-200';
      case 'ROLE_FACULTY':
        return 'bg-blue-100 text-blue-800 border-blue-200';
      case 'ROLE_STUDENT':
        return 'bg-emerald-100 text-emerald-800 border-emerald-200';
      default:
        return 'bg-slate-100 text-slate-800 border-slate-200';
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col font-sans">
      {/* Top Navigation */}
      <header className="bg-brand-navy text-white shadow-md border-b border-slate-800 sticky top-0 z-30">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <Link to="/" className="flex items-center gap-2.5">
              <div className="p-2 bg-indigo-600 rounded-lg shadow-inner">
                <GraduationCap className="w-5 h-5 text-white" />
              </div>
              <span className="font-bold text-lg tracking-tight text-white">CampusConnect</span>
            </Link>
            <span className={`ml-3 text-xs font-semibold px-2.5 py-0.5 rounded-full border ${roleBadgeStyle()}`}>
              {user?.role?.replace('ROLE_', '')}
            </span>
          </div>

          <div className="flex items-center gap-4">
            <NotificationBell />

            <div className="hidden sm:flex flex-col text-right">
              <span className="text-sm font-medium text-white">{user?.fullName}</span>
              <span className="text-xs text-slate-400">
                {user?.roleSpecificCode ? `${user.roleSpecificCode} • ` : ''}{user?.departmentName || user?.email}
              </span>
            </div>

            <button
              onClick={handleLogout}
              title="Logout"
              className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-medium bg-slate-800 hover:bg-red-950/40 text-slate-300 hover:text-red-400 border border-slate-700 hover:border-red-900/50 transition-colors"
            >
              <LogOut className="w-3.5 h-3.5" />
              <span>Sign Out</span>
            </button>
          </div>
        </div>
      </header>

      {/* Main Container */}
      <div className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-6 flex flex-col md:flex-row gap-6">
        {/* Sidebar Nav */}
        <aside className="w-full md:w-64 shrink-0">
          <nav className="bg-white rounded-xl border border-slate-200/80 p-3 shadow-sm space-y-1">
            <div className="px-3 py-2 text-xs font-semibold uppercase tracking-wider text-slate-400">
              Navigation
            </div>
            {getRoleLinks().map((item) => {
              const Icon = item.icon;
              const isActive = location.pathname === item.path;
              return (
                <Link
                  key={item.path}
                  to={item.path}
                  className={`flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                    isActive
                      ? 'bg-indigo-50 text-indigo-700 font-semibold shadow-xs'
                      : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'
                  }`}
                >
                  <Icon className={`w-4 h-4 ${isActive ? 'text-indigo-600' : 'text-slate-400'}`} />
                  <span>{item.name}</span>
                </Link>
              );
            })}
          </nav>
        </aside>

        {/* Dynamic Content Area */}
        <main className="flex-1 min-w-0">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
