import React, { useState, useEffect } from 'react';
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
  ShieldCheck,
  Briefcase,
  BookOpen,
  Eye,
  EyeOff
} from 'lucide-react';

type PortalRole = 'ADMIN' | 'FACULTY' | 'STUDENT';

export const LoginPage: React.FC = () => {
  const [selectedRole, setSelectedRole] = useState<PortalRole>('ADMIN');
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('admin123');
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const { user, token, loading: authLoading, login } = useAuth();
  const navigate = useNavigate();

  // If already authenticated, redirect to appropriate role dashboard
  useEffect(() => {
    if (!authLoading && token && user) {
      switch (user.role) {
        case 'ROLE_ADMIN':
          navigate('/admin', { replace: true });
          break;
        case 'ROLE_FACULTY':
          navigate('/faculty', { replace: true });
          break;
        case 'ROLE_STUDENT':
          navigate('/student', { replace: true });
          break;
        default:
          navigate('/admin', { replace: true });
      }
    }
  }, [user, token, authLoading, navigate]);

  // Handle switching between the three roles
  const handleRoleSelect = (role: PortalRole) => {
    setSelectedRole(role);
    setErrorMessage(null);
    if (role === 'ADMIN') {
      setUsername('admin');
      setPassword('admin123');
    } else if (role === 'FACULTY') {
      setUsername('faculty');
      setPassword('faculty123');
    } else {
      setUsername('student');
      setPassword('student123');
    }
  };

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

      // Redirect to appropriate role dashboard after successful authentication
      switch (data.role) {
        case 'ROLE_ADMIN':
          navigate('/admin', { replace: true });
          break;
        case 'ROLE_FACULTY':
          navigate('/faculty', { replace: true });
          break;
        case 'ROLE_STUDENT':
          navigate('/student', { replace: true });
          break;
        default:
          navigate('/', { replace: true });
      }
    } catch (err: any) {
      if (err.response?.status === 401) {
        setErrorMessage('Invalid username or password. Please verify your credentials.');
      } else if (!err.response) {
        setErrorMessage('Unable to connect to the campus authentication service. Please ensure the server is running.');
      } else {
        setErrorMessage(err.response?.data?.message || 'Authentication error. Please contact campus IT support.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  if (authLoading) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center">
        <div className="w-8 h-8 border-3 border-indigo-600 border-t-transparent rounded-full animate-spin"></div>
      </div>
    );
  }

  const roleLabel = selectedRole === 'ADMIN' ? 'Admin' : selectedRole === 'FACULTY' ? 'Faculty' : 'Student';

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8 font-sans">
      {/* Brand Header matching Screenshot 2 */}
      <div className="sm:mx-auto sm:w-full sm:max-w-md text-center">
        <div className="inline-flex p-3 bg-brand-navy rounded-2xl shadow-lg border border-slate-700 mb-3">
          <GraduationCap className="w-10 h-10 text-indigo-400" />
        </div>
        <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">
          CampusConnect
        </h1>
        <p className="mt-1 text-sm text-slate-600 font-medium">
          University Campus Management Portal
        </p>
      </div>

      {/* Centered Login Card */}
      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white py-8 px-6 shadow-xl shadow-slate-200/50 rounded-2xl border border-slate-200/80 sm:px-10">
          {errorMessage && (
            <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-xl flex items-start gap-3 text-red-700 text-sm animate-fadeIn">
              <AlertCircle className="w-5 h-5 shrink-0 mt-0.5 text-red-600" />
              <span>{errorMessage}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <div className="flex items-center justify-between mb-1.5">
                <label className="block text-xs font-semibold uppercase tracking-wider text-slate-700">
                  Username
                </label>
                <span className={`text-[11px] font-medium px-2 py-0.5 rounded border ${
                  selectedRole === 'ADMIN'
                    ? 'text-indigo-700 bg-indigo-50 border-indigo-200'
                    : selectedRole === 'FACULTY'
                    ? 'text-blue-700 bg-blue-50 border-blue-200'
                    : 'text-emerald-700 bg-emerald-50 border-emerald-200'
                }`}>
                  {roleLabel} Login
                </span>
              </div>
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
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  required
                  className="block w-full pl-10 pr-10 py-2.5 bg-slate-50 border border-slate-200 rounded-lg text-slate-900 placeholder-slate-400 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-600 focus:border-transparent transition-all"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute inset-y-0 right-0 pr-3.5 flex items-center text-slate-400 hover:text-slate-600 focus:outline-none cursor-pointer"
                  title={showPassword ? 'Hide password' : 'Show password'}
                  aria-label={showPassword ? 'Hide password' : 'Show password'}
                >
                  {showPassword ? (
                    <EyeOff className="w-4 h-4" />
                  ) : (
                    <Eye className="w-4 h-4" />
                  )}
                </button>
              </div>
            </div>

            <button
              type="submit"
              disabled={isLoading}
              className="w-full flex items-center justify-center gap-2 py-2.5 px-4 rounded-lg text-sm font-semibold text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 transition-colors shadow-sm disabled:opacity-60 cursor-pointer"
            >
              {isLoading ? (
                <>
                  <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                  <span>Authenticating...</span>
                </>
              ) : (
                <>
                  <span>Sign In as {roleLabel}</span>
                  <ArrowRight className="w-4 h-4" />
                </>
              )}
            </button>
          </form>

          {/* Role Selection Section matching Screenshot 2 Layout */}
          <div className="mt-8 pt-6 border-t border-slate-100">
            <span className="block text-xs font-semibold text-slate-400 uppercase tracking-wider mb-3">
              Select Portal Role
            </span>
            <div className="grid grid-cols-3 gap-2">
              <button
                type="button"
                onClick={() => handleRoleSelect('ADMIN')}
                className={`flex items-center justify-center gap-1.5 p-2 rounded-lg text-xs font-semibold transition-all cursor-pointer ${
                  selectedRole === 'ADMIN'
                    ? 'bg-indigo-50 border-2 border-indigo-600 text-indigo-700 shadow-xs'
                    : 'bg-slate-50 hover:bg-indigo-50/60 border border-slate-200 text-slate-600 hover:text-indigo-600'
                }`}
                title="Select Admin Role"
              >
                <ShieldCheck className={`w-3.5 h-3.5 ${selectedRole === 'ADMIN' ? 'text-indigo-600' : 'text-slate-400'}`} />
                <span>Admin Login</span>
              </button>

              <button
                type="button"
                onClick={() => handleRoleSelect('FACULTY')}
                className={`flex items-center justify-center gap-1.5 p-2 rounded-lg text-xs font-semibold transition-all cursor-pointer ${
                  selectedRole === 'FACULTY'
                    ? 'bg-blue-50 border-2 border-blue-600 text-blue-700 shadow-xs'
                    : 'bg-slate-50 hover:bg-blue-50/60 border border-slate-200 text-slate-600 hover:text-blue-600'
                }`}
                title="Select Faculty Role"
              >
                <Briefcase className={`w-3.5 h-3.5 ${selectedRole === 'FACULTY' ? 'text-blue-600' : 'text-slate-400'}`} />
                <span>Faculty Login</span>
              </button>

              <button
                type="button"
                onClick={() => handleRoleSelect('STUDENT')}
                className={`flex items-center justify-center gap-1.5 p-2 rounded-lg text-xs font-semibold transition-all cursor-pointer ${
                  selectedRole === 'STUDENT'
                    ? 'bg-emerald-50 border-2 border-emerald-600 text-emerald-700 shadow-xs'
                    : 'bg-slate-50 hover:bg-emerald-50/60 border border-slate-200 text-slate-600 hover:text-emerald-600'
                }`}
                title="Select Student Role"
              >
                <BookOpen className={`w-3.5 h-3.5 ${selectedRole === 'STUDENT' ? 'text-emerald-600' : 'text-slate-400'}`} />
                <span>Student Login</span>
              </button>
            </div>
          </div>
        </div>

        {/* Footer */}
        <p className="mt-6 text-center text-xs text-slate-500 font-medium">
          CampusConnect &bull; University Campus Management Portal &bull; Authorized Access
        </p>
      </div>
    </div>
  );
};
