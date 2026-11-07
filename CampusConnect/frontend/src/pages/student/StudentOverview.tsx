import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { GraduationCap, BookOpen, CalendarCheck, Award } from 'lucide-react';

export const StudentOverview: React.FC = () => {
  const { user } = useAuth();

  return (
    <div className="space-y-6">
      <div className="bg-white p-6 rounded-xl border border-slate-200/80 shadow-sm flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Student Academic Portal</h1>
          <p className="text-sm text-slate-500 mt-1">Welcome back, {user?.fullName}. Your semester progress and academic standing.</p>
        </div>
        <div className="p-3 bg-emerald-50 text-emerald-700 rounded-xl border border-emerald-100 flex items-center gap-2">
          <GraduationCap className="w-5 h-5" />
          <span className="text-xs font-semibold">{user?.roleSpecificCode || 'STUDENT'}</span>
        </div>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
        <div className="bg-white p-5 rounded-xl border border-slate-200/80 shadow-sm">
          <div className="flex items-center gap-3">
            <div className="p-2.5 bg-indigo-50 text-indigo-600 rounded-lg">
              <BookOpen className="w-5 h-5" />
            </div>
            <div>
              <span className="text-xs font-semibold uppercase text-slate-400">Registered Courses</span>
              <p className="text-xl font-bold text-slate-900 mt-0.5">Enrolled</p>
            </div>
          </div>
        </div>

        <div className="bg-white p-5 rounded-xl border border-slate-200/80 shadow-sm">
          <div className="flex items-center gap-3">
            <div className="p-2.5 bg-emerald-50 text-emerald-600 rounded-lg">
              <CalendarCheck className="w-5 h-5" />
            </div>
            <div>
              <span className="text-xs font-semibold uppercase text-slate-400">Attendance</span>
              <p className="text-xl font-bold text-slate-900 mt-0.5">Records</p>
            </div>
          </div>
        </div>

        <div className="bg-white p-5 rounded-xl border border-slate-200/80 shadow-sm">
          <div className="flex items-center gap-3">
            <div className="p-2.5 bg-purple-50 text-purple-600 rounded-lg">
              <Award className="w-5 h-5" />
            </div>
            <div>
              <span className="text-xs font-semibold uppercase text-slate-400">Grades & GPA</span>
              <p className="text-xl font-bold text-slate-900 mt-0.5">Live Results</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
