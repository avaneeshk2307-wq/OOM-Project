import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { apiClient } from '../../api/client';
import { LoginResponse } from '../../types/auth';
import { 
  GraduationCap, 
  Lock, 
  User as UserIcon, 
  AlertCircle, 
  ArrowRight,
  ShieldAlert,
  BookOpen,
  Briefcase
} from 'lucide-react';

export const LoginPage: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username.trim() || !password) {
      setErrorMessage('Please provide both username and password.');
      return;
    }

    setIsLoading(true);
    setErrorMessage(null);

    try {
      const response = await apiClient.post<LoginResponse>('/auth/login', {
        username: username.trim(),
        password,
      });

      const data = response.data;
      login(data.token, {
        userId: data.userId,
        personId: data.personId,
        username: data.username,
        fullName: data.fullName,
        email: data.email,
        role: data.role,
      });

      // Redirect according to authenticated role
      switch (data.role) {
        case 'ROLE_ADMIN':
          navigate('/admin');
          break;
        case 'ROLE_FACULTY':
          navigate('/faculty');
          break;
        case 'ROLE_STUDENT':
          navigate('/student');
          break;
        default:
          navigate('/');
      }
    } catch (err: any) {
      if (err.response?.status === 401) {
        setErrorMessage('Invalid username or password. Please verify your credentials.');
      } else {
        setErrorMessage(err.response?.data?.message || 'Authentication service unreachable.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleQuickFill = (u: string, p: string) => {
    setUsername(u);
    setPassword(p);
    setErrorMessage(null);
  };

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md text-center">
        <div className="inline-flex p-3 bg-brand-navy rounded-2xl shadow-lg border border-slate-700 mb-3">
          <GraduationCap className="w-10 h-10 text-indigo-400" />
        </div>
        <h2 className="text-3xl font-extrabold text-slate-900 tracking-tight">
          CampusConnect
        </h2>
        <p className="mt-1 text-sm text-slate-600">
          Smart Campus Management System &bull; University Portal
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white py-8 px-6 shadow-xl shadow-slate-200/50 rounded-2xl border border-slate-200/80 sm:px-10">
          {errorMessage && (
            <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-xl flex items-start gap-3 text-red-700 text-sm">
              <AlertCircle className="w-5 h-5 shrink-0 mt-0.5 text-red-600" />
              <span>{errorMessage}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-slate-700 mb-1.5">
                Username
              </label>
              <div className="relative rounded-lg shadow-sm">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                  <UserIcon className="w-4 h-4" />
                </div>
                <input
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="e.g. admin, faculty, student"
                  required
                  className="block w-full pl-10 pr-3 py-2.5 bg-slate-50 border border-slate-200 rounded-lg text-slate-900 placeholder-slate-400 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-600 focus:border-transparent transition-all"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider text-slate-700 mb-1.5">
                Password
              </label>
              <div className="relative rounded-lg shadow-sm">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                  <Lock className="w-4 h-4" />
                </div>
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  required
                  className="block w-full pl-10 pr-3 py-2.5 bg-slate-50 border border-slate-200 rounded-lg text-slate-900 placeholder-slate-400 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-600 focus:border-transparent transition-all"
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={isLoading}
              className="w-full flex items-center justify-center gap-2 py-2.5 px-4 rounded-lg text-sm font-semibold text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 transition-colors shadow-sm disabled:opacity-60"
            >
              {isLoading ? (
                <>
                  <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                  <span>Authenticating...</span>
                </>
              ) : (
                <>
                  <span>Sign In</span>
                  <ArrowRight className="w-4 h-4" />
                </>
              )}
            </button>
          </form>

          {/* Quick Demo Pre-fill Pills */}
          <div className="mt-8 pt-6 border-t border-slate-100">
            <span className="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-3">
              Demo Credentials (OOM Viva)
            </span>
            <div className="grid grid-cols-3 gap-2">
              <button
                type="button"
                onClick={() => handleQuickFill('admin', 'admin123')}
                className="flex items-center justify-center gap-1.5 p-2 bg-slate-50 hover:bg-indigo-50 border border-slate-200 hover:border-indigo-200 rounded-lg text-xs font-medium text-slate-700 hover:text-indigo-700 transition-colors"
              >
                <ShieldAlert className="w-3.5 h-3.5 text-indigo-600" />
                Admin
              </button>
              <button
                type="button"
                onClick={() => handleQuickFill('faculty', 'faculty123')}
                className="flex items-center justify-center gap-1.5 p-2 bg-slate-50 hover:bg-blue-50 border border-slate-200 hover:border-blue-200 rounded-lg text-xs font-medium text-slate-700 hover:text-blue-700 transition-colors"
              >
                <Briefcase className="w-3.5 h-3.5 text-blue-600" />
                Faculty
              </button>
              <button
                type="button"
                onClick={() => handleQuickFill('student', 'student123')}
                className="flex items-center justify-center gap-1.5 p-2 bg-slate-50 hover:bg-emerald-50 border border-slate-200 hover:border-emerald-200 rounded-lg text-xs font-medium text-slate-700 hover:text-emerald-700 transition-colors"
              >
                <BookOpen className="w-3.5 h-3.5 text-emerald-600" />
                Student
              </button>
            </div>
          </div>
        </div>

        <p className="mt-6 text-center text-xs text-slate-500">
          Stateless JWT &bull; BCrypt Hashed Passwords &bull; Role-Based Access Control
        </p>
      </div>
    </div>
  );
};
