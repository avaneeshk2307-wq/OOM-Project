import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { RoleGuard } from '../components/layout/RoleGuard';
import * as AuthContextModule from '../context/AuthContext';
import { User } from '../types/auth';

describe('RoleGuard Component', () => {
  const mockAdminUser: User = {
    userId: 1,
    personId: 10,
    username: 'admin',
    fullName: 'System Administrator',
    email: 'admin@campusconnect.edu',
    role: 'ROLE_ADMIN',
  };

  const mockStudentUser: User = {
    userId: 2,
    personId: 20,
    username: 'student',
    fullName: 'Alice Student',
    email: 'alice@campusconnect.edu',
    role: 'ROLE_STUDENT',
  };

  const renderWithRouter = (user: User | null, loading = false, allowedRoles: ('ROLE_ADMIN' | 'ROLE_FACULTY' | 'ROLE_STUDENT')[]) => {
    vi.spyOn(AuthContextModule, 'useAuth').mockReturnValue({
      user,
      token: user ? 'mock-token' : null,
      loading,
      login: vi.fn(),
      logout: vi.fn(),
      hasRole: (roles) => {
        if (!user) return false;
        if (Array.isArray(roles)) return roles.includes(user.role);
        return user.role === roles;
      },
    });

    return render(
      <MemoryRouter initialEntries={['/protected']}>
        <Routes>
          <Route path="/login" element={<div>Login Page Mock</div>} />
          <Route path="/unauthorized" element={<div>Unauthorized Page Mock</div>} />
          <Route element={<RoleGuard allowedRoles={allowedRoles} />}>
            <Route path="/protected" element={<div>Protected Content</div>} />
          </Route>
        </Routes>
      </MemoryRouter>
    );
  };

  it('renders loading state when auth state is being verified', () => {
    renderWithRouter(null, true, ['ROLE_ADMIN']);
    expect(screen.getByText('Verifying credentials...')).toBeInTheDocument();
  });

  it('redirects to /login when user is not authenticated', () => {
    renderWithRouter(null, false, ['ROLE_ADMIN']);
    expect(screen.getByText('Login Page Mock')).toBeInTheDocument();
    expect(screen.queryByText('Protected Content')).not.toBeInTheDocument();
  });

  it('redirects to /unauthorized when user role is not permitted', () => {
    renderWithRouter(mockStudentUser, false, ['ROLE_ADMIN']);
    expect(screen.getByText('Unauthorized Page Mock')).toBeInTheDocument();
    expect(screen.queryByText('Protected Content')).not.toBeInTheDocument();
  });

  it('renders protected content when user has the allowed role', () => {
    renderWithRouter(mockAdminUser, false, ['ROLE_ADMIN']);
    expect(screen.getByText('Protected Content')).toBeInTheDocument();
    expect(screen.queryByText('Login Page Mock')).not.toBeInTheDocument();
    expect(screen.queryByText('Unauthorized Page Mock')).not.toBeInTheDocument();
  });

  it('allows multi-role access (e.g. ROLE_ADMIN or ROLE_FACULTY)', () => {
    renderWithRouter(mockAdminUser, false, ['ROLE_ADMIN', 'ROLE_FACULTY']);
    expect(screen.getByText('Protected Content')).toBeInTheDocument();
  });
});
