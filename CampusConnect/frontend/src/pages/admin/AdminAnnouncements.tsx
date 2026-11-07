import React, { useState, useEffect } from 'react';
import { apiClient } from '../../api/client';
import { Announcement } from '../../types/announcement';
import { 
  Bell, 
  PlusCircle, 
  Trash2, 
  AlertCircle, 
  CheckCircle, 
  Users, 
  X, 
  Calendar,
  Filter
} from 'lucide-react';

export const AdminAnnouncements: React.FC = () => {
  const [announcements, setAnnouncements] = useState<Announcement[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const [showModal, setShowModal] = useState<boolean>(false);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [targetRole, setTargetRole] = useState<string>('ALL');
  const [submitting, setSubmitting] = useState<boolean>(false);
  const [filterRole, setFilterRole] = useState<string>('ALL');

  const fetchAnnouncements = async () => {
    try {
      setLoading(true);
      const res = await apiClient.get<Announcement[]>('/announcements/all');
      setAnnouncements(res.data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to fetch announcements.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAnnouncements();
  }, []);

  const handleCreateAnnouncement = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setSubmitting(true);
      setError(null);
      const payload = {
        title: title.trim(),
        content: content.trim(),
        targetRole: targetRole === 'ALL' ? null : targetRole,
        courseId: null
      };

      await apiClient.post('/announcements', payload);
      setSuccess('Announcement broadcasted successfully!');
      setShowModal(false);
      setTitle('');
      setContent('');
      setTargetRole('ALL');
      fetchAnnouncements();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create announcement.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this announcement?')) return;
    try {
      setError(null);
      await apiClient.delete(`/announcements/${id}`);
      setSuccess('Announcement deleted successfully.');
      setAnnouncements((prev) => prev.filter((a) => a.id !== id));
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to delete announcement.');
    }
  };

  const filtered = announcements.filter((a) => {
    if (filterRole === 'ALL') return true;
    if (filterRole === 'CAMPUS_WIDE') return a.targetRole === null;
    return a.targetRole === filterRole;
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Broadcast Announcements</h1>
          <p className="text-sm text-gray-500 mt-1">
            Publish campus-wide bulletins, academic alerts, and role-specific announcements.
          </p>
        </div>
        <button
          onClick={() => setShowModal(true)}
          className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg text-sm font-semibold shadow-sm transition-colors"
        >
          <PlusCircle className="w-4 h-4" />
          Create Announcement
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

      {/* Filter */}
      <div className="bg-white p-4 rounded-xl border border-gray-200 shadow-sm flex items-center justify-between gap-4">
        <div className="flex items-center gap-2">
          <Filter className="w-4 h-4 text-gray-400" />
          <span className="text-sm font-medium text-gray-700">Filter Audience:</span>
          <select
            value={filterRole}
            onChange={(e) => setFilterRole(e.target.value)}
            className="text-sm border border-gray-300 rounded-lg px-3 py-1.5 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
          >
            <option value="ALL">All Audiences</option>
            <option value="CAMPUS_WIDE">Campus-Wide Broadcasts</option>
            <option value="ROLE_STUDENT">Students Only</option>
            <option value="ROLE_FACULTY">Faculty Only</option>
          </select>
        </div>
        <span className="text-xs text-gray-400">
          Showing {filtered.length} announcement(s)
        </span>
      </div>

      {/* Announcement Cards */}
      {loading ? (
        <div className="p-12 text-center text-gray-500">Loading announcements...</div>
      ) : filtered.length === 0 ? (
        <div className="bg-white p-12 rounded-xl border border-gray-200 text-center text-gray-400">
          <Bell className="w-12 h-12 mx-auto mb-3 opacity-30" />
          <p className="text-base font-semibold">No announcements found matching filter.</p>
        </div>
      ) : (
        <div className="space-y-4">
          {filtered.map((item) => (
            <div key={item.id} className="bg-white p-6 rounded-xl border border-gray-200 shadow-sm hover:border-gray-300 transition-colors">
              <div className="flex items-start justify-between gap-4">
                <div className="space-y-1">
                  <div className="flex items-center gap-2">
                    <h3 className="text-lg font-bold text-gray-900">{item.title}</h3>
                    <span className={`px-2.5 py-0.5 rounded-full text-[11px] font-bold uppercase tracking-wider ${
                      item.targetRole === 'ROLE_STUDENT'
                        ? 'bg-blue-100 text-blue-800'
                        : item.targetRole === 'ROLE_FACULTY'
                        ? 'bg-purple-100 text-purple-800'
                        : 'bg-emerald-100 text-emerald-800'
                    }`}>
                      {item.targetRole === 'ROLE_STUDENT'
                        ? 'Students'
                        : item.targetRole === 'ROLE_FACULTY'
                        ? 'Faculty'
                        : 'Campus Wide'}
                    </span>
                  </div>
                  <div className="flex items-center gap-4 text-xs text-gray-400">
                    <span className="flex items-center gap-1">
                      <Users className="w-3.5 h-3.5" />
                      Posted by {item.authorName}
                    </span>
                    <span className="flex items-center gap-1">
                      <Calendar className="w-3.5 h-3.5" />
                      {new Date(item.createdAt).toLocaleDateString(undefined, {
                        month: 'short',
                        day: 'numeric',
                        year: 'numeric'
                      })}
                    </span>
                  </div>
                </div>

                <button
                  onClick={() => handleDelete(item.id)}
                  className="text-gray-400 hover:text-rose-600 p-1.5 rounded-lg hover:bg-rose-50 transition-colors"
                  title="Delete Announcement"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>

              <p className="text-sm text-gray-700 mt-3 whitespace-pre-wrap leading-relaxed">
                {item.content}
              </p>
            </div>
          ))}
        </div>
      )}

      {/* Create Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="bg-white rounded-2xl shadow-xl max-w-lg w-full overflow-hidden border border-gray-200">
            <div className="p-6 border-b border-gray-100 flex items-center justify-between">
              <h3 className="text-lg font-bold text-gray-900 flex items-center gap-2">
                <Bell className="w-5 h-5 text-indigo-600" />
                New Announcement
              </h3>
              <button
                onClick={() => setShowModal(false)}
                className="text-gray-400 hover:text-gray-600 p-1 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateAnnouncement} className="p-6 space-y-4">
              <div>
                <label className="block text-xs font-medium text-gray-700 mb-1">Title</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. End-Semester Examination Schedule Published"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-gray-700 mb-1">Target Audience</label>
                <select
                  value={targetRole}
                  onChange={(e) => setTargetRole(e.target.value)}
                  className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                >
                  <option value="ALL">Entire Campus (Students, Faculty, Staff)</option>
                  <option value="ROLE_STUDENT">Students Only</option>
                  <option value="ROLE_FACULTY">Faculty Only</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-medium text-gray-700 mb-1">Content / Body</label>
                <textarea
                  required
                  rows={4}
                  placeholder="Enter detailed notice content here..."
                  value={content}
                  onChange={(e) => setContent(e.target.value)}
                  className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                />
              </div>

              <div className="flex items-center justify-end gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="px-4 py-2 border border-gray-300 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={submitting}
                  className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg text-sm font-semibold shadow-sm transition-colors"
                >
                  {submitting ? 'Publishing...' : 'Publish Announcement'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
