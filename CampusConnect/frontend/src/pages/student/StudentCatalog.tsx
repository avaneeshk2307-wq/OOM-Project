import React, { useEffect, useState } from 'react';
import { apiClient } from '../../api/client';
import { Course } from '../../types/admin';
import { Enrollment } from '../../types/enrollment';
import { 
  CheckCircle2, 
  AlertCircle, 
  Layers, 
  UserCheck, 
  Search,
  Sparkles
} from 'lucide-react';

export const StudentCatalog: React.FC = () => {
  const [courses, setCourses] = useState<Course[]>([]);
  const [myEnrollments, setMyEnrollments] = useState<Enrollment[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [search, setSearch] = useState('');
  const [registeringId, setRegisteringId] = useState<number | null>(null);

  const loadCatalog = async () => {
    setLoading(true);
    try {
      const [coursesRes, enrollRes] = await Promise.all([
        apiClient.get<Course[]>('/admin/courses'),
        apiClient.get<Enrollment[]>('/enrollments/my-enrollments'),
      ]);
      setCourses(coursesRes.data);
      setMyEnrollments(enrollRes.data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Could not load course catalog.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCatalog();
  }, []);

  const isAlreadyRegistered = (courseId: number) => {
    return myEnrollments.some(
      (e) => e.courseId === courseId && e.status !== 'DROPPED' && e.status !== 'REJECTED'
    );
  };

  const handleRegister = async (courseId: number, courseCode: string) => {
    setRegisteringId(courseId);
    setError(null);
    try {
      await apiClient.post('/enrollments/register', { courseId });
      setSuccess(`Registration submitted for ${courseCode}. Awaiting administrative approval.`);
      loadCatalog();
      setTimeout(() => setSuccess(null), 4000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Registration failed.');
    } finally {
      setRegisteringId(null);
    }
  };

  const filteredCourses = courses.filter((c) =>
    c.title.toLowerCase().includes(search.toLowerCase()) ||
    c.code.toLowerCase().includes(search.toLowerCase()) ||
    (c.departmentName && c.departmentName.toLowerCase().includes(search.toLowerCase()))
  );

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white p-6 rounded-xl border border-slate-200/80 shadow-sm flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Course Registration Catalog</h1>
          <p className="text-sm text-slate-500 mt-1">Browse active academic courses, view syllabus details, and register for classes.</p>
        </div>

        <div className="relative w-full sm:w-64">
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
            <Search className="w-4 h-4" />
          </div>
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search subjects..."
            className="w-full pl-9 pr-3 py-2 bg-slate-50 border border-slate-200 rounded-lg text-sm text-slate-900 focus:outline-none focus:ring-2 focus:ring-indigo-600"
          />
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

      {/* Catalog Cards Grid */}
      {loading ? (
        <div className="p-12 text-center text-slate-400 text-sm">Loading course offerings...</div>
      ) : filteredCourses.length === 0 ? (
        <div className="p-12 text-center text-slate-400 text-sm">No courses matching search.</div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {filteredCourses.map((c) => {
            const registered = isAlreadyRegistered(c.id);
            const isFull = c.enrolledCount >= c.capacity;

            return (
              <div
                key={c.id}
                className="bg-white rounded-xl border border-slate-200/80 p-6 shadow-sm hover:border-indigo-300 transition-all flex flex-col justify-between space-y-4"
              >
                <div className="space-y-3">
                  <div className="flex items-start justify-between gap-3">
                    <div>
                      <span className="font-mono text-xs font-bold text-indigo-700 bg-indigo-50 px-2 py-0.5 rounded border border-indigo-100">
                        {c.code}
                      </span>
                      <span className="ml-2 text-xs font-semibold px-2 py-0.5 rounded-full bg-slate-100 text-slate-700">
                        Semester {c.semester}
                      </span>
                    </div>
                    <span className="text-xs font-semibold text-slate-500">
                      {c.credits} Credits
                    </span>
                  </div>

                  <h3 className="text-lg font-bold text-slate-900 leading-snug">
                    {c.title}
                  </h3>

                  <p className="text-xs text-slate-600 line-clamp-2 leading-relaxed">
                    {c.description || 'No prerequisite description specified for this syllabus.'}
                  </p>

                  <div className="flex flex-wrap items-center gap-3 pt-1 text-xs text-slate-500">
                    <span className="inline-flex items-center gap-1 font-medium text-slate-700">
                      <Layers className="w-3.5 h-3.5 text-slate-400" />
                      {c.departmentName || 'General Curriculum'}
                    </span>
                    <span>&bull;</span>
                    <span className="inline-flex items-center gap-1">
                      <UserCheck className="w-3.5 h-3.5 text-slate-400" />
                      {c.instructorName || 'Instructor TBA'}
                    </span>
                  </div>
                </div>

                <div className="pt-4 border-t border-slate-100 flex items-center justify-between">
                  <div className="text-xs font-medium text-slate-600">
                    Capacity:{' '}
                    <span className={`font-semibold ${isFull ? 'text-red-600' : 'text-slate-800'}`}>
                      {c.enrolledCount} / {c.capacity} Seats
                    </span>
                  </div>

                  {registered ? (
                    <span className="inline-flex items-center gap-1 px-3 py-1.5 rounded-lg text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                      <CheckCircle2 className="w-3.5 h-3.5" />
                      Registered
                    </span>
                  ) : isFull ? (
                    <span className="px-3 py-1.5 rounded-lg text-xs font-semibold bg-slate-100 text-slate-500">
                      Class Full
                    </span>
                  ) : (
                    <button
                      onClick={() => handleRegister(c.id, c.code)}
                      disabled={registeringId === c.id}
                      className="inline-flex items-center gap-1.5 px-4 py-1.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg text-xs font-semibold transition-colors shadow-sm disabled:opacity-50"
                    >
                      <Sparkles className="w-3.5 h-3.5" />
                      <span>{registeringId === c.id ? 'Submitting...' : 'Register Course'}</span>
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
