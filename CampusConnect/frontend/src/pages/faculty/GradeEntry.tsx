import React, { useState, useEffect } from 'react';
import { apiClient } from '../../api/client';
import { Course } from '../../types/admin';
import { Enrollment } from '../../types/enrollment';
import { AssessmentResponse, AssessmentType, GradeResponse, CourseGradeDistribution } from '../../types/grades';
import { 
  Award, 
  PlusCircle, 
  CheckCircle, 
  AlertCircle, 
  Save, 
  BookOpen, 
  TrendingUp,
  BarChart2,
  X
} from 'lucide-react';

export const GradeEntry: React.FC = () => {
  const [courses, setCourses] = useState<Course[]>([]);
  const [selectedCourseId, setSelectedCourseId] = useState<number | null>(null);
  const [assessments, setAssessments] = useState<AssessmentResponse[]>([]);
  const [selectedAssessmentId, setSelectedAssessmentId] = useState<number | null>(null);
  const [roster, setRoster] = useState<Enrollment[]>([]);
  const [existingGrades, setExistingGrades] = useState<GradeResponse[]>([]);
  const [gradingMode, setGradingMode] = useState<'ABSOLUTE' | 'RELATIVE'>('ABSOLUTE');
  const [distribution, setDistribution] = useState<CourseGradeDistribution | null>(null);

  // Form states for creating new assessment component
  const [showNewAssessmentModal, setShowNewAssessmentModal] = useState<boolean>(false);
  const [assessmentName, setAssessmentName] = useState('');
  const [assessmentType, setAssessmentType] = useState<AssessmentType>('MIDTERM');
  const [maxMarks, setMaxMarks] = useState<number>(100);
  const [weightage, setWeightage] = useState<number>(30);

  // Marks inputs per enrollment: enrollmentId -> marksObtained
  const [marksMap, setMarksMap] = useState<Record<number, number>>({});

  const [loading, setLoading] = useState<boolean>(true);
  const [saving, setSaving] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

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
        setError(err.response?.data?.message || 'Failed to load courses');
      } finally {
        setLoading(false);
      }
    };
    fetchCourses();
  }, []);

  // When course changes, load assessments, roster, and existing grades
  useEffect(() => {
    if (!selectedCourseId) return;

    const fetchCourseData = async () => {
      try {
        const [assessmentsRes, rosterRes, gradesRes, distRes] = await Promise.all([
          apiClient.get<AssessmentResponse[]>(`/grades/assessments/course/${selectedCourseId}`),
          apiClient.get<Enrollment[]>(`/enrollments/course/${selectedCourseId}`),
          apiClient.get<GradeResponse[]>(`/grades/course/${selectedCourseId}`),
          apiClient.get<CourseGradeDistribution>(`/grades/course/${selectedCourseId}/distribution`).catch(() => ({ data: null }))
        ]);

        setAssessments(assessmentsRes.data);
        const activeStudents = rosterRes.data.filter(
          (e) => e.status === 'ENROLLED' || e.status === 'APPROVED'
        );
        setRoster(activeStudents);
        setExistingGrades(gradesRes.data);
        setDistribution(distRes.data);

        if (assessmentsRes.data.length > 0) {
          setSelectedAssessmentId(assessmentsRes.data[0].id);
        } else {
          setSelectedAssessmentId(null);
        }
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load course assessments');
      }
    };

    fetchCourseData();
  }, [selectedCourseId]);

  // When selected assessment changes, initialize marksMap with any existing grades
  useEffect(() => {
    if (!selectedAssessmentId) {
      setMarksMap({});
      return;
    }

    const currentGrades = existingGrades.filter((g) => g.assessmentId === selectedAssessmentId);
    const newMap: Record<number, number> = {};
    currentGrades.forEach((g) => {
      newMap[g.enrollmentId] = g.marksObtained;
    });
    setMarksMap(newMap);
  }, [selectedAssessmentId, existingGrades]);

  const handleCreateAssessment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedCourseId) return;

    try {
      setSaving(true);
      setError(null);
      const payload = {
        courseId: selectedCourseId,
        name: assessmentName.trim(),
        type: assessmentType,
        maxMarks,
        weightagePercentage: weightage
      };

      const res = await apiClient.post<AssessmentResponse>('/grades/assessments', payload);
      setAssessments((prev) => [...prev, res.data]);
      setSelectedAssessmentId(res.data.id);
      setShowNewAssessmentModal(false);
      setAssessmentName('');
      setSuccess(`Assessment component "${res.data.name}" successfully created!`);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create assessment component');
    } finally {
      setSaving(false);
    }
  };

  const handleMarksChange = (enrollmentId: number, val: string) => {
    const num = parseFloat(val);
    setMarksMap((prev) => ({
      ...prev,
      [enrollmentId]: isNaN(num) ? 0 : num
    }));
  };

  const currentAssessment = assessments.find((a) => a.id === selectedAssessmentId);

  const handleSaveGrades = async () => {
    if (!selectedAssessmentId || !currentAssessment) return;

    try {
      setSaving(true);
      setError(null);
      setSuccess(null);

      const batch = roster.map((student) => ({
        enrollmentId: student.id,
        assessmentId: selectedAssessmentId,
        marksObtained: marksMap[student.id] ?? 0,
        gradingMode: gradingMode
      }));

      const res = await apiClient.post<GradeResponse[]>('/grades/batch', batch);
      setSuccess(`Grades evaluated using ${gradingMode === 'RELATIVE' ? 'Relative Norm-Referenced Curve' : 'Absolute Criterion'} Strategy for ${res.data.length} students!`);

      // Refresh course grades & distribution
      const [gradesRes, distRes] = await Promise.all([
        apiClient.get<GradeResponse[]>(`/grades/course/${selectedCourseId}`),
        apiClient.get<CourseGradeDistribution>(`/grades/course/${selectedCourseId}/distribution`).catch(() => ({ data: null }))
      ]);
      setExistingGrades(gradesRes.data);
      setDistribution(distRes.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to save student grades');
    } finally {
      setSaving(false);
    }
  };

  const calculatePreviewGrade = (marks: number, max: number) => {
    if (!max || max <= 0) return { letter: '—', point: 0.0 };
    const pct = (marks / max) * 100;
    if (pct >= 90) return { letter: 'A+', point: 4.0 };
    if (pct >= 80) return { letter: 'A', point: 4.0 };
    if (pct >= 75) return { letter: 'B+', point: 3.5 };
    if (pct >= 70) return { letter: 'B', point: 3.0 };
    if (pct >= 65) return { letter: 'C+', point: 2.5 };
    if (pct >= 60) return { letter: 'C', point: 2.0 };
    if (pct >= 50) return { letter: 'D', point: 1.0 };
    return { letter: 'F', point: 0.0 };
  };

  if (loading) {
    return <div className="p-12 text-center text-gray-500">Loading grade management system...</div>;
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Academic Grading Center</h1>
          <p className="text-sm text-gray-500 mt-1">
            Evaluate student coursework and compute grades according to academic grading policies.
          </p>
        </div>
        <button
          onClick={() => setShowNewAssessmentModal(true)}
          className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg text-sm font-semibold shadow-sm transition-colors"
        >
          <PlusCircle className="w-4 h-4" />
          Add Assessment Component
        </button>
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

      {/* Course & Assessment Selectors */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 bg-white p-6 rounded-xl border border-gray-200 shadow-sm">
        <div>
          <label className="block text-xs font-semibold uppercase text-gray-500 tracking-wider mb-1">
            Course Selection
          </label>
          <select
            value={selectedCourseId || ''}
            onChange={(e) => setSelectedCourseId(Number(e.target.value))}
            className="w-full text-sm font-semibold text-gray-900 border border-gray-300 rounded-lg px-3 py-2 bg-gray-50 focus:bg-white focus:ring-2 focus:ring-indigo-500 focus:outline-none"
          >
            {courses.map((course) => (
              <option key={course.id} value={course.id}>
                {course.code} — {course.title}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label className="block text-xs font-semibold uppercase text-gray-500 tracking-wider mb-1">
            Assessment Component
          </label>
          {assessments.length === 0 ? (
            <div className="text-sm text-gray-400 italic py-2">
              No assessment components defined yet. Click "Add Assessment Component" above.
            </div>
          ) : (
            <select
              value={selectedAssessmentId || ''}
              onChange={(e) => setSelectedAssessmentId(Number(e.target.value))}
              className="w-full text-sm font-semibold text-gray-900 border border-gray-300 rounded-lg px-3 py-2 bg-gray-50 focus:bg-white focus:ring-2 focus:ring-indigo-500 focus:outline-none"
            >
              {assessments.map((a) => (
                <option key={a.id} value={a.id}>
                  {a.name} ({a.type}) — Max {a.maxMarks} Marks ({a.weightagePercentage}% Weightage)
                </option>
              ))}
            </select>
          )}
        </div>
      </div>

      {/* Strategy Pattern Selection & Cohort Distribution Banner */}
      <div className="bg-white p-6 rounded-xl border border-gray-200 shadow-sm space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-2">
              <TrendingUp className="w-5 h-5 text-indigo-600" />
              <h3 className="font-bold text-gray-900 text-sm">Grading Evaluation Policy</h3>
            </div>
            <p className="text-xs text-gray-500 mt-0.5">
              Select standard criterion grading or relative bell-curve grading for this evaluation.
            </p>
          </div>

          <div className="inline-flex rounded-lg border border-gray-200 bg-gray-50 p-1">
            <button
              type="button"
              onClick={() => setGradingMode('ABSOLUTE')}
              className={`px-3 py-1.5 rounded-md text-xs font-semibold transition-all ${
                gradingMode === 'ABSOLUTE'
                  ? 'bg-white text-indigo-700 shadow-xs'
                  : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              Absolute Criterion
            </button>
            <button
              type="button"
              onClick={() => setGradingMode('RELATIVE')}
              className={`px-3 py-1.5 rounded-md text-xs font-semibold transition-all ${
                gradingMode === 'RELATIVE'
                  ? 'bg-white text-indigo-700 shadow-xs'
                  : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              Relative Bell Curve
            </button>
          </div>
        </div>

        {distribution && distribution.totalGradedAssessments > 0 && (
          <div className="pt-3 border-t border-gray-100 flex flex-wrap items-center justify-between gap-4 text-xs">
            <div className="flex items-center gap-6">
              <div>
                <span className="text-gray-500">Graded Submissions: </span>
                <span className="font-bold text-gray-900">{distribution.totalGradedAssessments}</span>
              </div>
              <div>
                <span className="text-gray-500">Class Mean (μ): </span>
                <span className="font-bold text-indigo-600">{distribution.averagePercentage}%</span>
              </div>
              <div>
                <span className="text-gray-500">Std Dev (σ): </span>
                <span className="font-bold text-purple-600">±{distribution.standardDeviation}%</span>
              </div>
            </div>

            <div className="flex items-center gap-2">
              <BarChart2 className="w-4 h-4 text-gray-400" />
              <span className="text-gray-500">Current Distribution Mode: </span>
              <span className="font-semibold text-gray-800">
                {gradingMode === 'RELATIVE' ? 'Gaussian Z-Score Curved' : 'Fixed Absolute Thresholds'}
              </span>
            </div>
          </div>
        )}
      </div>

      {/* Assessment Component Grade Entry Table */}
      {currentAssessment && (
        <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
          <div className="p-6 border-b border-gray-200 flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div>
              <div className="flex items-center gap-3">
                <h2 className="text-lg font-bold text-gray-900">{currentAssessment.name}</h2>
                <span className="text-xs font-semibold px-2.5 py-0.5 rounded bg-indigo-50 text-indigo-700">
                  {currentAssessment.type}
                </span>
              </div>
              <p className="text-xs text-gray-500 mt-1">
                Max Marks: <strong>{currentAssessment.maxMarks}</strong> | Weightage in Final Grade: <strong>{currentAssessment.weightagePercentage}%</strong>
              </p>
            </div>

            <button
              onClick={handleSaveGrades}
              disabled={saving || roster.length === 0}
              className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 disabled:bg-gray-300 text-white rounded-lg text-sm font-semibold shadow-sm transition-colors"
            >
              <Save className="w-4 h-4" />
              {saving ? 'Evaluating & Saving...' : 'Save Evaluated Grades'}
            </button>
          </div>

          {roster.length === 0 ? (
            <div className="p-8 text-center text-gray-400">
              <BookOpen className="w-10 h-10 mx-auto mb-2 opacity-30" />
              <p className="text-sm font-medium">No enrolled students in this course roster.</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm text-gray-600">
                <thead className="bg-gray-50 text-xs uppercase text-gray-700 border-b border-gray-200">
                  <tr>
                    <th className="px-6 py-3 font-semibold">Student Name</th>
                    <th className="px-6 py-3 font-semibold">Roll Number</th>
                    <th className="px-6 py-3 font-semibold">Marks Obtained (Out of {currentAssessment.maxMarks})</th>
                    <th className="px-6 py-3 font-semibold">Calculated Percentage</th>
                    <th className="px-6 py-3 font-semibold">Strategy Letter Grade</th>
                    <th className="px-6 py-3 font-semibold">Grade Point</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {roster.map((student) => {
                    const currentMarks = marksMap[student.id] ?? 0;
                    const preview = calculatePreviewGrade(currentMarks, currentAssessment.maxMarks);
                    const pct = currentAssessment.maxMarks > 0 
                      ? Math.round((currentMarks / currentAssessment.maxMarks) * 1000) / 10 
                      : 0;

                    return (
                      <tr key={student.id} className="hover:bg-gray-50 transition-colors">
                        <td className="px-6 py-3 font-medium text-gray-900">
                          {student.studentName}
                        </td>
                        <td className="px-6 py-3 font-mono text-xs text-gray-500">
                          {student.rollNumber}
                        </td>
                        <td className="px-6 py-3">
                          <input
                            type="number"
                            min="0"
                            max={currentAssessment.maxMarks}
                            step="0.5"
                            value={marksMap[student.id] ?? ''}
                            onChange={(e) => handleMarksChange(student.id, e.target.value)}
                            placeholder="0"
                            className="w-28 text-sm font-semibold border border-gray-300 rounded px-3 py-1.5 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                          />
                        </td>
                        <td className="px-6 py-3 font-mono text-sm text-gray-700">
                          {pct}%
                        </td>
                        <td className="px-6 py-3">
                          <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold ${
                            preview.letter === 'A+' || preview.letter === 'A'
                              ? 'bg-emerald-100 text-emerald-800'
                              : preview.letter === 'B+' || preview.letter === 'B'
                              ? 'bg-blue-100 text-blue-800'
                              : preview.letter === 'C+' || preview.letter === 'C'
                              ? 'bg-amber-100 text-amber-800'
                              : preview.letter === 'D'
                              ? 'bg-orange-100 text-orange-800'
                              : 'bg-rose-100 text-rose-800'
                          }`}>
                            {preview.letter}
                          </span>
                        </td>
                        <td className="px-6 py-3 font-mono font-bold text-gray-800">
                          {preview.point.toFixed(1)}
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* Modal for Creating New Assessment Component */}
      {showNewAssessmentModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="bg-white rounded-2xl shadow-xl max-w-md w-full overflow-hidden border border-gray-200">
            <div className="p-6 border-b border-gray-100 flex items-center justify-between">
              <h3 className="text-lg font-bold text-gray-900 flex items-center gap-2">
                <Award className="w-5 h-5 text-indigo-600" />
                New Assessment Component
              </h3>
              <button
                onClick={() => setShowNewAssessmentModal(false)}
                className="text-gray-400 hover:text-gray-600 p-1 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateAssessment} className="p-6 space-y-4">
              <div>
                <label className="block text-xs font-medium text-gray-700 mb-1">Assessment Name</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Midterm Examination, Quiz 1, Term Paper"
                  value={assessmentName}
                  onChange={(e) => setAssessmentName(e.target.value)}
                  className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-gray-700 mb-1">Assessment Type</label>
                <select
                  value={assessmentType}
                  onChange={(e) => setAssessmentType(e.target.value as AssessmentType)}
                  className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                >
                  <option value="MIDTERM">MIDTERM</option>
                  <option value="FINAL_EXAM">FINAL EXAM</option>
                  <option value="QUIZ">QUIZ</option>
                  <option value="ASSIGNMENT">ASSIGNMENT</option>
                  <option value="PROJECT">PROJECT</option>
                </select>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-gray-700 mb-1">Maximum Marks</label>
                  <input
                    type="number"
                    min="1"
                    required
                    value={maxMarks}
                    onChange={(e) => setMaxMarks(Number(e.target.value))}
                    className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-700 mb-1">Weightage (%)</label>
                  <input
                    type="number"
                    min="1"
                    max="100"
                    required
                    value={weightage}
                    onChange={(e) => setWeightage(Number(e.target.value))}
                    className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                  />
                </div>
              </div>

              <div className="flex items-center justify-end gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => setShowNewAssessmentModal(false)}
                  className="px-4 py-2 border border-gray-300 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={saving}
                  className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg text-sm font-semibold shadow-sm transition-colors"
                >
                  {saving ? 'Creating...' : 'Create Component'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
