import React, { useState, useEffect } from 'react';
import { apiClient } from '../../api/client';
import { StudentCourseGradeReport } from '../../types/grades';
import { 
  Award, 
  BookOpen, 
  Sparkles, 
  ChevronDown, 
  ChevronUp, 
  AlertCircle
} from 'lucide-react';

export const StudentGrades: React.FC = () => {
  const [reports, setReports] = useState<StudentCourseGradeReport[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [expandedCourseId, setExpandedCourseId] = useState<number | null>(null);

  useEffect(() => {
    const fetchMyGrades = async () => {
      try {
        setLoading(true);
        const res = await apiClient.get<StudentCourseGradeReport[]>('/grades/my-grades');
        setReports(res.data);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to fetch personal grades report.');
      } finally {
        setLoading(false);
      }
    };

    fetchMyGrades();
  }, []);

  if (loading) {
    return <div className="p-12 text-center text-gray-500">Loading student grade transcript...</div>;
  }

  // Calculate Cumulative GPA
  const totalCredits = reports.reduce((acc, curr) => acc + curr.credits, 0);
  const totalWeightedPoints = reports.reduce((acc, curr) => acc + curr.gradePoint * curr.credits, 0);
  const cgpa = totalCredits > 0 ? (totalWeightedPoints / totalCredits).toFixed(2) : '0.00';

  const getBadgeColor = (grade: string) => {
    switch (grade) {
      case 'A+':
      case 'A':
        return 'bg-emerald-100 text-emerald-800 border-emerald-200';
      case 'B+':
      case 'B':
        return 'bg-blue-100 text-blue-800 border-blue-200';
      case 'C+':
      case 'C':
        return 'bg-amber-100 text-amber-800 border-amber-200';
      case 'D':
        return 'bg-orange-100 text-orange-800 border-orange-200';
      default:
        return 'bg-rose-100 text-rose-800 border-rose-200';
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Academic Grades & GPA Report</h1>
        <p className="text-sm text-gray-500 mt-1">
          Review course evaluations, assessment breakdowns, and cumulative grade point performance.
        </p>
      </div>

      {/* Error Banner */}
      {error && (
        <div className="p-4 rounded-lg bg-red-50 border border-red-200 text-red-700 flex items-center gap-3">
          <AlertCircle className="w-5 h-5 flex-shrink-0" />
          <p className="text-sm font-medium">{error}</p>
        </div>
      )}

      {/* Cumulative Performance Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="bg-white p-5 rounded-xl border border-gray-200 shadow-sm">
          <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider">Cumulative GPA (CGPA)</p>
          <div className="flex items-baseline gap-2 mt-2">
            <span className="text-3xl font-extrabold text-indigo-600">{cgpa}</span>
            <span className="text-xs text-gray-400">on 4.0 scale</span>
          </div>
        </div>

        <div className="bg-white p-5 rounded-xl border border-gray-200 shadow-sm">
          <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider">Total Credit Hours</p>
          <div className="flex items-baseline gap-2 mt-2">
            <span className="text-3xl font-extrabold text-gray-900">{totalCredits}</span>
            <span className="text-xs text-gray-500">registered semester credits</span>
          </div>
        </div>

        <div className="bg-white p-5 rounded-xl border border-gray-200 shadow-sm">
          <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider">Academic Standing</p>
          <div className="flex items-center gap-2 mt-2">
            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-emerald-100 text-emerald-800">
              <Sparkles className="w-4 h-4 text-emerald-600" />
              {parseFloat(cgpa) >= 3.5 ? 'Dean\'s Honor List' : 'Good Academic Standing'}
            </span>
          </div>
        </div>
      </div>

      {/* Course Evaluation Reports */}
      <div className="space-y-4">
        {reports.length === 0 ? (
          <div className="bg-white p-12 rounded-xl border border-gray-200 text-center text-gray-400">
            <BookOpen className="w-12 h-12 mx-auto mb-3 opacity-30" />
            <p className="text-base font-semibold">No course grade records available.</p>
            <p className="text-xs text-gray-400 mt-1">Grades will appear here once faculty evaluate your assignments and exams.</p>
          </div>
        ) : (
          reports.map((course) => {
            const isExpanded = expandedCourseId === course.courseId;

            return (
              <div key={course.courseId} className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
                {/* Course Header */}
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
                        Weighted Total Score: <strong>{course.totalWeightedScore}%</strong> | {course.assessments.length} assessment(s) evaluated
                      </p>
                    </div>
                  </div>

                  <div className="flex items-center gap-4">
                    <div className="flex items-center gap-2">
                      <span className={`px-3 py-1 rounded-full text-sm font-extrabold border ${getBadgeColor(course.finalGrade)}`}>
                        {course.finalGrade} ({course.gradePoint.toFixed(1)} GP)
                      </span>
                    </div>

                    {isExpanded ? (
                      <ChevronUp className="w-5 h-5 text-gray-400" />
                    ) : (
                      <ChevronDown className="w-5 h-5 text-gray-400" />
                    )}
                  </div>
                </div>

                {/* Expanded Assessments Breakdown */}
                {isExpanded && (
                  <div className="border-t border-gray-100 bg-gray-50/50 p-5 space-y-3">
                    <h5 className="text-xs font-bold uppercase tracking-wider text-gray-500 flex items-center gap-1.5">
                      <Award className="w-3.5 h-3.5 text-indigo-600" />
                      Assessment Components Breakdown
                    </h5>

                    {course.assessments.length === 0 ? (
                      <p className="text-xs text-gray-400 italic">No specific assessments graded yet for this course.</p>
                    ) : (
                      <div className="overflow-x-auto bg-white rounded-lg border border-gray-200">
                        <table className="w-full text-left text-xs text-gray-600">
                          <thead className="bg-gray-50 text-gray-700 font-semibold border-b border-gray-200">
                            <tr>
                              <th className="px-4 py-2.5">Component</th>
                              <th className="px-4 py-2.5">Type</th>
                              <th className="px-4 py-2.5">Marks Obtained</th>
                              <th className="px-4 py-2.5">Max Marks</th>
                              <th className="px-4 py-2.5">Letter Grade</th>
                              <th className="px-4 py-2.5">Evaluation Remarks</th>
                            </tr>
                          </thead>
                          <tbody className="divide-y divide-gray-100">
                            {course.assessments.map((a) => (
                              <tr key={a.id} className="hover:bg-gray-50">
                                <td className="px-4 py-2.5 font-medium text-gray-900">{a.assessmentName}</td>
                                <td className="px-4 py-2.5">
                                  <span className="px-2 py-0.5 rounded bg-gray-100 text-gray-700 font-mono text-[10px]">
                                    {a.assessmentType}
                                  </span>
                                </td>
                                <td className="px-4 py-2.5 font-semibold text-gray-900">{a.marksObtained}</td>
                                <td className="px-4 py-2.5 text-gray-500">{a.maxMarks}</td>
                                <td className="px-4 py-2.5">
                                  <span className={`px-2 py-0.5 rounded text-xs font-bold ${
                                    a.letterGrade === 'A+' || a.letterGrade === 'A'
                                      ? 'bg-emerald-100 text-emerald-800'
                                      : a.letterGrade === 'B+' || a.letterGrade === 'B'
                                      ? 'bg-blue-100 text-blue-800'
                                      : 'bg-amber-100 text-amber-800'
                                  }`}>
                                    {a.letterGrade}
                                  </span>
                                </td>
                                <td className="px-4 py-2.5 text-gray-500 italic">{a.remark || '—'}</td>
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
