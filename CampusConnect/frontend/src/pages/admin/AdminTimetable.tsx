import React, { useEffect, useState } from 'react';
import { timetableApi } from '../../api/timetable';
import { apiClient } from '../../api/client';
import { Classroom, CreateClassroomRequest, CreateTimetableRequest, DayOfWeek, RoomType, TimetableEntry } from '../../types/timetable';
import { Course, Faculty } from '../../types/admin';
import { TimetableGrid } from '../../components/widgets/TimetableGrid';
import { Calendar, Plus, Building, AlertCircle, CheckCircle, Trash2, X } from 'lucide-react';

export const AdminTimetable: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'schedule' | 'classrooms'>('schedule');
  const [entries, setEntries] = useState<TimetableEntry[]>([]);
  const [classrooms, setClassrooms] = useState<Classroom[]>([]);
  const [courses, setCourses] = useState<Course[]>([]);
  const [faculties, setFaculties] = useState<Faculty[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  // Scheduling Modal
  const [isScheduleModalOpen, setIsScheduleModalOpen] = useState(false);
  const [selectedCourseId, setSelectedCourseId] = useState<number | ''>('');
  const [selectedClassroomId, setSelectedClassroomId] = useState<number | ''>('');
  const [selectedFacultyId, setSelectedFacultyId] = useState<number | ''>('');
  const [selectedDay, setSelectedDay] = useState<DayOfWeek>('MONDAY');
  const [startTime, setStartTime] = useState('09:00');
  const [endTime, setEndTime] = useState('10:30');
  const [scheduleSubmitting, setScheduleSubmitting] = useState(false);

  // Classroom Modal
  const [isClassroomModalOpen, setIsClassroomModalOpen] = useState(false);
  const [roomNumber, setRoomNumber] = useState('');
  const [building, setBuilding] = useState('');
  const [capacity, setCapacity] = useState<number>(50);
  const [roomType, setRoomType] = useState<RoomType>('LECTURE_HALL');
  const [classroomSubmitting, setClassroomSubmitting] = useState(false);

  const fetchData = async () => {
    try {
      setLoading(true);
      setError(null);
      const [entriesData, classroomsData, coursesRes, facultiesRes] = await Promise.all([
        timetableApi.getAllTimetables(),
        timetableApi.getClassrooms(),
        apiClient.get<Course[]>('/admin/courses'),
        apiClient.get<Faculty[]>('/admin/faculty'),
      ]);
      setEntries(entriesData);
      setClassrooms(classroomsData);
      setCourses(coursesRes.data);
      setFaculties(facultiesRes.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load timetable and classroom data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleCreateSchedule = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedCourseId || !selectedClassroomId) {
      setError('Please select both a course and a classroom');
      return;
    }

    try {
      setScheduleSubmitting(true);
      setError(null);
      setSuccess(null);

      const payload: CreateTimetableRequest = {
        courseId: Number(selectedCourseId),
        classroomId: Number(selectedClassroomId),
        facultyId: selectedFacultyId ? Number(selectedFacultyId) : undefined,
        dayOfWeek: selectedDay,
        startTime,
        endTime,
      };

      await timetableApi.createTimetableEntry(payload);
      setSuccess('Class scheduled successfully!');
      setIsScheduleModalOpen(false);
      // Reset form
      setSelectedCourseId('');
      setSelectedClassroomId('');
      setSelectedFacultyId('');
      fetchData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Schedule conflict detected or invalid input.');
    } finally {
      setScheduleSubmitting(false);
    }
  };

  const handleDeleteSchedule = async (id: number) => {
    if (!window.confirm('Are you sure you want to remove this timetable slot?')) return;
    try {
      await timetableApi.deleteTimetableEntry(id);
      setSuccess('Timetable slot removed successfully');
      fetchData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to delete schedule slot');
    }
  };

  const handleCreateClassroom = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setClassroomSubmitting(true);
      setError(null);
      setSuccess(null);

      const payload: CreateClassroomRequest = {
        roomNumber,
        building,
        capacity,
        roomType,
      };

      await timetableApi.createClassroom(payload);
      setSuccess('Classroom added successfully!');
      setIsClassroomModalOpen(false);
      setRoomNumber('');
      setBuilding('');
      setCapacity(50);
      fetchData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create classroom');
    } finally {
      setClassroomSubmitting(false);
    }
  };

  const handleDeleteClassroom = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this classroom?')) return;
    try {
      await timetableApi.deleteClassroom(id);
      setSuccess('Classroom deleted successfully');
      fetchData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Cannot delete classroom with active schedule entries');
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <Calendar className="w-7 h-7 text-indigo-600" />
            Timetable & Schedule Management
          </h1>
          <p className="text-sm text-slate-500 mt-1">
            Weekly lecture schedules, classroom allocation, and automated conflict detection.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <button
            onClick={() => setIsClassroomModalOpen(true)}
            className="inline-flex items-center gap-2 px-4 py-2 border border-slate-300 rounded-lg text-sm font-medium text-slate-700 bg-white hover:bg-slate-50 shadow-sm transition-colors"
          >
            <Building className="w-4 h-4 text-slate-500" />
            Add Classroom
          </button>
          <button
            onClick={() => setIsScheduleModalOpen(true)}
            className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg text-sm font-medium shadow-sm transition-colors"
          >
            <Plus className="w-4 h-4" />
            Schedule Lecture
          </button>
        </div>
      </div>

      {/* Alerts */}
      {error && (
        <div className="p-4 bg-rose-50 border border-rose-200 rounded-xl flex items-start gap-3 text-rose-800 text-sm animate-fadeIn">
          <AlertCircle className="w-5 h-5 flex-shrink-0 text-rose-600 mt-0.5" />
          <div className="flex-1 font-medium">{error}</div>
          <button onClick={() => setError(null)} className="text-rose-500 hover:text-rose-700">
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {success && (
        <div className="p-4 bg-emerald-50 border border-emerald-200 rounded-xl flex items-start gap-3 text-emerald-800 text-sm animate-fadeIn">
          <CheckCircle className="w-5 h-5 flex-shrink-0 text-emerald-600 mt-0.5" />
          <div className="flex-1 font-medium">{success}</div>
          <button onClick={() => setSuccess(null)} className="text-emerald-500 hover:text-emerald-700">
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {/* Tab Switcher */}
      <div className="flex border-b border-slate-200 gap-6">
        <button
          onClick={() => setActiveTab('schedule')}
          className={`pb-3 text-sm font-medium transition-colors border-b-2 flex items-center gap-2 ${
            activeTab === 'schedule'
              ? 'border-indigo-600 text-indigo-600'
              : 'border-transparent text-slate-500 hover:text-slate-700'
          }`}
        >
          <Calendar className="w-4 h-4" />
          Campus Weekly Timetable ({entries.length} Slots)
        </button>
        <button
          onClick={() => setActiveTab('classrooms')}
          className={`pb-3 text-sm font-medium transition-colors border-b-2 flex items-center gap-2 ${
            activeTab === 'classrooms'
              ? 'border-indigo-600 text-indigo-600'
              : 'border-transparent text-slate-500 hover:text-slate-700'
          }`}
        >
          <Building className="w-4 h-4" />
          Classrooms & Facilities ({classrooms.length})
        </button>
      </div>

      {/* Tab Contents */}
      {activeTab === 'schedule' ? (
        <TimetableGrid entries={entries} onDelete={handleDeleteSchedule} isLoading={loading} />
      ) : (
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
          <div className="p-4 border-b border-slate-200 bg-slate-50 flex items-center justify-between">
            <h2 className="font-semibold text-slate-800 text-sm">Registered Campus Learning Facilities</h2>
            <span className="text-xs text-slate-500">Total: {classrooms.length} facilities</span>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse text-sm">
              <thead>
                <tr className="bg-slate-50/50 text-slate-600 border-b border-slate-200 font-medium">
                  <th className="py-3 px-4">Room Number</th>
                  <th className="py-3 px-4">Building</th>
                  <th className="py-3 px-4">Facility Type</th>
                  <th className="py-3 px-4">Student Capacity</th>
                  <th className="py-3 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {classrooms.map((room) => (
                  <tr key={room.id} className="hover:bg-slate-50/80 transition-colors">
                    <td className="py-3 px-4 font-semibold text-slate-900">{room.roomNumber}</td>
                    <td className="py-3 px-4 text-slate-600">{room.building}</td>
                    <td className="py-3 px-4">
                      <span className="px-2 py-0.5 rounded-full text-xs font-medium bg-slate-100 text-slate-700">
                        {room.roomType.replace('_', ' ')}
                      </span>
                    </td>
                    <td className="py-3 px-4 text-slate-600">
                      <span className="font-medium text-slate-900">{room.capacity}</span> seats
                    </td>
                    <td className="py-3 px-4 text-right">
                      <button
                        onClick={() => handleDeleteClassroom(room.id)}
                        className="text-red-500 hover:text-red-700 p-1 rounded hover:bg-red-50 transition-colors"
                        title="Delete Facility"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Schedule Lecture Modal */}
      {isScheduleModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="bg-white rounded-2xl shadow-xl border border-slate-200 w-full max-w-lg overflow-hidden animate-scaleIn">
            <div className="px-6 py-4 border-b border-slate-200 flex items-center justify-between bg-slate-50">
              <h3 className="font-semibold text-slate-800 text-base">Schedule New Lecture Session</h3>
              <button
                onClick={() => setIsScheduleModalOpen(false)}
                className="text-slate-400 hover:text-slate-600"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateSchedule} className="p-6 space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Course *</label>
                <select
                  value={selectedCourseId}
                  onChange={(e) => setSelectedCourseId(e.target.value ? Number(e.target.value) : '')}
                  required
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                >
                  <option value="">Select a Course</option>
                  {courses.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.code} – {c.title} (Cap: {c.capacity})
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Classroom / Facility *</label>
                <select
                  value={selectedClassroomId}
                  onChange={(e) => setSelectedClassroomId(e.target.value ? Number(e.target.value) : '')}
                  required
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                >
                  <option value="">Select a Facility</option>
                  {classrooms.map((room) => (
                    <option key={room.id} value={room.id}>
                      {room.roomNumber} ({room.building}) – Capacity: {room.capacity}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Instructor</label>
                <select
                  value={selectedFacultyId}
                  onChange={(e) => setSelectedFacultyId(e.target.value ? Number(e.target.value) : '')}
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                >
                  <option value="">Use Course Assigned Instructor</option>
                  {faculties.map((f) => (
                    <option key={f.id} value={f.id}>
                      {f.name} ({f.employeeCode} - {f.designation})
                    </option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Day *</label>
                  <select
                    value={selectedDay}
                    onChange={(e) => setSelectedDay(e.target.value as DayOfWeek)}
                    className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                  >
                    <option value="MONDAY">Monday</option>
                    <option value="TUESDAY">Tuesday</option>
                    <option value="WEDNESDAY">Wednesday</option>
                    <option value="THURSDAY">Thursday</option>
                    <option value="FRIDAY">Friday</option>
                    <option value="SATURDAY">Saturday</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Start Time *</label>
                  <input
                    type="time"
                    value={startTime}
                    onChange={(e) => setStartTime(e.target.value)}
                    required
                    className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">End Time *</label>
                  <input
                    type="time"
                    value={endTime}
                    onChange={(e) => setEndTime(e.target.value)}
                    required
                    className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                  />
                </div>
              </div>

              <div className="pt-4 flex justify-end gap-3 border-t border-slate-200">
                <button
                  type="button"
                  onClick={() => setIsScheduleModalOpen(false)}
                  className="px-4 py-2 border border-slate-300 rounded-lg text-sm font-medium text-slate-700 hover:bg-slate-50 transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={scheduleSubmitting}
                  className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 text-white rounded-lg text-sm font-medium shadow-sm transition-colors"
                >
                  {scheduleSubmitting ? 'Checking Conflicts...' : 'Confirm Schedule'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Add Classroom Modal */}
      {isClassroomModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="bg-white rounded-2xl shadow-xl border border-slate-200 w-full max-w-md overflow-hidden animate-scaleIn">
            <div className="px-6 py-4 border-b border-slate-200 flex items-center justify-between bg-slate-50">
              <h3 className="font-semibold text-slate-800 text-base">Add Campus Facility</h3>
              <button
                onClick={() => setIsClassroomModalOpen(false)}
                className="text-slate-400 hover:text-slate-600"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateClassroom} className="p-6 space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Room Code / Number *</label>
                <input
                  type="text"
                  placeholder="e.g. LH-205"
                  value={roomNumber}
                  onChange={(e) => setRoomNumber(e.target.value)}
                  required
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Building / Complex *</label>
                <input
                  type="text"
                  placeholder="e.g. Turing Science Building"
                  value={building}
                  onChange={(e) => setBuilding(e.target.value)}
                  required
                  className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Capacity *</label>
                  <input
                    type="number"
                    min="1"
                    value={capacity}
                    onChange={(e) => setCapacity(Number(e.target.value))}
                    required
                    className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Room Type *</label>
                  <select
                    value={roomType}
                    onChange={(e) => setRoomType(e.target.value as RoomType)}
                    className="w-full px-3 py-2 border border-slate-300 rounded-lg text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                  >
                    <option value="LECTURE_HALL">Lecture Hall</option>
                    <option value="LABORATORY">Laboratory</option>
                    <option value="SEMINAR_ROOM">Seminar Room</option>
                    <option value="AUDITORIUM">Auditorium</option>
                  </select>
                </div>
              </div>

              <div className="pt-4 flex justify-end gap-3 border-t border-slate-200">
                <button
                  type="button"
                  onClick={() => setIsClassroomModalOpen(false)}
                  className="px-4 py-2 border border-slate-300 rounded-lg text-sm font-medium text-slate-700 hover:bg-slate-50 transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={classroomSubmitting}
                  className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 text-white rounded-lg text-sm font-medium shadow-sm transition-colors"
                >
                  {classroomSubmitting ? 'Saving...' : 'Add Facility'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
