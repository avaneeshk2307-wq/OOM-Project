import React, { useState, useEffect } from 'react';
import { 
  CheckCircle, 
  XCircle, 
  UserCheck, 
  AlertCircle, 
  Filter, 
  RotateCcw,
  BookOpen,
  Calendar
} from 'lucide-react';
import { apiClient } from '../../api/client';
import { Enrollment, EnrollmentStatus } from '../../types/enrollment';

export const AdminEnrollmentManagement: React.FC = () => {
  const [enrollments, setEnrollments] = useState<Enrollment[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState<string>('ALL');

  const fetchEnrollments = async () => {
    try {
      setLoading(true);
      setError(null);
      const url = statusFilter === 'ALL' 
        ? '/enrollments' 
        : `/enrollments?status=${statusFilter}`;
      const res = await apiClient.get<Enrollment[]>(url);
      setEnrollments(res.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to fetch enrollment records');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchEnrollments();
  }, [statusFilter]);

  const handleAction = async (id: number, action: 'approve' | 'finalize' | 'drop' | 'reject') => {
    try {
      setError(null);
      setSuccessMsg(null);
      await apiClient.put(`/enrollments/${id}/${action}`);
      setSuccessMsg(`Enrollment successfully transitioned: ${action.toUpperCase()}`);
      fetchEnrollments();
      setTimeout(() => setSuccessMsg(null), 4000);
    } catch (err: any) {
      setError(err.response?.data?.message || `Failed to perform ${action} on enrollment`);
    }
  };

  const getStatusBadge = (status: EnrollmentStatus) => {
    switch (status) {
      case 'ENROLLED':
        return <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-100 text-emerald-800">ENROLLED</span>;
      case 'APPROVED':
        return <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-blue-100 text-blue-800">APPROVED</span>;
      case 'SUBMITTED':
        return <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-amber-100 text-amber-800">SUBMITTED (Pending)</span>;
      case 'DRAFT':
        return <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-gray-100 text-gray-800">DRAFT</span>;
      case 'DROPPED':
        return <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-purple-100 text-purple-800">DROPPED</span>;
      case 'REJECTED':
        return <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-rose-100 text-rose-800">REJECTED</span>;
    }
  };

  const counts = {
    all: enrollments.length,
    submitted: enrollments.filter(e => e.status === 'SUBMITTED').length,
    approved: enrollments.filter(e => e.status === 'APPROVED').length,
    enrolled: enrollments.filter(e => e.status === 'ENROLLED').length
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Course Enrollment Workflow</h1>
          <p className="text-sm text-gray-500 mt-1">
            Review, approve, and manage official student course registration applications.
          </p>
        </div>
        <button
          onClick={fetchEnrollments}
          className="inline-flex items-center gap-2 px-3.5 py-2 bg-white border border-gray-300 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-50 transition-colors shadow-sm"
        >
          <RotateCcw className="w-4 h-4" />
          Refresh
        </button>
      </div>

      {/* Alerts */}
      {error && (
        <div className="p-4 rounded-lg bg-red-50 border border-red-200 text-red-700 flex items-center gap-3">
          <AlertCircle className="w-5 h-5 flex-shrink-0" />
          <p className="text-sm font-medium">{error}</p>
        </div>
      )}
      {successMsg && (
        <div className="p-4 rounded-lg bg-emerald-50 border border-emerald-200 text-emerald-700 flex items-center gap-3">
          <CheckCircle className="w-5 h-5 flex-shrink-0" />
          <p className="text-sm font-medium">{successMsg}</p>
        </div>
      )}

      {/* State Metrics */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div className="bg-white p-4 rounded-xl border border-gray-200 shadow-sm">
          <p className="text-xs font-medium text-gray-500 uppercase tracking-wider">Total Applications</p>
          <p className="text-2xl font-bold text-gray-900 mt-1">{counts.all}</p>
        </div>
        <div className="bg-white p-4 rounded-xl border border-amber-200 shadow-sm">
          <p className="text-xs font-medium text-amber-700 uppercase tracking-wider">Pending Approval</p>
          <p className="text-2xl font-bold text-amber-900 mt-1">{counts.submitted}</p>
        </div>
        <div className="bg-white p-4 rounded-xl border border-blue-200 shadow-sm">
          <p className="text-xs font-medium text-blue-700 uppercase tracking-wider">Awaiting Finalization</p>
          <p className="text-2xl font-bold text-blue-900 mt-1">{counts.approved}</p>
        </div>
        <div className="bg-white p-4 rounded-xl border border-emerald-200 shadow-sm">
          <p className="text-xs font-medium text-emerald-700 uppercase tracking-wider">Confirmed Enrolled</p>
          <p className="text-2xl font-bold text-emerald-900 mt-1">{counts.enrolled}</p>
        </div>
      </div>

      {/* Filters */}
      <div className="bg-white p-4 rounded-xl border border-gray-200 shadow-sm flex flex-wrap items-center justify-between gap-4">
        <div className="flex items-center gap-2">
          <Filter className="w-4 h-4 text-gray-400" />
          <span className="text-sm font-medium text-gray-700">Filter By State:</span>
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="text-sm border border-gray-300 rounded-lg px-3 py-1.5 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
          >
            <option value="ALL">All States</option>
            <option value="SUBMITTED">SUBMITTED</option>
            <option value="APPROVED">APPROVED</option>
            <option value="ENROLLED">ENROLLED</option>
            <option value="DROPPED">DROPPED</option>
            <option value="REJECTED">REJECTED</option>
          </select>
        </div>
        <span className="text-xs text-gray-400">
          Showing {enrollments.length} record(s)
        </span>
      </div>

      {/* Enrollment Table */}
      <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
        {loading ? (
          <div className="p-12 text-center text-gray-500">Loading enrollment records...</div>
        ) : enrollments.length === 0 ? (
          <div className="p-12 text-center text-gray-400">
            <BookOpen className="w-12 h-12 mx-auto mb-3 opacity-30" />
            <p className="text-base font-medium">No enrollment records found matching the filter.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-gray-600">
              <thead className="bg-gray-50 text-xs uppercase text-gray-700 border-b border-gray-200">
                <tr>
                  <th className="px-6 py-3 font-semibold">Student</th>
                  <th className="px-6 py-3 font-semibold">Course</th>
                  <th className="px-6 py-3 font-semibold">Semester / Credits</th>
                  <th className="px-6 py-3 font-semibold">State</th>
                  <th className="px-6 py-3 font-semibold">Date</th>
                  <th className="px-6 py-3 font-semibold text-right">Workflow Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {enrollments.map((item) => (
                  <tr key={item.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4">
                      <div className="font-medium text-gray-900">{item.studentName}</div>
                      <div className="text-xs text-gray-500 font-mono">{item.rollNumber}</div>
                    </td>
                    <td className="px-6 py-4">
                      <div className="font-medium text-gray-900">{item.courseTitle}</div>
                      <div className="text-xs text-indigo-600 font-mono font-medium">{item.courseCode}</div>
                    </td>
                    <td className="px-6 py-4">
                      <div>Sem {item.semester}</div>
                      <div className="text-xs text-gray-400">{item.credits} Credits</div>
                    </td>
                    <td className="px-6 py-4">
                      {getStatusBadge(item.status)}
                    </td>
                    <td className="px-6 py-4 text-xs text-gray-500">
                      <div className="flex items-center gap-1.5">
                        <Calendar className="w-3.5 h-3.5 text-gray-400" />
                        {new Date(item.registeredAt).toLocaleDateString()}
                      </div>
                    </td>
                    <td className="px-6 py-4 text-right">
                      <div className="flex items-center justify-end gap-2">
                        {item.status === 'SUBMITTED' && (
                          <>
                            <button
                              onClick={() => handleAction(item.id, 'approve')}
                              className="inline-flex items-center gap-1 px-2.5 py-1 text-xs font-semibold rounded bg-blue-50 text-blue-700 hover:bg-blue-100 transition-colors"
                              title="Approve registration"
                            >
                              <UserCheck className="w-3.5 h-3.5" />
                              Approve
                            </button>
                            <button
                              onClick={() => handleAction(item.id, 'reject')}
                              className="inline-flex items-center gap-1 px-2.5 py-1 text-xs font-semibold rounded bg-rose-50 text-rose-700 hover:bg-rose-100 transition-colors"
                              title="Reject registration"
                            >
                              <XCircle className="w-3.5 h-3.5" />
                              Reject
                            </button>
                          </>
                        )}

                        {item.status === 'APPROVED' && (
                          <>
                            <button
                              onClick={() => handleAction(item.id, 'finalize')}
                              className="inline-flex items-center gap-1 px-2.5 py-1 text-xs font-semibold rounded bg-emerald-50 text-emerald-700 hover:bg-emerald-100 transition-colors"
                              title="Confirm enrollment"
                            >
                              <CheckCircle className="w-3.5 h-3.5" />
                              Enroll
                            </button>
                            <button
                              onClick={() => handleAction(item.id, 'reject')}
                              className="inline-flex items-center gap-1 px-2.5 py-1 text-xs font-semibold rounded bg-rose-50 text-rose-700 hover:bg-rose-100 transition-colors"
                              title="Reject registration"
                            >
                              <XCircle className="w-3.5 h-3.5" />
                              Reject
                            </button>
                          </>
                        )}

                        {item.status === 'ENROLLED' && (
                          <button
                            onClick={() => handleAction(item.id, 'drop')}
                            className="inline-flex items-center gap-1 px-2.5 py-1 text-xs font-semibold rounded bg-purple-50 text-purple-700 hover:bg-purple-100 transition-colors"
                            title="Drop student from course"
                          >
                            Drop
                          </button>
                        )}

                        {(item.status === 'DROPPED' || item.status === 'REJECTED') && (
                          <span className="text-xs text-gray-400 italic">Terminal State</span>
                        )}
                      </div>
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
