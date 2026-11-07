import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { apiClient } from '../api/client';
import { 
  GraduationCap, 
  Server, 
  CheckCircle2, 
  AlertCircle, 
  Layers, 
  ShieldCheck, 
  Cpu, 
  ArrowRight,
  LogIn
} from 'lucide-react';

interface HealthData {
  status: string;
  service: string;
  version: string;
  timestamp: string;
  architecture: string;
}

export const LandingPage: React.FC = () => {
  const [health, setHealth] = useState<HealthData | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    apiClient.get<HealthData>('/health')
      .then((res) => {
        setHealth(res.data);
        setError(null);
      })
      .catch((err) => {
        setError(err.message || 'Could not connect to backend server');
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900 flex flex-col font-sans">
      {/* Top Navigation */}
      <header className="bg-brand-navy text-white shadow-md border-b border-slate-800">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-indigo-600 rounded-lg shadow-inner">
              <GraduationCap className="w-6 h-6 text-white" />
            </div>
            <div>
              <span className="font-bold text-xl tracking-tight text-white">CampusConnect</span>
              <span className="hidden sm:inline-block ml-2 text-xs bg-indigo-500/30 text-indigo-200 px-2 py-0.5 rounded border border-indigo-400/30">
                OOM 2026
              </span>
            </div>
          </div>
          <div className="flex items-center gap-4">
            <Link
              to="/login"
              className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg text-sm font-semibold transition-colors shadow-sm"
            >
              <LogIn className="w-4 h-4" />
              <span>Portal Login</span>
            </Link>
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-10">
        <div className="bg-white rounded-2xl shadow-sm border border-slate-200/80 p-8 sm:p-10">
          <div className="max-w-3xl space-y-4">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full text-xs font-semibold bg-indigo-50 text-indigo-700 border border-indigo-100">
              <Layers className="w-3.5 h-3.5" />
              Layered Modular Monolithic Architecture
            </div>
            <h1 className="text-3xl sm:text-4xl font-extrabold text-slate-900 tracking-tight leading-tight">
              Smart Campus Management System
            </h1>
            <p className="text-base sm:text-lg text-slate-600 leading-relaxed">
              CampusConnect is a university-grade management platform built with Spring Boot 3, React TypeScript,
              and PostgreSQL, implementing Object-Oriented Analysis & Design principles, GoF design patterns,
              and strict role-based access control.
            </p>
            <div className="pt-2">
              <Link
                to="/login"
                className="inline-flex items-center gap-2 px-5 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-sm font-semibold transition-all shadow-md shadow-indigo-600/20"
              >
                <span>Launch Portal Authentication</span>
                <ArrowRight className="w-4 h-4" />
              </Link>
            </div>
          </div>

          {/* System Health Integration Widget */}
          <div className="mt-8 pt-8 border-t border-slate-100">
            <h2 className="text-sm font-semibold uppercase tracking-wider text-slate-500 mb-4 flex items-center gap-2">
              <Server className="w-4 h-4 text-indigo-600" />
              Backend Connection & Health Status
            </h2>
            {loading ? (
              <div className="p-4 bg-slate-50 rounded-xl border border-slate-200 flex items-center gap-3 text-slate-600">
                <div className="w-4 h-4 border-2 border-indigo-600 border-t-transparent rounded-full animate-spin"></div>
                <span className="text-sm">Connecting to Spring Boot REST API (`/api/v1/health`)...</span>
              </div>
            ) : error ? (
              <div className="p-4 bg-amber-50 rounded-xl border border-amber-200 flex items-start gap-3 text-amber-800">
                <AlertCircle className="w-5 h-5 text-amber-600 shrink-0 mt-0.5" />
                <div className="text-sm">
                  <p className="font-semibold">Backend Offline or Starting Up</p>
                  <p className="text-amber-700 mt-0.5 text-xs">Run <code className="bg-amber-100 px-1 py-0.5 rounded text-amber-900 font-mono">.\mvnw.cmd spring-boot:run</code> inside <code className="font-mono">CampusConnect/backend</code>.</p>
                </div>
              </div>
            ) : health ? (
              <div className="p-4 bg-emerald-50 rounded-xl border border-emerald-200 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                <div className="flex items-center gap-3">
                  <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0" />
                  <div>
                    <span className="font-semibold text-emerald-900 text-sm">{health.service}</span>
                    <span className="ml-2 text-xs bg-emerald-200/70 text-emerald-800 px-2 py-0.5 rounded font-mono">v{health.version}</span>
                    <p className="text-xs text-emerald-700 mt-0.5">{health.architecture}</p>
                  </div>
                </div>
                <div className="text-xs text-emerald-800 font-mono bg-white/70 px-3 py-1.5 rounded-lg border border-emerald-200/60 self-start sm:self-auto">
                  Status: {health.status}
                </div>
              </div>
            ) : null}
          </div>
        </div>

        {/* OOP & Architecture Highlights */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm space-y-3">
            <div className="w-10 h-10 rounded-lg bg-indigo-50 flex items-center justify-center text-indigo-600">
              <ShieldCheck className="w-5 h-5" />
            </div>
            <h3 className="font-semibold text-slate-900">Encapsulation & Security</h3>
            <p className="text-sm text-slate-600">
              Controlled constructors, private state invariants, and stateless JWT authentication with Spring Security 6 RBAC.
            </p>
          </div>

          <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm space-y-3">
            <div className="w-10 h-10 rounded-lg bg-blue-50 flex items-center justify-center text-blue-600">
              <Cpu className="w-5 h-5" />
            </div>
            <h3 className="font-semibold text-slate-900">Polymorphism & Patterns</h3>
            <p className="text-sm text-slate-600">
              Strategy Pattern for absolute/relative grading and State Pattern enforcing valid enrollment transitions.
            </p>
          </div>

          <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm space-y-3">
            <div className="w-10 h-10 rounded-lg bg-violet-50 flex items-center justify-center text-violet-600">
              <Layers className="w-5 h-5" />
            </div>
            <h3 className="font-semibold text-slate-900">Joined Inheritance</h3>
            <p className="text-sm text-slate-600">
              JPA Joined table inheritance mapping common <code className="text-xs font-mono bg-slate-100 px-1 py-0.5 rounded">Person</code> attributes to Student, Faculty, and Admin entities.
            </p>
          </div>
        </div>

        {/* Quick Portal Access */}
        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
          <h2 className="text-base font-semibold text-slate-900 mb-4">Phase 1 Target Portals</h2>
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <Link to="/login" className="p-4 rounded-lg bg-slate-50 border border-slate-200 hover:border-indigo-300 hover:bg-indigo-50/30 transition-all">
              <div className="font-medium text-slate-900 flex items-center justify-between text-sm">
                <span>Admin Dashboard</span>
                <ArrowRight className="w-4 h-4 text-slate-400" />
              </div>
              <p className="text-xs text-slate-500 mt-1">Student, Faculty, Course, and Department Management</p>
            </Link>
            <Link to="/login" className="p-4 rounded-lg bg-slate-50 border border-slate-200 hover:border-indigo-300 hover:bg-indigo-50/30 transition-all">
              <div className="font-medium text-slate-900 flex items-center justify-between text-sm">
                <span>Faculty Dashboard</span>
                <ArrowRight className="w-4 h-4 text-slate-400" />
              </div>
              <p className="text-xs text-slate-500 mt-1">Assigned Courses, Attendance Marking, Grade Entry</p>
            </Link>
            <Link to="/login" className="p-4 rounded-lg bg-slate-50 border border-slate-200 hover:border-indigo-300 hover:bg-indigo-50/30 transition-all">
              <div className="font-medium text-slate-900 flex items-center justify-between text-sm">
                <span>Student Dashboard</span>
                <ArrowRight className="w-4 h-4 text-slate-400" />
              </div>
              <p className="text-xs text-slate-500 mt-1">Course Catalog, Enrollments, Attendance & Grade History</p>
            </Link>
          </div>
        </div>
      </main>

      {/* Footer */}
      <footer className="bg-white border-t border-slate-200 py-6 text-center text-xs text-slate-500">
        CampusConnect &copy; 2026 &bull; Object-Oriented Methodology (OOM) Project &bull; Spring Boot + React + PostgreSQL
      </footer>
    </div>
  );
};

export default LandingPage;
