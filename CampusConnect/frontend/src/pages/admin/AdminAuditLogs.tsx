import React, { useState, useEffect } from 'react';
import { auditApi } from '../../api/audit';
import { AuditLogResponse, AuditAction } from '../../types/audit';
import { 
  ShieldCheck, 
  Search, 
  Filter, 
  RefreshCw, 
  ChevronLeft, 
  ChevronRight, 
  FileText, 
  Eye, 
  X,
  Activity,
  UserCheck,
  Award
} from 'lucide-react';

const ACTION_COLORS: Record<string, string> = {
  CREATE_STUDENT: 'bg-emerald-50 text-emerald-700 border-emerald-200',
  UPDATE_STUDENT: 'bg-blue-50 text-blue-700 border-blue-200',
  PROMOTE_STUDENT: 'bg-indigo-50 text-indigo-700 border-indigo-200',
  CREATE_FACULTY: 'bg-teal-50 text-teal-700 border-teal-200',
  CREATE_COURSE: 'bg-cyan-50 text-cyan-700 border-cyan-200',
  ASSIGN_FACULTY: 'bg-sky-50 text-sky-700 border-sky-200',
  SUBMIT_GRADE: 'bg-purple-50 text-purple-700 border-purple-200',
  RECORD_ATTENDANCE: 'bg-amber-50 text-amber-700 border-amber-200',
  ENROLLMENT_STATUS_CHANGE: 'bg-violet-50 text-violet-700 border-violet-200',
  SCHEDULE_TIMETABLE: 'bg-pink-50 text-pink-700 border-pink-200',
  SECURITY_EVENT: 'bg-rose-50 text-rose-700 border-rose-200',
};

const ALL_ACTIONS: AuditAction[] = [
  'CREATE_STUDENT',
  'UPDATE_STUDENT',
  'PROMOTE_STUDENT',
  'CREATE_FACULTY',
  'UPDATE_FACULTY',
  'CREATE_COURSE',
  'UPDATE_COURSE',
  'ASSIGN_FACULTY',
  'CREATE_DEPARTMENT',
  'SCHEDULE_TIMETABLE',
  'DELETE_TIMETABLE',
  'ENROLLMENT_STATUS_CHANGE',
  'SUBMIT_GRADE',
  'RECORD_ATTENDANCE',
  'POST_ANNOUNCEMENT',
  'SECURITY_EVENT'
];

