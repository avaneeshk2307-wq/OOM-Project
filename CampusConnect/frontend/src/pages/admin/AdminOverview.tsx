import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { ShieldAlert, Users, BookOpen, Building2 } from 'lucide-react';

export const AdminOverview: React.FC = () => {
  const { user } = useAuth();

  return (
    <div className="space-y-6">
      <div className="bg-white p-6 rounded-xl border border-slate-200/80 shadow-sm flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Administrator Console</h1>
          <p className="text-sm text-slate-500 mt-1">Welcome back, {user?.fullName}. Campus governance and academic management.</p>
        </div>
        <div className="p-3 bg-purple-50 text-purple-700 rounded-xl border border-purple-100 flex items-center gap-2">
          <ShieldAlert className="w-5 h-5" />
          <span className="text-xs font-semibold">{user?.roleSpecificCode || 'SUPER_ADMIN'}</span>
        </div>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
        <div className="bg-white p-5 rounded-xl border border-slate-200/80 shadow-sm">
          <div className="flex items-center gap-3">
            <div className="p-2.5 bg-blue-50 text-blue-600 rounded-lg">
              <Users className="w-5 h-5" />
            </div>
            <div>
              <span className="text-xs font-semibold uppercase text-slate-400">Student Body</span>
              <p className="text-xl font-bold text-slate-900 mt-0.5">Active</p>
            </div>
          </div>
        </div>

        <div className="bg-white p-5 rounded-xl border border-slate-200/80 shadow-sm">
          <div className="flex items-center gap-3">
            <div className="p-2.5 bg-indigo-50 text-indigo-600 rounded-lg">
              <BookOpen className="w-5 h-5" />
            </div>
            <div>
              <span className="text-xs font-semibold uppercase text-slate-400">Curriculum</span>
              <p className="text-xl font-bold text-slate-900 mt-0.5">Courses Active</p>
            </div>
          </div>
        </div>

        <div className="bg-white p-5 rounded-xl border border-slate-200/80 shadow-sm">
          <div className="flex items-center gap-3">
            <div className="p-2.5 bg-emerald-50 text-emerald-600 rounded-lg">
              <Building2 className="w-5 h-5" />
            </div>
            <div>
              <span className="text-xs font-semibold uppercase text-slate-400">Departments</span>
              <p className="text-xl font-bold text-slate-900 mt-0.5">Configured</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
