import React from 'react';
import { useNavigate } from 'react-router-dom';
import { ShieldX, ArrowLeft } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

export const UnauthorizedPage: React.FC = () => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
      <div className="max-w-md w-full bg-white rounded-2xl border border-slate-200 shadow-xl p-8 text-center space-y-6">
        <div className="w-16 h-16 bg-red-50 text-red-600 rounded-2xl flex items-center justify-center mx-auto border border-red-100">
          <ShieldX className="w-8 h-8" />
        </div>
        <div className="space-y-2">
          <h1 className="text-2xl font-bold text-slate-900">Access Denied</h1>
          <p className="text-sm text-slate-600">
            Your authenticated account (<span className="font-semibold text-slate-800">{user?.username}</span> with role{' '}
            <code className="text-xs bg-slate-100 px-1 py-0.5 rounded text-indigo-700 font-mono">{user?.role}</code>) does not have sufficient privileges to access this area.
          </p>
        </div>

        <div className="flex flex-col sm:flex-row gap-3 pt-2">
          <button
            onClick={() => navigate(-1)}
            className="flex-1 inline-flex items-center justify-center gap-2 px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg text-sm font-medium transition-colors"
          >
            <ArrowLeft className="w-4 h-4" />
            Go Back
          </button>
          <button
            onClick={() => {
              logout();
              navigate('/login');
            }}
            className="flex-1 inline-flex items-center justify-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg text-sm font-medium transition-colors shadow-sm"
          >
            Switch Account
          </button>
        </div>
      </div>
    </div>
  );
};
