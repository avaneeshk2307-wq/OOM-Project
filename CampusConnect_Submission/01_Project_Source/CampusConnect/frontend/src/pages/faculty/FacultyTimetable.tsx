import React, { useEffect, useState } from 'react';
import { timetableApi } from '../../api/timetable';
import { TimetableEntry } from '../../types/timetable';
import { TimetableGrid } from '../../components/widgets/TimetableGrid';
import { Calendar, BookOpen, MapPin, AlertCircle } from 'lucide-react';

export const FacultyTimetable: React.FC = () => {
  const [entries, setEntries] = useState<TimetableEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchTimetable = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await timetableApi.getMyTimetable();
        setEntries(data);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to fetch teaching timetable');
      } finally {
        setLoading(false);
      }
    };

    fetchTimetable();
  }, []);

  const totalClasses = entries.length;
  const uniqueRooms = new Set(entries.map((e) => e.roomNumber)).size;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <Calendar className="w-7 h-7 text-indigo-600" />
            Faculty Teaching Timetable
          </h1>
          <p className="text-sm text-slate-500 mt-1">
            Weekly scheduled lecture and lab sessions assigned to your instructional roster.
          </p>
        </div>

        <div className="flex items-center gap-4">
          <div className="px-4 py-2 bg-indigo-50 border border-indigo-100 rounded-xl flex items-center gap-3">
            <div className="p-2 bg-indigo-600 rounded-lg text-white">
              <BookOpen className="w-4 h-4" />
            </div>
            <div>
              <div className="text-xs text-indigo-600 font-medium">Teaching Load</div>
              <div className="text-base font-bold text-indigo-950">{totalClasses} Sessions/Week</div>
            </div>
          </div>

          <div className="px-4 py-2 bg-purple-50 border border-purple-100 rounded-xl flex items-center gap-3">
            <div className="p-2 bg-purple-600 rounded-lg text-white">
              <MapPin className="w-4 h-4" />
            </div>
            <div>
              <div className="text-xs text-purple-600 font-medium">Assigned Rooms</div>
              <div className="text-base font-bold text-purple-950">{uniqueRooms} Facilities</div>
            </div>
          </div>
        </div>
      </div>

      {error && (
        <div className="p-4 bg-rose-50 border border-rose-200 rounded-xl flex items-center gap-3 text-rose-800 text-sm">
          <AlertCircle className="w-5 h-5 flex-shrink-0 text-rose-600" />
          <div className="font-medium">{error}</div>
        </div>
      )}

      {/* Grid */}
      <TimetableGrid entries={entries} isLoading={loading} />
    </div>
  );
};

