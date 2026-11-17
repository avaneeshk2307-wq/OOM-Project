import React, { useEffect, useState } from 'react';
import { analyticsApi } from '../../api/analytics';
import { CampusAnalyticsResponse } from '../../types/analytics';
import { 
  BarChart, 
  Bar, 
  PieChart, 
  Pie, 
  Cell, 
  XAxis, 
  YAxis, 
  Tooltip, 
  Legend, 
  ResponsiveContainer, 
  CartesianGrid 
} from 'recharts';
import { 
  BarChart3, 
  PieChart as PieIcon, 
  TrendingUp, 
  Users, 
  BookOpen, 
  CalendarCheck, 
  AlertCircle 
} from 'lucide-react';

const COLORS = ['#4f46e5', '#06b6d4', '#10b981', '#f59e0b', '#ec4899', '#8b5cf6'];
const GRADE_COLORS: Record<string, string> = {
  'A+': '#10b981',
  'A': '#34d399',
  'B+': '#3b82f6',
  'B': '#60a5fa',
  'C+': '#f59e0b',
  'C': '#fbbf24',
  'D': '#f97316',
  'F': '#ef4444',
};

export const AdminAnalytics: React.FC = () => {
  const [data, setData] = useState<CampusAnalyticsResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchAnalytics = async () => {
      try {
        setLoading(true);
        setError(null);
        const res = await analyticsApi.getOverview();
        setData(res);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load institutional analytics.');
      } finally {
        setLoading(false);
      }
    };

    fetchAnalytics();
  }, []);

  if (loading) {
    return (
      <div className="flex justify-center items-center py-24">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  if (error || !data) {
    return (
      <div className="p-6 bg-rose-50 border border-rose-200 rounded-2xl flex items-center gap-3 text-rose-800 text-sm">
        <AlertCircle className="w-5 h-5 flex-shrink-0" />
        <span className="font-medium">{error || 'Unable to retrieve campus analytics.'}</span>
      </div>
    );
  }

  // Format data for Recharts
  const deptData = data.departmentMetrics.map((d) => ({
    name: `${d.code} (${d.name})`,
    students: d.studentCount,
    faculty: d.facultyCount,
    courses: d.courseCount,
  }));

  const capacityData = data.courseCapacityMetrics.map((c) => ({
    name: c.code,
    title: c.title,
    Enrolled: c.enrolledStudents,
    Capacity: c.capacity,
    Utilization: c.utilizationPercentage,
  }));

  const gradeChartData = Object.entries(data.gradeDistribution).map(([grade, count]) => ({
    grade,
    count,
    fill: GRADE_COLORS[grade] || '#6366f1',
  }));

  const facultyData = data.facultyWorkload.map((f) => ({
    name: f.facultyName,
    credits: f.totalCreditsTaught,
    slots: f.weeklyScheduledSlots,
    courses: f.assignedCoursesCount,
  }));

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
        <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
          <BarChart3 className="w-7 h-7 text-indigo-600" />
          Institutional Analytics & Academic Intelligence
        </h1>
        <p className="text-sm text-slate-500 mt-1">
          Comprehensive real-time reporting across student distributions, course capacity, attendance health, and grade curves.
        </p>
      </div>

      {/* Overview Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-center gap-4">
          <div className="p-3 bg-indigo-50 text-indigo-600 rounded-xl">
            <Users className="w-6 h-6" />
          </div>
          <div>
            <div className="text-xs text-slate-500 font-medium uppercase tracking-wider">Campus Attendance</div>
            <div className="text-2xl font-bold text-slate-900">{data.attendanceHealth.campusWidePercentage}%</div>
            <div className="text-xs text-slate-400 mt-0.5">
              {data.attendanceHealth.totalPresentRecords} / {data.attendanceHealth.totalSessionRecords} attendances
            </div>
          </div>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-center gap-4">
          <div className="p-3 bg-emerald-50 text-emerald-600 rounded-xl">
            <CalendarCheck className="w-6 h-6" />
          </div>
          <div>
            <div className="text-xs text-slate-500 font-medium uppercase tracking-wider">Attendance Standing</div>
            <div className="text-2xl font-bold text-emerald-600">{data.attendanceHealth.studentsInGoodStanding}</div>
            <div className="text-xs text-slate-400 mt-0.5">
              {data.attendanceHealth.studentsWithShortageAlert} students under 75% alert
            </div>
          </div>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-center gap-4">
          <div className="p-3 bg-purple-50 text-purple-600 rounded-xl">
            <BookOpen className="w-6 h-6" />
          </div>
          <div>
            <div className="text-xs text-slate-500 font-medium uppercase tracking-wider">Active Curriculum</div>
            <div className="text-2xl font-bold text-slate-900">{data.courseCapacityMetrics.length}</div>
            <div className="text-xs text-slate-400 mt-0.5">Across {data.departmentMetrics.length} departments</div>
          </div>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-center gap-4">
          <div className="p-3 bg-amber-50 text-amber-600 rounded-xl">
            <TrendingUp className="w-6 h-6" />
          </div>
          <div>
            <div className="text-xs text-slate-500 font-medium uppercase tracking-wider">Faculty Roster</div>
            <div className="text-2xl font-bold text-slate-900">{data.facultyWorkload.length}</div>
            <div className="text-xs text-slate-400 mt-0.5">Teaching {data.facultyWorkload.reduce((a, b) => a + b.totalCreditsTaught, 0)} total credits</div>
          </div>
        </div>
      </div>

      {/* Visual Charts Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Chart 1: Department Distribution */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <PieIcon className="w-5 h-5 text-indigo-600" />
              <h3 className="font-bold text-slate-900 text-sm">Department Student Enrollment</h3>
            </div>
            <span className="text-xs text-slate-400">Headcount</span>
          </div>

          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={deptData}
                  dataKey="students"
                  nameKey="name"
                  cx="50%"
                  cy="50%"
                  outerRadius={90}
                  innerRadius={50}
                  paddingAngle={4}
                  label={({ name, percent }: any) => `${name?.split(' ')[0]} ${(percent * 100).toFixed(0)}%`}
                >
                  {deptData.map((_, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend verticalAlign="bottom" height={36} />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Chart 2: Course Capacity vs Enrolled */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <BarChart3 className="w-5 h-5 text-emerald-600" />
              <h3 className="font-bold text-slate-900 text-sm">Course Capacity vs. Enrolled Students</h3>
            </div>
            <span className="text-xs text-slate-400">Roster Ratio</span>
          </div>

          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={capacityData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                <XAxis dataKey="name" tick={{ fontSize: 11 }} />
                <YAxis tick={{ fontSize: 11 }} />
                <Tooltip />
                <Legend />
                <Bar dataKey="Enrolled" fill="#4f46e5" radius={[4, 4, 0, 0]} />
                <Bar dataKey="Capacity" fill="#e2e8f0" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Chart 3: Campus Grade Distribution */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <TrendingUp className="w-5 h-5 text-amber-500" />
              <h3 className="font-bold text-slate-900 text-sm">Institutional Grade Curve Distribution</h3>
            </div>
            <span className="text-xs text-slate-400">Performance Count</span>
          </div>

          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={gradeChartData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                <XAxis dataKey="grade" tick={{ fontSize: 12, fontWeight: 'bold' }} />
                <YAxis tick={{ fontSize: 11 }} allowDecimals={false} />
                <Tooltip />
                <Bar dataKey="count" name="Evaluations" fill="#6366f1" radius={[6, 6, 0, 0]}>
                  {gradeChartData.map((entry, index) => (
                    <Cell key={`grade-cell-${index}`} fill={entry.fill} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Chart 4: Faculty Teaching Workload */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Users className="w-5 h-5 text-purple-600" />
              <h3 className="font-bold text-slate-900 text-sm">Faculty Instructional Load & Weekly Sessions</h3>
            </div>
            <span className="text-xs text-slate-400">Credit Load</span>
          </div>

          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={facultyData} layout="vertical" margin={{ top: 10, right: 10, left: 20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" horizontal={false} stroke="#f1f5f9" />
                <XAxis type="number" tick={{ fontSize: 11 }} />
                <YAxis dataKey="name" type="category" tick={{ fontSize: 11 }} width={90} />
                <Tooltip />
                <Legend />
                <Bar dataKey="credits" name="Total Credits" fill="#8b5cf6" radius={[0, 4, 4, 0]} />
                <Bar dataKey="slots" name="Weekly Classes" fill="#06b6d4" radius={[0, 4, 4, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>
    </div>
  );
};

