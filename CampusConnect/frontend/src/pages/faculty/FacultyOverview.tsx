import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { apiClient } from '../../api/client';
import { Course } from '../../types/admin';
import { Announcement } from '../../types/announcement';
import { Briefcase, BookOpen, CalendarCheck, Award, Bell, ArrowRight, Calendar } from 'lucide-react';
import { Link } from 'react-router-dom';

export const FacultyOverview: React.FC = () => {
  const { user } = useAuth();
  const [courses, setCourses] = useState<Course[]>([]);
  const [announcements, setAnnouncements] = useState<Announcement[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const loadDashboard = async () => {
      try {
        setLoading(true);
        const [coursesRes, annRes] = await Promise.all([
          apiClient.get<Course[]>('/admin/courses'),
          apiClient.get<Announcement[]>('/announcements')
        ]);
        setCourses(coursesRes.data);
        setAnnouncements(annRes.data);
      } catch (err) {
        console.error('Failed to load faculty dashboard data', err);
      } finally {
        setLoading(false);
      }
    };
    loadDashboard();
  }, []);

  return (
    <div className="space-y-6">
      {/* Welcome Banner */}
      <div className="bg-white p-6 rounded-xl border border-slate-200/80 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Faculty Academic Portal</h1>
          <p className="text-sm text-slate-500 mt-1">Welcome back, {user?.fullName}. Teaching assignments and student evaluations.</p>
        </div>
        <div className="p-3 bg-blue-50 text-blue-700 rounded-xl border border-blue-100 flex items-center gap-2 self-start md:self-auto">
          <Briefcase className="w-5 h-5" />
          <span className="text-xs font-semibold">{user?.roleSpecificCode || 'FACULTY'}</span>
        </div>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
        <Link to="/faculty/attendance" className="bg-white p-5 rounded-xl border border-slate-200/80 shadow-sm hover:border-indigo-300 transition-colors group">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="p-2.5 bg-indigo-50 text-indigo-600 rounded-lg group-hover:bg-indigo-100 transition-colors">
                <BookOpen className="w-5 h-5" />
              </div>
              <div>
                <span className="text-xs font-semibold uppercase text-slate-400">Assigned Courses</span>
                <p className="text-2xl font-bold text-slate-900 mt-0.5">{loading ? '...' : courses.length}</p>
              </div>
            </div>
            <ArrowRight className="w-4 h-4 text-gray-300 group-hover:text-indigo-600 transition-colors" />
          </div>
        </Link>

        <Link to="/faculty/attendance" className="bg-white p-5 rounded-xl border border-slate-200/80 shadow-sm hover:border-emerald-300 transition-colors group">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="p-2.5 bg-emerald-50 text-emerald-600 rounded-lg group-hover:bg-emerald-100 transition-colors">
                <CalendarCheck className="w-5 h-5" />
              </div>
              <div>
                <span className="text-xs font-semibold uppercase text-slate-400">Attendance</span>
                <p className="text-sm font-bold text-slate-900 mt-1">Take Roll Call</p>
              </div>
            </div>
            <ArrowRight className="w-4 h-4 text-gray-300 group-hover:text-emerald-600 transition-colors" />
          </div>
        </Link>

        <Link to="/faculty/grades" className="bg-white p-5 rounded-xl border border-slate-200/80 shadow-sm hover:border-amber-300 transition-colors group">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="p-2.5 bg-amber-50 text-amber-600 rounded-lg group-hover:bg-amber-100 transition-colors">
                <Award className="w-5 h-5" />
              </div>
              <div>
                <span className="text-xs font-semibold uppercase text-slate-400">Assessments</span>
                <p className="text-sm font-bold text-slate-900 mt-1">Grade Submissions</p>
              </div>
            </div>
            <ArrowRight className="w-4 h-4 text-gray-300 group-hover:text-amber-600 transition-colors" />
          </div>
        </Link>
      </div>

      {/* Announcements Feed */}
      <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
        <div className="p-6 border-b border-gray-100 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Bell className="w-5 h-5 text-indigo-600" />
            <h2 className="text-lg font-bold text-gray-900">Faculty Notices & Bulletins</h2>
          </div>
          <span className="text-xs text-gray-400">{announcements.length} update(s)</span>
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
                  Posted by <strong>{item.authorName}</strong>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
