import React, { useState, useEffect } from 'react';
import { apiClient } from '../../api/client';
import { StudentCourseAttendanceSummary } from '../../types/attendance';
import { 
  CheckCircle, 
  AlertTriangle, 
  Calendar, 
  BookOpen, 
  Check, 
  X, 
  Clock, 
  ChevronDown, 
  ChevronUp,
  AlertCircle
} from 'lucide-react';

export const StudentAttendance: React.FC = () => {
  const [summaries, setSummaries] = useState<StudentCourseAttendanceSummary[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [expandedCourseId, setExpandedCourseId] = useState<number | null>(null);

  useEffect(() => {
    const fetchMyAttendance = async () => {
      try {
        setLoading(true);
        const res = await apiClient.get<StudentCourseAttendanceSummary[]>('/attendance/my-attendance');
        setSummaries(res.data);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load attendance records.');
      } finally {
        setLoading(false);
      }
    };

    fetchMyAttendance();
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center p-12 text-gray-500">
        Loading personal attendance records...
      </div>
    );
  }

  const warningCourses = summaries.filter((s) => s.lowAttendanceWarning);
  const totalClasses = summaries.reduce((acc, curr) => acc + curr.totalSessions, 0);
  const attendedClasses = summaries.reduce((acc, curr) => acc + curr.attendedSessions, 0);
  const overallPct = totalClasses > 0 ? Math.round((attendedClasses / totalClasses) * 100) : 100;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">My Course Attendance</h1>
        <p className="text-sm text-gray-500 mt-1">
          Review class session presence and monitor eligibility status across your semester courses.
        </p>
      </div>

      {/* Error Message */}
      {error && (
        <div className="p-4 rounded-lg bg-red-50 border border-red-200 text-red-700 flex items-center gap-3">
          <AlertCircle className="w-5 h-5 flex-shrink-0" />
          <p className="text-sm font-medium">{error}</p>
        </div>
      )}

      {/* Low Attendance Warning Alert */}
      {warningCourses.length > 0 && (
        <div className="p-4 rounded-xl bg-amber-50 border border-amber-300 text-amber-900 flex items-start gap-3 shadow-sm">
          <AlertTriangle className="w-5 h-5 text-amber-600 flex-shrink-0 mt-0.5" />
          <div>
            <h4 className="text-sm font-bold">Low Attendance Shortage Warning</h4>
            <p className="text-xs text-amber-800 mt-0.5">
              You are currently below the university mandatory 75% attendance threshold in{' '}
              <strong>{warningCourses.map(c => c.courseCode).join(', ')}</strong>. 
              Prompt attendance in upcoming lectures is required to retain exam eligibility.
            </p>
          </div>
        </div>
      )}

      {/* Aggregate Overview Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="bg-white p-5 rounded-xl border border-gray-200 shadow-sm">
          <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider">Overall Attendance</p>
          <div className="flex items-baseline gap-2 mt-2">
            <span className={`text-3xl font-extrabold ${overallPct >= 75 ? 'text-emerald-600' : 'text-amber-600'}`}>
              {overallPct}%
            </span>
            <span className="text-xs text-gray-400">across all registered courses</span>
          </div>
        </div>

        <div className="bg-white p-5 rounded-xl border border-gray-200 shadow-sm">
          <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider">Total Class Meetings</p>
          <div className="flex items-baseline gap-2 mt-2">
            <span className="text-3xl font-extrabold text-gray-900">{attendedClasses}</span>
            <span className="text-xs text-gray-500">attended out of {totalClasses} conducted</span>
          </div>
        </div>

        <div className="bg-white p-5 rounded-xl border border-gray-200 shadow-sm">
          <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider">Attendance Status</p>
          <div className="flex items-center gap-2 mt-2">
            {warningCourses.length === 0 ? (
              <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-emerald-100 text-emerald-800">
                <CheckCircle className="w-4 h-4" /> Good Academic Standing
              </span>
            ) : (
              <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-rose-100 text-rose-800">
                <AlertTriangle className="w-4 h-4" /> {warningCourses.length} Course(s) Below 75%
              </span>
            )}
          </div>
        </div>
      </div>

      {/* Course-by-Course Attendance Cards */}
      <div className="space-y-4">
        {summaries.length === 0 ? (
          <div className="bg-white p-12 rounded-xl border border-gray-200 text-center text-gray-400">
            <BookOpen className="w-12 h-12 mx-auto mb-3 opacity-30" />
            <p className="text-base font-semibold">No enrolled courses found for attendance tracking.</p>
            <p className="text-xs text-gray-400 mt-1">Visit Course Registration to enroll in courses for the current semester.</p>
          </div>
        ) : (
          summaries.map((course) => {
            const isExpanded = expandedCourseId === course.courseId;

            return (
              <div key={course.courseId} className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
                {/* Course Attendance Header */}
                <div 
                  onClick={() => setExpandedCourseId(isExpanded ? null : course.courseId)}
                  className="p-5 cursor-pointer hover:bg-gray-50 transition-colors flex flex-col md:flex-row md:items-center justify-between gap-4"
                >
                  <div className="flex items-start gap-3">
                    <div className="w-10 h-10 rounded-lg bg-indigo-50 border border-indigo-100 flex items-center justify-center flex-shrink-0 text-indigo-600 font-bold text-sm">
                      {course.credits} Cr
                    </div>
                    <div>
                      <div className="flex items-center gap-2">
                        <span className="font-bold text-gray-900">{course.courseTitle}</span>
                        <span className="text-xs font-mono font-medium text-indigo-600 bg-indigo-50 px-2 py-0.5 rounded">
                          {course.courseCode}
                        </span>
                      </div>
                      <p className="text-xs text-gray-500 mt-1">
                        Attended {course.attendedSessions} of {course.totalSessions} sessions held
                      </p>
                    </div>
                  </div>

                  <div className="flex items-center gap-4">
                    {/* Progress Bar & Percentage */}
                    <div className="flex flex-col items-end gap-1">
                      <div className="flex items-center gap-2">
                        <span className={`text-sm font-extrabold ${course.percentage >= 75 ? 'text-emerald-700' : 'text-rose-700'}`}>
                          {course.percentage}%
                        </span>
                        {course.lowAttendanceWarning ? (
                          <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-rose-100 text-rose-800">
                            Shortage Warning
                          </span>
                        ) : (
                          <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-emerald-100 text-emerald-800">
                            Eligible
                          </span>
                        )}
                      </div>
                      <div className="w-36 bg-gray-200 rounded-full h-2 overflow-hidden">
                        <div 
                          className={`h-2 rounded-full ${course.percentage >= 75 ? 'bg-emerald-500' : 'bg-rose-500'}`}
                          style={{ width: `${Math.min(course.percentage, 100)}%` }}
                        />
                      </div>
                    </div>

                    {isExpanded ? (
                      <ChevronUp className="w-5 h-5 text-gray-400" />
                    ) : (
                      <ChevronDown className="w-5 h-5 text-gray-400" />
                    )}
                  </div>
                </div>

                {/* Collapsible Session Log */}
                {isExpanded && (
                  <div className="border-t border-gray-100 bg-gray-50/50 p-5 space-y-3">
                    <h5 className="text-xs font-bold uppercase tracking-wider text-gray-500 flex items-center gap-1.5">
                      <Calendar className="w-3.5 h-3.5 text-indigo-600" />
                      Session Attendance Log
                    </h5>

                    {course.history.length === 0 ? (
                      <p className="text-xs text-gray-400 italic">No attendance records recorded yet for this course.</p>
                    ) : (
                      <div className="overflow-x-auto bg-white rounded-lg border border-gray-200">
                        <table className="w-full text-left text-xs text-gray-600">
                          <thead className="bg-gray-50 text-gray-700 font-semibold border-b border-gray-200">
                            <tr>
                              <th className="px-4 py-2.5">Date</th>
                              <th className="px-4 py-2.5">Class Topic</th>
                              <th className="px-4 py-2.5">Status</th>
                              <th className="px-4 py-2.5">Faculty Remarks</th>
                            </tr>
                          </thead>
                          <tbody className="divide-y divide-gray-100">
                            {course.history.map((item, idx) => (
                              <tr key={idx} className="hover:bg-gray-50">
                                <td className="px-4 py-2 font-medium text-gray-900 whitespace-nowrap">
                                  {new Date(item.sessionDate).toLocaleDateString(undefined, {
                                    year: 'numeric',
                                    month: 'short',
                                    day: 'numeric'
                                  })}
                                </td>
                                <td className="px-4 py-2">{item.topic || 'Regular Lecture'}</td>
                                <td className="px-4 py-2">
                                  <span className={`inline-flex items-center gap-1 font-semibold ${
                                    item.status === 'PRESENT'
                                      ? 'text-emerald-700'
                                      : item.status === 'LATE'
                                      ? 'text-amber-700'
                                      : 'text-rose-700'
                                  }`}>
                                    {item.status === 'PRESENT' && <Check className="w-3 h-3" />}
                                    {item.status === 'ABSENT' && <X className="w-3 h-3" />}
                                    {item.status === 'LATE' && <Clock className="w-3 h-3" />}
                                    {item.status}
                                  </span>
                                </td>
                                <td className="px-4 py-2 text-gray-400 italic">
                                  {item.remarks || '—'}
                                </td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    )}
                  </div>
                )}
              </div>
            );
          })
        )}
      </div>
    </div>
  );
};
