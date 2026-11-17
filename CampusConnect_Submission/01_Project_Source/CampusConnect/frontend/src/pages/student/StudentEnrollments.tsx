import React, { useEffect, useState } from 'react';
import { apiClient } from '../../api/client';
import { Enrollment, EnrollmentStatus } from '../../types/enrollment';
import { 
  Award, 
  Trash2, 
  AlertCircle, 
  CheckCircle2, 
  Clock, 
  Check, 
  XCircle
} from 'lucide-react';

export const StudentEnrollments: React.FC = () => {
  const [enrollments, setEnrollments] = useState<Enrollment[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const loadEnrollments = async () => {
    setLoading(true);
    try {
      const res = await apiClient.get<Enrollment[]>('/enrollments/my-enrollments');
      setEnrollments(res.data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Could not load your registered courses.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadEnrollments();
  }, []);

  const handleDrop = async (id: number, courseCode: string) => {
    if (!window.confirm(`Are you sure you want to drop course ${courseCode}?`)) return;

    setError(null);
    try {
      await apiClient.put(`/enrollments/${id}/drop`);
      setSuccess(`Course ${courseCode} dropped.`);
      loadEnrollments();
      setTimeout(() => setSuccess(null), 3500);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Cannot drop course.');
    }
  };

  const getStatusBadge = (status: EnrollmentStatus) => {
    switch (status) {
      case 'SUBMITTED':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-amber-50 text-amber-700 border border-amber-200">
            <Clock className="w-3 h-3" />
            Submitted
          </span>
        );
      case 'APPROVED':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-blue-50 text-blue-700 border border-blue-200">
            <Check className="w-3 h-3" />
            Approved
          </span>
        );
      case 'ENROLLED':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
            <CheckCircle2 className="w-3 h-3" />
            Enrolled
          </span>
        );
      case 'DROPPED':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-slate-100 text-slate-600 border border-slate-200">
            Dropped
          </span>
        );
      case 'REJECTED':
        return (
          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-red-50 text-red-700 border border-red-200">
            <XCircle className="w-3 h-3" />
            Rejected
          </span>
        );
      default:
        return (
          <span className="px-2 py-0.5 rounded text-xs bg-slate-100 text-slate-700">
            {status}
          </span>
        );
    }
  };

  const totalCredits = enrollments
    .filter((e) => e.status === 'ENROLLED' || e.status === 'APPROVED')
    .reduce((sum, e) => sum + e.credits, 0);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white p-6 rounded-xl border border-slate-200/80 shadow-sm flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">My Course Registrations</h1>
          <p className="text-sm text-slate-500 mt-1">Track registration lifecycle states, review enrolled credits, and manage courses.</p>
        </div>

        <div className="px-4 py-2 bg-indigo-50 border border-indigo-100 rounded-xl flex items-center gap-3">
          <Award className="w-5 h-5 text-indigo-600" />
          <div>
            <span className="text-xs uppercase font-semibold text-indigo-500">Active Credits</span>
            <p className="text-lg font-bold text-indigo-900 leading-none mt-0.5">{totalCredits} Credits</p>
          </div>
        </div>
      </div>

      {/* Notifications */}
      {error && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-xl flex items-start gap-3 text-red-700 text-sm">
          <AlertCircle className="w-5 h-5 text-red-600 shrink-0 mt-0.5" />
          <span>{error}</span>
        </div>
      )}
      {success && (
        <div className="p-4 bg-emerald-50 border border-emerald-200 rounded-xl flex items-center gap-3 text-emerald-700 text-sm">
          <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0" />
          <span>{success}</span>
        </div>
      )}

      {/* Enrollments Table */}
      <div className="bg-white rounded-xl border border-slate-200/80 shadow-sm overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-slate-500 text-sm">Loading your registrations...</div>
        ) : enrollments.length === 0 ? (
          <div className="p-8 text-center text-slate-500 text-sm">
            You are not registered in any courses yet. Visit the Course Catalog to register.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-700">
              <thead className="bg-slate-50 border-b border-slate-200 text-xs uppercase font-semibold text-slate-500 tracking-wider">
                <tr>
                  <th className="px-6 py-3.5">Course Code</th>
                  <th className="px-6 py-3.5">Title</th>
                  <th className="px-6 py-3.5 text-center">Credits</th>
                  <th className="px-6 py-3.5 text-center">Semester</th>
                  <th className="px-6 py-3.5 text-center">State Pattern Status</th>
                  <th className="px-6 py-3.5">Registration Date</th>
                  <th className="px-6 py-3.5 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {enrollments.map((e) => (
                  <tr key={e.id} className="hover:bg-slate-50/70 transition-colors">
                    <td className="px-6 py-4 font-mono font-bold text-indigo-700">
                      {e.courseCode}
                    </td>
                    <td className="px-6 py-4 font-semibold text-slate-900">
                      {e.courseTitle}
                    </td>
                    <td className="px-6 py-4 text-center font-medium">
                      {e.credits}
                    </td>
                    <td className="px-6 py-4 text-center">
                      <span className="text-xs px-2 py-0.5 rounded-full bg-slate-100 font-medium text-slate-700">
                        Sem {e.semester}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-center">
                      {getStatusBadge(e.status)}
                    </td>
                    <td className="px-6 py-4 text-xs text-slate-500">
                      {new Date(e.registeredAt).toLocaleDateString()}
                    </td>
                    <td className="px-6 py-4 text-right">
                      {e.status !== 'DROPPED' && e.status !== 'REJECTED' && (
                        <button
                          onClick={() => handleDrop(e.id, e.courseCode)}
                          className="inline-flex items-center gap-1 px-2.5 py-1 text-xs font-semibold text-red-600 bg-red-50 hover:bg-red-100 rounded-lg transition-colors border border-red-200"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                          <span>Drop</span>
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};
