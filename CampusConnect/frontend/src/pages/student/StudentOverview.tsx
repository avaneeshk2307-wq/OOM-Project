import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { apiClient } from '../../api/client';
import { Enrollment } from '../../types/enrollment';
import { StudentCourseAttendanceSummary } from '../../types/attendance';
import { StudentCourseGradeReport } from '../../types/grades';
import { Announcement } from '../../types/announcement';
import { 
  GraduationCap, 
  BookOpen, 
  CalendarCheck, 
  Award, 
  Bell, 
  ArrowRight, 
  Calendar,
  AlertTriangle
} from 'lucide-react';
import { Link } from 'react-router-dom';

export const StudentOverview: React.FC = () => {
  const { user } = useAuth();
  const [enrollments, setEnrollments] = useState<Enrollment[]>([]);
  const [attendance, setAttendance] = useState<StudentCourseAttendanceSummary[]>([]);
  const [grades, setGrades] = useState<StudentCourseGradeReport[]>([]);
  const [announcements, setAnnouncements] = useState<Announcement[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const loadStudentData = async () => {
      try {
        setLoading(true);
        const [enrollRes, attRes, gradesRes, annRes] = await Promise.all([
          apiClient.get<Enrollment[]>('/enrollments/my-enrollments'),
          apiClient.get<StudentCourseAttendanceSummary[]>('/attendance/my-attendance'),
          apiClient.get<StudentCourseGradeReport[]>('/grades/my-grades'),
          apiClient.get<Announcement[]>('/announcements')
        ]);
        setEnrollments(enrollRes.data);
        setAttendance(attRes.data);
        setGrades(gradesRes.data);
        setAnnouncements(annRes.data);
      } catch (err) {
        console.error('Failed to load student dashboard data', err);
      } finally {
        setLoading(false);
      }
    };
    loadStudentData();
  }, []);

  const totalClasses = attendance.reduce((acc, curr) => acc + curr.totalSessions, 0);
  const attendedClasses = attendance.reduce((acc, curr) => acc + curr.attendedSessions, 0);
  const overallAttendancePct = totalClasses > 0 ? Math.round((attendedClasses / totalClasses) * 100) : 100;

  const totalCredits = grades.reduce((acc, curr) => acc + curr.credits, 0);
  const totalWeightedPoints = grades.reduce((acc, curr) => acc + curr.gradePoint * curr.credits, 0);
  const cgpa = totalCredits > 0 ? (totalWeightedPoints / totalCredits).toFixed(2) : '0.00';

  const hasShortage = attendance.some((a) => a.lowAttendanceWarning);

  return (
    <div className="space-y-6">
      {/* Welcome Banner */}
      <div className="bg-white p-6 rounded-xl border border-slate-200/80 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Student Academic Portal</h1>
          <p className="text-sm text-slate-500 mt-1">Welcome back, {user?.fullName}. Here is your real-time academic progress.</p>
        </div>
        <div className="p-3 bg-emerald-50 text-emerald-700 rounded-xl border border-emerald-100 flex items-center gap-2 self-start md:self-auto">
          <GraduationCap className="w-5 h-5" />
          <span className="text-xs font-semibold">{user?.roleSpecificCode || 'STUDENT'}</span>
        </div>
      </div>

      {/* Attendance Shortage Warning Alert */}
      {hasShortage && (
        <div className="p-4 rounded-xl bg-amber-50 border border-amber-300 text-amber-900 flex items-center gap-3 shadow-sm">
          <AlertTriangle className="w-5 h-5 text-amber-600 flex-shrink-0" />
          <p className="text-xs font-medium">
            Attention: You have course(s) with attendance below 75%. Please visit the Attendance tab to review session logs.
          </p>
        </div>
      )}

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
        <Link to="/student/courses" className="bg-white p-5 rounded-xl border border-slate-200/80 shadow-sm hover:border-indigo-300 transition-colors group">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="p-2.5 bg-indigo-50 text-indigo-600 rounded-lg group-hover:bg-indigo-100 transition-colors">
                <BookOpen className="w-5 h-5" />
              </div>
              <div>
                <span className="text-xs font-semibold uppercase text-slate-400">Enrolled Courses</span>
                <p className="text-2xl font-bold text-slate-900 mt-0.5">{loading ? '...' : enrollments.length}</p>
              </div>
            </div>
            <ArrowRight className="w-4 h-4 text-gray-300 group-hover:text-indigo-600 transition-colors" />
          </div>
        </Link>

        <Link to="/student/attendance" className="bg-white p-5 rounded-xl border border-slate-200/80 shadow-sm hover:border-emerald-300 transition-colors group">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="p-2.5 bg-emerald-50 text-emerald-600 rounded-lg group-hover:bg-emerald-100 transition-colors">
                <CalendarCheck className="w-5 h-5" />
              </div>
              <div>
                <span className="text-xs font-semibold uppercase text-slate-400">Class Attendance</span>
                <p className={`text-2xl font-bold mt-0.5 ${overallAttendancePct >= 75 ? 'text-emerald-600' : 'text-amber-600'}`}>
                  {loading ? '...' : `${overallAttendancePct}%`}
                </p>
              </div>
            </div>
            <ArrowRight className="w-4 h-4 text-gray-300 group-hover:text-emerald-600 transition-colors" />
          </div>
        </Link>

        <Link to="/student/grades" className="bg-white p-5 rounded-xl border border-slate-200/80 shadow-sm hover:border-purple-300 transition-colors group">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="p-2.5 bg-purple-50 text-purple-600 rounded-lg group-hover:bg-purple-100 transition-colors">
                <Award className="w-5 h-5" />
              </div>
              <div>
                <span className="text-xs font-semibold uppercase text-slate-400">Cumulative GPA</span>
                <p className="text-2xl font-bold text-slate-900 mt-0.5">{loading ? '...' : cgpa}</p>
              </div>
            </div>
            <ArrowRight className="w-4 h-4 text-gray-300 group-hover:text-purple-600 transition-colors" />
          </div>
        </Link>
      </div>

      {/* Announcements Feed */}
      <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
        <div className="p-6 border-b border-gray-100 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Bell className="w-5 h-5 text-indigo-600" />
            <h2 className="text-lg font-bold text-gray-900">Campus & Student Notices</h2>
          </div>
          <span className="text-xs text-gray-400">{announcements.length} bulletin(s)</span>
        </div>

        {announcements.length === 0 ? (
          <div className="p-8 text-center text-gray-400">
            <p className="text-sm">No campus notices posted at this time.</p>
          </div>
        ) : (
          <div className="divide-y divide-gray-100">
            {announcements.map((item) => (
              <div key={item.id} className="p-5 hover:bg-gray-50/50 transition-colors">
                <div className="flex items-center justify-between gap-4">
                  <h4 className="font-bold text-sm text-gray-900">{item.title}</h4>
                  <span className="text-xs text-gray-400 flex items-center gap-1 whitespace-nowrap">
                    <Calendar className="w-3.5 h-3.5" />
                    {new Date(item.createdAt).toLocaleDateString()}
                  </span>
                </div>
                <p className="text-xs text-gray-600 mt-1.5 whitespace-pre-wrap leading-relaxed">
                  {item.content}
                </p>
                <div className="text-[11px] text-gray-400 mt-2">
                  Broadcast by <strong>{item.authorName}</strong>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
