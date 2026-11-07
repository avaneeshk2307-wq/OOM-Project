import React from 'react';
import { TimetableEntry, DayOfWeek } from '../../types/timetable';
import { Clock, MapPin, User, Trash2 } from 'lucide-react';

interface TimetableGridProps {
  entries: TimetableEntry[];
  onDelete?: (id: number) => void;
  isLoading?: boolean;
}

const DAYS: { key: DayOfWeek; label: string }[] = [
  { key: 'MONDAY', label: 'Monday' },
  { key: 'TUESDAY', label: 'Tuesday' },
  { key: 'WEDNESDAY', label: 'Wednesday' },
  { key: 'THURSDAY', label: 'Thursday' },
  { key: 'FRIDAY', label: 'Friday' },
  { key: 'SATURDAY', label: 'Saturday' },
];

export const TimetableGrid: React.FC<TimetableGridProps> = ({ entries, onDelete, isLoading }) => {
  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-16">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  // Filter out days that have no classes if it's Saturday, or always show Mon-Fri
  const activeDays = DAYS.filter((d) => d.key !== 'SATURDAY' || entries.some((e) => e.dayOfWeek === 'SATURDAY'));

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
      {activeDays.map(({ key, label }) => {
        const dayEntries = entries
          .filter((e) => e.dayOfWeek === key)
          .sort((a, b) => a.startTime.localeCompare(b.startTime));

        return (
          <div key={key} className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden flex flex-col">
            <div className="bg-slate-50 px-4 py-3 border-b border-slate-200 flex items-center justify-between">
              <h3 className="font-semibold text-slate-800 text-sm">{label}</h3>
              <span className="text-xs px-2 py-0.5 rounded-full bg-indigo-50 text-indigo-700 font-medium">
                {dayEntries.length} {dayEntries.length === 1 ? 'class' : 'classes'}
              </span>
            </div>

            <div className="p-3 space-y-3 flex-1 min-h-[160px]">
              {dayEntries.length === 0 ? (
                <div className="h-full flex items-center justify-center text-xs text-slate-400 italic">
                  No classes scheduled
                </div>
              ) : (
                dayEntries.map((entry) => (
                  <div
                    key={entry.id}
                    className="p-3 rounded-lg border border-indigo-100 bg-indigo-50/50 hover:bg-indigo-50 transition-colors relative group"
                  >
                    <div className="flex items-start justify-between">
                      <div className="font-bold text-xs text-indigo-900 tracking-wide">{entry.courseCode}</div>
                      {onDelete && (
                        <button
                          onClick={() => onDelete(entry.id)}
                          className="opacity-0 group-hover:opacity-100 transition-opacity p-1 text-red-500 hover:text-red-700 rounded hover:bg-red-50"
                          title="Delete Schedule Slot"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                        </button>
                      )}
                    </div>

                    <div className="text-xs font-medium text-slate-700 mt-0.5 line-clamp-1" title={entry.courseTitle}>
                      {entry.courseTitle}
                    </div>

                    <div className="mt-2.5 space-y-1 text-[11px] text-slate-600">
                      <div className="flex items-center gap-1.5">
                        <Clock className="w-3 h-3 text-indigo-500" />
                        <span className="font-medium text-slate-800">
                          {entry.startTime} – {entry.endTime}
                        </span>
                      </div>

                      <div className="flex items-center gap-1.5">
                        <MapPin className="w-3 h-3 text-emerald-500" />
                        <span>
                          {entry.roomNumber} <span className="text-slate-400">({entry.building})</span>
                        </span>
                      </div>

                      <div className="flex items-center gap-1.5">
                        <User className="w-3 h-3 text-amber-500" />
                        <span className="truncate">{entry.facultyName}</span>
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
};
