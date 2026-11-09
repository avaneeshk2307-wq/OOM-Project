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
  Eye,
  EyeOff,
  ShieldCheck
} from 'lucide-react';

export const LoginPage: React.FC = () => {
  const [username, setUsername] = useState(() => localStorage.getItem('remembered_username') || '');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [rememberMe, setRememberMe] = useState(() => !!localStorage.getItem('remembered_username'));
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const { user, token, loading: authLoading, login } = useAuth();
  const navigate = useNavigate();

  // If user is already authenticated, redirect to their role-specific dashboard
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

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username.trim() || !password) {
      setErrorMessage('Please enter both your campus username/email and password.');
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

      // Handle "Remember me"
      if (rememberMe) {
        localStorage.setItem('remembered_username', username.trim());
      } else {
        localStorage.removeItem('remembered_username');
      }

      login(data.token, {
        userId: data.userId,
        personId: data.personId,
        username: data.username,
        fullName: data.fullName,
        email: data.email,
        role: data.role,
      });

      // Role-based redirection after successful login
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
        setErrorMessage('Invalid username or password. Please verify your campus credentials.');
      } else if (!err.response) {
        setErrorMessage('Unable to connect to the campus authentication service. Please check your network connection.');
      } else {
        setErrorMessage(err.response?.data?.message || 'Authentication error. Please contact campus IT support.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  // Brief spinner while verifying session on initial mount
  if (authLoading) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center">
        <div className="flex flex-col items-center gap-3 text-slate-500">
          <div className="w-8 h-8 border-3 border-indigo-600 border-t-transparent rounded-full animate-spin"></div>
          <span className="text-sm font-medium">Verifying campus session...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-b from-slate-50 via-slate-100 to-slate-200 flex flex-col justify-between py-10 px-4 sm:px-6 lg:px-8 font-sans">
      <div className="w-full max-w-md mx-auto my-auto">
        {/* University Brand Header */}
        <div className="text-center mb-8">
          <div className="inline-flex p-3.5 bg-slate-900 rounded-2xl shadow-xl border border-slate-800 text-indigo-400 mb-4 ring-4 ring-indigo-50">
            <GraduationCap className="w-10 h-10" />
          </div>
          <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">
            CampusConnect
          </h1>
          <p className="text-sm font-medium text-indigo-700 uppercase tracking-widest mt-1">
            University Campus Management Portal
          </p>
          <p className="text-xs text-slate-500 mt-1">
            One Platform for Your Campus
          </p>
        </div>

        {/* Centered Login Card */}
        <div className="bg-white rounded-2xl shadow-xl shadow-slate-200/60 border border-slate-200/90 overflow-hidden">
          {/* Card Header */}
          <div className="px-6 pt-7 pb-4 border-b border-slate-100 text-center sm:px-8">
            <h2 className="text-xl font-bold text-slate-900">
              Welcome Back
            </h2>
            <p className="text-xs text-slate-500 mt-1">
              Sign in to access your campus account and academic services.
            </p>
          </div>

          <div className="p-6 sm:p-8 space-y-5">
            {/* Error Notification */}
            {errorMessage && (
              <div className="p-3.5 bg-red-50 border border-red-200 rounded-xl flex items-start gap-3 text-red-700 text-xs font-medium animate-fadeIn">
                <AlertCircle className="w-4 h-4 shrink-0 mt-0.5 text-red-600" />
                <span className="leading-relaxed">{errorMessage}</span>
              </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-4">
              {/* Username Input */}
              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider text-slate-700 mb-1.5">
                  Campus Username or Email
                </label>
                <div className="relative rounded-lg shadow-xs">
                  <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                    <UserIcon className="w-4 h-4" />
                  </div>
                  <input
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    placeholder="e.g. admin or your university ID"
                    required
                    autoFocus
                    autoComplete="username"
                    className="block w-full pl-10 pr-3.5 py-2.5 bg-slate-50/70 border border-slate-200 rounded-lg text-slate-900 placeholder-slate-400 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-600 focus:bg-white focus:border-transparent transition-all"
                  />
                </div>
              </div>

              {/* Password Input with Show/Hide Toggle */}
              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider text-slate-700 mb-1.5">
                  Password
                </label>
                <div className="relative rounded-lg shadow-xs">
                  <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                    <Lock className="w-4 h-4" />
                  </div>
                  <input
                    type={showPassword ? 'text' : 'password'}
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Enter your secure password"
                    required
                    autoComplete="current-password"
                    className="block w-full pl-10 pr-10 py-2.5 bg-slate-50/70 border border-slate-200 rounded-lg text-slate-900 placeholder-slate-400 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-600 focus:bg-white focus:border-transparent transition-all"
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute inset-y-0 right-0 pr-3.5 flex items-center text-slate-400 hover:text-slate-600 focus:outline-none"
                    title={showPassword ? 'Hide password' : 'Show password'}
                  >
                    {showPassword ? (
                      <EyeOff className="w-4 h-4" />
                    ) : (
                      <Eye className="w-4 h-4" />
                    )}
                  </button>
                </div>
              </div>

              {/* Remember Me Option */}
              <div className="flex items-center justify-between text-xs pt-1">
                <label className="flex items-center gap-2 text-slate-600 cursor-pointer select-none">
                  <input
                    type="checkbox"
                    checked={rememberMe}
                    onChange={(e) => setRememberMe(e.target.checked)}
                    className="w-4 h-4 rounded border-slate-300 text-indigo-600 focus:ring-indigo-500 cursor-pointer"
                  />
                  <span>Remember username</span>
                </label>
                <span className="text-slate-400 flex items-center gap-1">
                  <ShieldCheck className="w-3.5 h-3.5 text-emerald-600" />
                  Secure Session
                </span>
              </div>

              {/* Sign In Button */}
              <button
                type="submit"
                disabled={isLoading}
                className="w-full mt-2 flex items-center justify-center gap-2 py-2.5 px-4 rounded-lg text-sm font-semibold text-white bg-indigo-600 hover:bg-indigo-700 active:bg-indigo-800 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 transition-colors shadow-md shadow-indigo-600/20 disabled:opacity-60 cursor-pointer"
              >
                {isLoading ? (
                  <>
                    <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                    <span>Signing in...</span>
                  </>
                ) : (
                  <>
                    <span>Sign In to Portal</span>
                    <ArrowRight className="w-4 h-4" />
                  </>
                )}
              </button>
            </form>
          </div>

          {/* Institutional Compliance Notice */}
          <div className="px-6 py-3.5 bg-slate-50 border-t border-slate-100 text-center text-slate-400 text-xs">
            Authorized institutional access only &bull; System activity is monitored
          </div>
        </div>

        {/* Global Portal Footer */}
        <div className="mt-8 text-center text-xs text-slate-500 space-y-1">
          <p className="font-medium text-slate-600">
            CampusConnect &bull; University Campus Management Portal
          </p>
          <p className="text-slate-400">
            &copy; {new Date().getFullYear()} CampusConnect. All rights reserved.
          </p>
        </div>
      </div>
    </div>
  );
};
