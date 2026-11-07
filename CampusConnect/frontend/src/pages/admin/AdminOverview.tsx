import React, { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { apiClient } from '../../api/client';
import { AdminStats } from '../../types/admin';
import { 
  Users, 
  Briefcase, 
  BookOpen, 
  Building2, 
  Award, 
  RefreshCw,
  AlertCircle 
} from 'lucide-react';

export const AdminOverview: React.FC = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState<AdminStats | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchStats = async () => {
    setLoading(true);
    try {
      const res = await apiClient.get<AdminStats>('/admin/stats');
      setStats(res.data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Could not load administrative statistics.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStats();
  }, []);

  return (
    <div className="space-y-6">
      {/* Welcome Banner */}
      <div className="bg-white p-6 rounded-xl border border-slate-200/80 shadow-sm flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-2xl font-bold text-slate-900">Campus Overview</h1>
            <span className="text-xs bg-purple-100 text-purple-700 px-2 py-0.5 rounded-full font-semibold border border-purple-200">
              Governance Mode
            </span>
          </div>
          <p className="text-sm text-slate-500 mt-1">
            Welcome, {user?.fullName}. Real-time academic metrics and institutional management.
          </p>
        </div>

        <button
          onClick={fetchStats}
          disabled={loading}
          className="inline-flex items-center gap-2 px-3.5 py-2 bg-slate-50 hover:bg-slate-100 text-slate-700 rounded-lg text-xs font-semibold border border-slate-200 transition-colors shadow-xs disabled:opacity-50 self-start sm:self-auto"
        >
          <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
          <span>Refresh Metrics</span>
        </button>
      </div>

      {error && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-xl flex items-center gap-3 text-red-700 text-sm">
          <AlertCircle className="w-5 h-5 text-red-600 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
        <div className="bg-white p-5 rounded-xl border border-slate-200/80 shadow-sm space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">Students</span>
            <div className="p-2 bg-blue-50 text-blue-600 rounded-lg">
              <Users className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-extrabold text-slate-900">
            {loading ? '...' : stats?.totalStudents ?? 0}
          </p>
          <p className="text-xs text-slate-400">Enrolled student body</p>
        </div>

        <div className="bg-white p-5 rounded-xl border border-slate-200/80 shadow-sm space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">Faculty</span>
            <div className="p-2 bg-indigo-50 text-indigo-600 rounded-lg">
              <Briefcase className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-extrabold text-slate-900">
            {loading ? '...' : stats?.totalFaculty ?? 0}
          </p>
          <p className="text-xs text-slate-400">Teaching faculty members</p>
        </div>

        <div className="bg-white p-5 rounded-xl border border-slate-200/80 shadow-sm space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">Courses</span>
            <div className="p-2 bg-emerald-50 text-emerald-600 rounded-lg">
              <BookOpen className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-extrabold text-slate-900">
            {loading ? '...' : stats?.totalCourses ?? 0}
          </p>
          <p className="text-xs text-slate-400">Active curriculum subjects</p>
        </div>

        <div className="bg-white p-5 rounded-xl border border-slate-200/80 shadow-sm space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">Departments</span>
            <div className="p-2 bg-purple-50 text-purple-600 rounded-lg">
              <Building2 className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-extrabold text-slate-900">
            {loading ? '...' : stats?.totalDepartments ?? 0}
          </p>
          <p className="text-xs text-slate-400">Academic divisions</p>
        </div>

        <div className="bg-white p-5 rounded-xl border border-slate-200/80 shadow-sm space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">Enrollments</span>
            <div className="p-2 bg-amber-50 text-amber-600 rounded-lg">
              <Award className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-extrabold text-slate-900">
            {loading ? '...' : stats?.totalEnrollments ?? 0}
          </p>
          <p className="text-xs text-slate-400">Course registrations</p>
        </div>
      </div>

      {/* Department Breakdown Section */}
      <div className="bg-white p-6 rounded-xl border border-slate-200/80 shadow-sm space-y-4">
        <h2 className="text-base font-semibold text-slate-900 flex items-center gap-2">
          <Building2 className="w-4 h-4 text-indigo-600" />
          Department Student Distribution
        </h2>

        {loading ? (
          <div className="p-4 text-slate-400 text-sm">Loading department metrics...</div>
        ) : stats?.studentsByDepartment && Object.keys(stats.studentsByDepartment).length > 0 ? (
          <div className="divide-y divide-slate-100">
            {Object.entries(stats.studentsByDepartment).map(([deptName, count]) => (
              <div key={deptName} className="py-3 flex items-center justify-between">
                <span className="font-medium text-sm text-slate-800">{deptName}</span>
                <span className="text-xs font-semibold px-2.5 py-1 bg-indigo-50 text-indigo-700 rounded-full border border-indigo-100">
                  {count} {count === 1 ? 'Student' : 'Students'}
                </span>
              </div>
            ))}
          </div>
        ) : (
          <p className="text-sm text-slate-500 py-2">No department distribution recorded yet.</p>
        )}
      </div>
    </div>
  );
};