export const AdminAuditLogs: React.FC = () => {
  const [logs, setLogs] = useState<AuditLogResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedAction, setSelectedAction] = useState<string>('');
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [page, setPage] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(1);
  const [totalElements, setTotalElements] = useState<number>(0);
  const [selectedLog, setSelectedLog] = useState<AuditLogResponse | null>(null);

  const fetchLogs = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await auditApi.getAuditLogs(selectedAction || undefined, page, 15);
      setLogs(data.content);
      setTotalPages(data.totalPages || 1);
      setTotalElements(data.totalElements || 0);
    } catch (err: any) {
      console.error('Failed to load audit logs:', err);
      setError('Unable to load institutional audit trail.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLogs();
  }, [page, selectedAction]);

  const filteredLogs = logs.filter(log => {
    if (!searchQuery.trim()) return true;
    const q = searchQuery.toLowerCase();
    return (
      log.performedByUsername.toLowerCase().includes(q) ||
      log.entityName.toLowerCase().includes(q) ||
      (log.details && log.details.toLowerCase().includes(q)) ||
      (log.entityId && log.entityId.toLowerCase().includes(q))
    );
  });

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4 bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
        <div>
          <div className="flex items-center gap-2">
            <span className="p-2 bg-indigo-100 text-indigo-700 rounded-lg">
              <ShieldCheck className="w-6 h-6" />
            </span>
            <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Institutional Audit Logs</h1>
          </div>
          <p className="text-slate-500 text-sm mt-1">
            Tamper-evident system activity ledger recording administrative modifications, grading events, and student operations.
          </p>
        </div>
        <button
          onClick={fetchLogs}
          disabled={loading}
          className="inline-flex items-center gap-2 px-4 py-2 border border-slate-300 rounded-lg text-sm font-medium text-slate-700 hover:bg-slate-50 transition-colors shadow-sm"
        >
          <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
          Refresh Ledger
        </button>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex items-center gap-4">
          <div className="p-3 bg-blue-50 text-blue-600 rounded-xl">
            <Activity className="w-6 h-6" />
          </div>
          <div>
            <div className="text-2xl font-bold text-slate-900">{totalElements}</div>
            <div className="text-xs font-medium text-slate-500 uppercase tracking-wider">Total Recorded Events</div>
          </div>
        </div>
        <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex items-center gap-4">
          <div className="p-3 bg-emerald-50 text-emerald-600 rounded-xl">
            <UserCheck className="w-6 h-6" />
          </div>
          <div>
            <div className="text-2xl font-bold text-slate-900">Active</div>
            <div className="text-xs font-medium text-slate-500 uppercase tracking-wider">Security Logging Status</div>
          </div>
        </div>
        <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex items-center gap-4">
          <div className="p-3 bg-purple-50 text-purple-600 rounded-xl">
            <Award className="w-6 h-6" />
          </div>
          <div>
            <div className="text-2xl font-bold text-slate-900">Immutable</div>
            <div className="text-xs font-medium text-slate-500 uppercase tracking-wider">Storage Guarantee</div>
          </div>
        </div>
      </div>

      {/* Filter and Search Bar */}
      <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm flex flex-col md:flex-row gap-4 items-center justify-between">
        <div className="relative w-full md:w-80">
          <Search className="absolute left-3 top-2.5 h-4 w-4 text-slate-400" />
          <input
            type="text"
            placeholder="Search username, entity, details..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-9 pr-4 py-2 w-full text-sm rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
          />
        </div>

        <div className="flex items-center gap-3 w-full md:w-auto">
          <div className="flex items-center gap-2 text-sm text-slate-600">
            <Filter className="w-4 h-4 text-slate-400" />
            <span>Action:</span>
          </div>
          <select
            value={selectedAction}
            onChange={(e) => {
              setSelectedAction(e.target.value);
              setPage(0);
            }}
            className="text-sm rounded-lg border border-slate-300 py-2 px-3 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="">All Actions</option>
            {ALL_ACTIONS.map(action => (
              <option key={action} value={action}>{action}</option>
            ))}
          </select>
        </div>
      </div>

      {/* Audit Logs Table */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
        {loading ? (
          <div className="p-12 text-center text-slate-500">
            <RefreshCw className="w-8 h-8 animate-spin mx-auto text-indigo-600 mb-3" />
            Loading audit ledger...
          </div>
        ) : error ? (
          <div className="p-8 text-center text-rose-600 font-medium">{error}</div>
        ) : filteredLogs.length === 0 ? (
          <div className="p-12 text-center text-slate-400">
            <FileText className="w-12 h-12 mx-auto mb-3 opacity-40" />
            <p className="text-base font-semibold text-slate-700">No audit records found</p>
            <p className="text-sm text-slate-400 mt-1">Actions performed across the system will appear here.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-600">
              <thead className="bg-slate-50 border-b border-slate-200 text-xs font-semibold text-slate-700 uppercase tracking-wider">
                <tr>
                  <th className="px-6 py-3">Timestamp</th>
                  <th className="px-6 py-3">Action</th>
                  <th className="px-6 py-3">Target Entity</th>
                  <th className="px-6 py-3">Actor (Username)</th>
                  <th className="px-6 py-3">IP Address</th>
                  <th className="px-6 py-3">Details</th>
                  <th className="px-6 py-3 text-right">Inspect</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200">
                {filteredLogs.map(log => {
                  const badgeClass = ACTION_COLORS[log.action] || 'bg-slate-100 text-slate-700 border-slate-200';
                  return (
                    <tr key={log.id} className="hover:bg-slate-50 transition-colors">
                      <td className="px-6 py-4 font-mono text-xs text-slate-500 whitespace-nowrap">
                        {new Date(log.createdAt).toLocaleString()}
                      </td>
                      <td className="px-6 py-4">
                        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold border ${badgeClass}`}>
                          {log.action}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className="font-semibold text-slate-800">{log.entityName}</span>
                        {log.entityId && (
                          <span className="ml-1 text-xs text-slate-400">#{log.entityId}</span>
                        )}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className="inline-flex items-center gap-1.5 font-medium text-slate-900">
                          <span className="w-2 h-2 rounded-full bg-indigo-500"></span>
                          {log.performedByUsername}
                        </span>
                      </td>
                      <td className="px-6 py-4 font-mono text-xs text-slate-500">
                        {log.ipAddress || '127.0.0.1'}
                      </td>
                      <td className="px-6 py-4 max-w-xs truncate text-slate-600 text-xs">
                        {log.details || '—'}
                      </td>
                      <td className="px-6 py-4 text-right">
                        <button
                          onClick={() => setSelectedLog(log)}
                          className="p-1.5 hover:bg-indigo-50 text-slate-400 hover:text-indigo-600 rounded-lg transition-colors"
                          title="View Details"
                        >
                          <Eye className="w-4 h-4" />
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}

        {/* Pagination Bar */}
        <div className="px-6 py-4 bg-slate-50 border-t border-slate-200 flex items-center justify-between text-sm text-slate-600">
          <div>
            Showing Page <span className="font-semibold text-slate-900">{page + 1}</span> of{' '}
            <span className="font-semibold text-slate-900">{totalPages}</span> ({totalElements} total entries)
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={() => setPage(p => Math.max(0, p - 1))}
              disabled={page === 0}
              className="inline-flex items-center gap-1 px-3 py-1.5 border border-slate-300 rounded-lg text-xs font-medium text-slate-700 bg-white hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed"
            >
              <ChevronLeft className="w-3.5 h-3.5" /> Previous
            </button>
            <button
              onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
              disabled={page >= totalPages - 1}
              className="inline-flex items-center gap-1 px-3 py-1.5 border border-slate-300 rounded-lg text-xs font-medium text-slate-700 bg-white hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed"
            >
              Next <ChevronRight className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>
      </div>

      {/* Details Modal */}
      {selectedLog && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-sm animate-fade-in">
          <div className="bg-white rounded-2xl shadow-2xl max-w-lg w-full border border-slate-100 overflow-hidden">
            <div className="p-5 border-b border-slate-100 flex items-center justify-between bg-slate-50">
              <div className="flex items-center gap-2">
                <ShieldCheck className="w-5 h-5 text-indigo-600" />
                <h3 className="font-bold text-slate-900 text-lg">Audit Record #{selectedLog.id}</h3>
              </div>
              <button
                onClick={() => setSelectedLog(null)}
                className="text-slate-400 hover:text-slate-600 p-1 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>
            <div className="p-6 space-y-4 text-sm">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <div className="text-xs font-medium text-slate-400 uppercase">Action</div>
                  <div className="mt-1 font-semibold text-slate-900">{selectedLog.action}</div>
                </div>
                <div>
                  <div className="text-xs font-medium text-slate-400 uppercase">Timestamp</div>
                  <div className="mt-1 text-slate-700 font-mono text-xs">{new Date(selectedLog.createdAt).toLocaleString()}</div>
                </div>
                <div>
                  <div className="text-xs font-medium text-slate-400 uppercase">Actor</div>
                  <div className="mt-1 font-semibold text-slate-900">{selectedLog.performedByUsername}</div>
                </div>
                <div>
                  <div className="text-xs font-medium text-slate-400 uppercase">Client IP</div>
                  <div className="mt-1 font-mono text-xs text-slate-700">{selectedLog.ipAddress || '127.0.0.1'}</div>
                </div>
                <div>
                  <div className="text-xs font-medium text-slate-400 uppercase">Entity Target</div>
                  <div className="mt-1 font-semibold text-slate-900">
                    {selectedLog.entityName} {selectedLog.entityId ? `(#${selectedLog.entityId})` : ''}
                  </div>
                </div>
              </div>
              <div className="pt-2">
                <div className="text-xs font-medium text-slate-400 uppercase mb-1">Payload / Description</div>
                <div className="bg-slate-50 border border-slate-200 rounded-lg p-3 font-mono text-xs text-slate-800 whitespace-pre-wrap break-words">
                  {selectedLog.details || 'No extended payload provided.'}
                </div>
              </div>
            </div>
            <div className="p-4 bg-slate-50 border-t border-slate-100 flex justify-end">
              <button
                onClick={() => setSelectedLog(null)}
                className="px-4 py-2 bg-slate-200 hover:bg-slate-300 text-slate-800 rounded-lg text-sm font-medium transition-colors"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

