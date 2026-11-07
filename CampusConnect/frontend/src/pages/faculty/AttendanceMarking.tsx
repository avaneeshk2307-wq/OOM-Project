import React, { useState, useEffect } from 'react';
import { apiClient } from '../../api/client';
import { Course } from '../../types/admin';
import { Enrollment } from '../../types/enrollment';
import { AttendanceSessionResponse, AttendanceStatus } from '../../types/attendance';
import { 
  Calendar, 
  CheckCircle, 
  AlertCircle, 
  Clock, 
  Users, 
  ChevronDown, 
  ChevronUp,
  Save,
  Check,
  X,
  BookOpen
} from 'lucide-react';

export const AttendanceMarking: React.FC = () => {
  const [courses, setCourses] = useState<Course[]>([]);
  const [selectedCourseId, setSelectedCourseId] = useState<number | null>(null);
  const [roster, setRoster] = useState<Enrollment[]>([]);
  const [pastSessions, setPastSessions] = useState<AttendanceSessionResponse[]>([]);
  
  const [sessionDate, setSessionDate] = useState<string>(new Date().toISOString().split('T')[0]);
  const [topic, setTopic] = useState<string>('');
  const [attendanceMap, setAttendanceMap] = useState<Record<number, { status: AttendanceStatus; remarks: string }>>({});
  
  const [loading, setLoading] = useState<boolean>(true);
  const [submitting, setSubmitting] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [expandedSessionId, setExpandedSessionId] = useState<number | null>(null);

  // Load courses
  useEffect(() => {
    const fetchCourses = async () => {
      try {
        setLoading(true);
        const res = await apiClient.get<Course[]>('/admin/courses');
        setCourses(res.data);
        if (res.data.length > 0) {
          setSelectedCourseId(res.data[0].id);
        }
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load courses.');
      } finally {
        setLoading(false);
      }
    };
    fetchCourses();
  }, []);

  // When selected course changes, fetch roster and past sessions
  useEffect(() => {
    if (!selectedCourseId) return;

    const fetchCourseData = async () => {
      try {
        const [rosterRes, sessionsRes] = await Promise.all([
          apiClient.get<Enrollment[]>(`/enrollments/course/${selectedCourseId}`),
          apiClient.get<AttendanceSessionResponse[]>(`/attendance/sessions/course/${selectedCourseId}`)
        ]);

        // Filter only approved/enrolled students
        const activeStudents = rosterRes.data.filter(
          (e) => e.status === 'ENROLLED' || e.status === 'APPROVED'
        );
        setRoster(activeStudents);
        setPastSessions(sessionsRes.data);

        // Pre-populate attendanceMap with default PRESENT
        const initialMap: Record<number, { status: AttendanceStatus; remarks: string }> = {};
        activeStudents.forEach((student) => {
          initialMap[student.studentId] = { status: 'PRESENT', remarks: '' };
        });
        setAttendanceMap(initialMap);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load course details.');
      }
    };

    fetchCourseData();
  }, [selectedCourseId]);

  const handleStatusChange = (studentId: number, status: AttendanceStatus) => {
    setAttendanceMap((prev) => ({
      ...prev,
      [studentId]: {
        ...prev[studentId],
        status
      }
    }));
  };

  const handleRemarksChange = (studentId: number, remarks: string) => {
    setAttendanceMap((prev) => ({
      ...prev,
      [studentId]: {
        ...prev[studentId],
        remarks
      }
    }));
  };

  const markAll = (status: AttendanceStatus) => {
    setAttendanceMap((prev) => {
      const updated = { ...prev };
      Object.keys(updated).forEach((id) => {
        updated[Number(id)] = {
          ...updated[Number(id)],
          status
        };
      });
      return updated;
    });
  };

  const handleSaveAttendance = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedCourseId) return;
    if (roster.length === 0) {
      setError('Cannot record attendance: No students enrolled in this course.');
      return;
    }

    try {
      setSubmitting(true);
      setError(null);
      setSuccess(null);

      const records = roster.map((student) => ({
        studentId: student.studentId,
        status: attendanceMap[student.studentId]?.status || 'PRESENT',
        remarks: attendanceMap[student.studentId]?.remarks || ''
      }));

      const payload = {
        courseId: selectedCourseId,
        sessionDate,
        topic: topic.trim() || 'Regular Lecture',
        records
      };

      await apiClient.post('/attendance/sessions', payload);
      setSuccess(`Attendance session successfully recorded for ${sessionDate}!`);
      setTopic('');

      // Refresh past sessions
      const sessionsRes = await apiClient.get<AttendanceSessionResponse[]>(
        `/attendance/sessions/course/${selectedCourseId}`
      );
      setPastSessions(sessionsRes.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to submit attendance session.');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center p-12 text-gray-500">
        Loading attendance dashboard...
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Faculty Attendance Register</h1>
        <p className="text-sm text-gray-500 mt-1">
          Record class meetings and track student presence using aggregate composition roots.
        </p>
      </div>

      {/* Alerts */}
      {error && (
        <div className="p-4 rounded-lg bg-red-50 border border-red-200 text-red-700 flex items-center gap-3">
          <AlertCircle className="w-5 h-5 flex-shrink-0" />
          <p className="text-sm font-medium">{error}</p>
        </div>
      )}
      {success && (
        <div className="p-4 rounded-lg bg-emerald-50 border border-emerald-200 text-emerald-700 flex items-center gap-3">
          <CheckCircle className="w-5 h-5 flex-shrink-0" />
          <p className="text-sm font-medium">{success}</p>
        </div>
      )}

      {/* Course Selector */}
      <div className="bg-white p-6 rounded-xl border border-gray-200 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <label className="block text-xs font-semibold uppercase text-gray-500 tracking-wider mb-1">
            Active Teaching Course
          </label>
          <select
            value={selectedCourseId || ''}
            onChange={(e) => setSelectedCourseId(Number(e.target.value))}
            className="text-base font-semibold text-gray-900 border border-gray-300 rounded-lg px-3 py-2 bg-gray-50 focus:bg-white focus:ring-2 focus:ring-indigo-500 focus:outline-none"
          >
            {courses.map((course) => (
              <option key={course.id} value={course.id}>
                {course.code} — {course.title} (Sem {course.semester})
              </option>
            ))}
          </select>
        </div>
        <div className="flex items-center gap-6 text-sm text-gray-600">
          <div className="flex items-center gap-2">
            <Users className="w-4 h-4 text-indigo-600" />
            <span><strong>{roster.length}</strong> Enrolled Students</span>
          </div>
          <div className="flex items-center gap-2">
            <Calendar className="w-4 h-4 text-indigo-600" />
            <span><strong>{pastSessions.length}</strong> Completed Sessions</span>
          </div>
        </div>
      </div>

      {/* Attendance Session Entry Form */}
      <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
        <div className="p-6 border-b border-gray-200">
          <h2 className="text-lg font-bold text-gray-900 flex items-center gap-2">
            <Clock className="w-5 h-5 text-indigo-600" />
            New Class Session
          </h2>
          <p className="text-xs text-gray-500 mt-0.5">
            Set date and topic, verify student presence status, and click Save.
          </p>

          <form onSubmit={handleSaveAttendance} className="mt-4 space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-medium text-gray-700 mb-1">Session Date</label>
                <input
                  type="date"
                  value={sessionDate}
                  onChange={(e) => setSessionDate(e.target.value)}
                  required
                  className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-700 mb-1">Lecture Topic / Notes</label>
                <input
                  type="text"
                  placeholder="e.g., Chapter 4: Polymorphic Method Dispatch"
                  value={topic}
                  onChange={(e) => setTopic(e.target.value)}
                  className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                />
              </div>
            </div>

            {/* Quick Actions */}
            <div className="flex items-center justify-between pt-2">
              <div className="flex items-center gap-2">
                <span className="text-xs font-medium text-gray-500">Quick Set:</span>
                <button
                  type="button"
                  onClick={() => markAll('PRESENT')}
                  className="px-2.5 py-1 text-xs font-medium rounded-md bg-emerald-50 text-emerald-700 hover:bg-emerald-100 transition-colors"
                >
                  All Present
                </button>
                <button
                  type="button"
                  onClick={() => markAll('ABSENT')}
                  className="px-2.5 py-1 text-xs font-medium rounded-md bg-rose-50 text-rose-700 hover:bg-rose-100 transition-colors"
                >
                  All Absent
                </button>
                <button
                  type="button"
                  onClick={() => markAll('LATE')}
                  className="px-2.5 py-1 text-xs font-medium rounded-md bg-amber-50 text-amber-700 hover:bg-amber-100 transition-colors"
                >
                  All Late
                </button>
              </div>

              <button
                type="submit"
                disabled={submitting || roster.length === 0}
                className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 disabled:bg-gray-300 text-white text-sm font-semibold rounded-lg shadow-sm transition-colors"
              >
                <Save className="w-4 h-4" />
                {submitting ? 'Saving Session...' : 'Submit Attendance'}
              </button>
            </div>

            {/* Roster Table */}
            <div className="border border-gray-200 rounded-lg overflow-hidden mt-4">
              {roster.length === 0 ? (
                <div className="p-8 text-center text-gray-400">
                  <Users className="w-10 h-10 mx-auto mb-2 opacity-30" />
                  <p className="text-sm font-medium">No students currently enrolled in this course.</p>
                </div>
              ) : (
                <table className="w-full text-left text-sm text-gray-600">
                  <thead className="bg-gray-50 text-xs uppercase text-gray-700 border-b border-gray-200">
                    <tr>
                      <th className="px-6 py-3 font-semibold">Student Name</th>
                      <th className="px-6 py-3 font-semibold">Roll Number</th>
                      <th className="px-6 py-3 font-semibold text-center">Status</th>
                      <th className="px-6 py-3 font-semibold">Remarks (Optional)</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-200">
                    {roster.map((student) => {
                      const currentStatus = attendanceMap[student.studentId]?.status || 'PRESENT';
                      return (
                        <tr key={student.studentId} className="hover:bg-gray-50 transition-colors">
                          <td className="px-6 py-3 font-medium text-gray-900">
                            {student.studentName}
                          </td>
                          <td className="px-6 py-3 font-mono text-xs text-gray-500">
                            {student.rollNumber}
                          </td>
                          <td className="px-6 py-3">
                            <div className="flex items-center justify-center gap-1.5">
                              <button
                                type="button"
                                onClick={() => handleStatusChange(student.studentId, 'PRESENT')}
                                className={`px-2.5 py-1 rounded text-xs font-semibold transition-colors ${
                                  currentStatus === 'PRESENT'
                                    ? 'bg-emerald-600 text-white shadow-sm'
                                    : 'bg-emerald-50 text-emerald-700 hover:bg-emerald-100'
                                }`}
                              >
                                Present
                              </button>
                              <button
                                type="button"
                                onClick={() => handleStatusChange(student.studentId, 'ABSENT')}
                                className={`px-2.5 py-1 rounded text-xs font-semibold transition-colors ${
                                  currentStatus === 'ABSENT'
                                    ? 'bg-rose-600 text-white shadow-sm'
                                    : 'bg-rose-50 text-rose-700 hover:bg-rose-100'
                                }`}
                              >
                                Absent
                              </button>
                              <button
                                type="button"
                                onClick={() => handleStatusChange(student.studentId, 'LATE')}
                                className={`px-2.5 py-1 rounded text-xs font-semibold transition-colors ${
                                  currentStatus === 'LATE'
                                    ? 'bg-amber-500 text-white shadow-sm'
                                    : 'bg-amber-50 text-amber-700 hover:bg-amber-100'
                                }`}
                              >
                                Late
                              </button>
                            </div>
                          </td>
                          <td className="px-6 py-3">
                            <input
                              type="text"
                              placeholder="e.g. Excused, Medical..."
                              value={attendanceMap[student.studentId]?.remarks || ''}
                              onChange={(e) => handleRemarksChange(student.studentId, e.target.value)}
                              className="w-full text-xs border border-gray-200 rounded px-2.5 py-1 focus:ring-1 focus:ring-indigo-500 focus:outline-none"
                            />
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              )}
            </div>
          </form>
        </div>
      </div>

      {/* Historical Sessions */}
      <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
        <div className="p-6 border-b border-gray-200">
          <h2 className="text-lg font-bold text-gray-900 flex items-center gap-2">
            <BookOpen className="w-5 h-5 text-indigo-600" />
            Class Session History
          </h2>
          <p className="text-xs text-gray-500 mt-0.5">
            Previous attendance sessions recorded for this course. Click on any session to review individual student records.
          </p>
        </div>

        {pastSessions.length === 0 ? (
          <div className="p-8 text-center text-gray-400">
            <Calendar className="w-10 h-10 mx-auto mb-2 opacity-30" />
            <p className="text-sm font-medium">No previous class sessions found for this course.</p>
          </div>
        ) : (
          <div className="divide-y divide-gray-200">
            {pastSessions.map((session) => {
              const pct = session.totalRecords > 0 
                ? Math.round((session.presentCount / session.totalRecords) * 100) 
                : 0;
              const isExpanded = expandedSessionId === session.id;

              return (
                <div key={session.id} className="p-4">
                  <div 
                    onClick={() => setExpandedSessionId(isExpanded ? null : session.id)}
                    className="flex flex-col md:flex-row md:items-center justify-between gap-3 cursor-pointer hover:bg-gray-50 p-2 rounded-lg transition-colors"
                  >
                    <div>
                      <div className="flex items-center gap-3">
                        <span className="font-semibold text-gray-900">
                          {new Date(session.sessionDate).toLocaleDateString(undefined, { 
                            weekday: 'short', 
                            year: 'numeric', 
                            month: 'short', 
                            day: 'numeric' 
                          })}
                        </span>
                        <span className="text-xs font-medium px-2 py-0.5 rounded bg-indigo-50 text-indigo-700">
                          {session.topic || 'Class Meeting'}
                        </span>
                      </div>
                      <div className="text-xs text-gray-500 mt-1">
                        Attendance: {session.presentCount} / {session.totalRecords} present ({pct}%)
                      </div>
                    </div>

                    <div className="flex items-center gap-4">
                      <div className="w-32 bg-gray-200 rounded-full h-2 overflow-hidden">
                        <div 
                          className={`h-2 rounded-full ${pct >= 75 ? 'bg-emerald-500' : 'bg-amber-500'}`}
                          style={{ width: `${pct}%` }}
                        />
                      </div>
                      {isExpanded ? (
                        <ChevronUp className="w-5 h-5 text-gray-400" />
                      ) : (
                        <ChevronDown className="w-5 h-5 text-gray-400" />
                      )}
                    </div>
                  </div>

                  {/* Expanded Student Breakdown */}
                  {isExpanded && (
                    <div className="mt-3 pl-4 border-l-2 border-indigo-200 space-y-2">
                      <table className="w-full text-left text-xs text-gray-600">
                        <thead className="text-gray-400 font-semibold border-b border-gray-100">
                          <tr>
                            <th className="py-2">Student</th>
                            <th className="py-2">Roll Number</th>
                            <th className="py-2">Status</th>
                            <th className="py-2">Remarks</th>
                          </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100">
                          {session.records.map((r) => (
                            <tr key={r.id}>
                              <td className="py-2 font-medium text-gray-900">{r.studentName}</td>
                              <td className="py-2 font-mono text-gray-500">{r.rollNumber}</td>
                              <td className="py-2">
                                <span className={`inline-flex items-center gap-1 font-semibold ${
                                  r.status === 'PRESENT' 
                                    ? 'text-emerald-700' 
                                    : r.status === 'LATE' 
                                    ? 'text-amber-700' 
                                    : 'text-rose-700'
                                }`}>
                                  {r.status === 'PRESENT' && <Check className="w-3 h-3" />}
                                  {r.status === 'ABSENT' && <X className="w-3 h-3" />}
                                  {r.status}
                                </span>
                              </td>
                              <td className="py-2 text-gray-400">{r.remarks || '—'}</td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
};
