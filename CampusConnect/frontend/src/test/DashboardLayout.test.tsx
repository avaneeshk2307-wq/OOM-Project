import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { DashboardLayout } from '../components/layout/DashboardLayout';
import * as AuthContextModule from '../context/AuthContext';
import { User } from '../types/auth';

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useLocation: () => ({ pathname: '/admin' }),
  };
});

// Mock NotificationBell
vi.mock('../components/layout/NotificationBell', () => ({
  NotificationBell: () => <div data-testid="notification-bell">NotificationBell</div>,
}));

describe('DashboardLayout Component', () => {
  const mockLogout = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  const renderLayoutForUser = (user: User) => {
    vi.spyOn(AuthContextModule, 'useAuth').mockReturnValue({
      user,
      token: 'jwt-token',
      loading: false,
      login: vi.fn(),
      logout: mockLogout,
      hasRole: (roles) => {
        if (Array.isArray(roles)) return roles.includes(user.role);
        return user.role === roles;
      },
    });

    return render(
      <BrowserRouter>
        <DashboardLayout />
      </BrowserRouter>
    );
  };

  it('renders admin navigation items for ROLE_ADMIN', () => {
    const adminUser: User = {
      userId: 1,
      personId: 10,
      username: 'admin',
      fullName: 'System Administrator',
      email: 'admin@campusconnect.edu',
      role: 'ROLE_ADMIN',
    };

    renderLayoutForUser(adminUser);

    expect(screen.getByText('System Administrator')).toBeInTheDocument();
    expect(screen.getByText('admin@campusconnect.edu')).toBeInTheDocument();
    expect(screen.getByText('Students')).toBeInTheDocument();
    expect(screen.getByText('Faculty')).toBeInTheDocument();
    expect(screen.getByText('Departments')).toBeInTheDocument();
    expect(screen.getByText('Courses')).toBeInTheDocument();
    expect(screen.getByText('Enrollments')).toBeInTheDocument();
    expect(screen.getByText('Timetable')).toBeInTheDocument();
    expect(screen.getByText('Analytics')).toBeInTheDocument();
  });

  it('renders faculty navigation items for ROLE_FACULTY', () => {
    const facultyUser: User = {
      userId: 2,
      personId: 20,
      username: 'fac_alan',
      fullName: 'Dr. Alan Turing',
      email: 'alan.turing@campusconnect.edu',
      role: 'ROLE_FACULTY',
    };

    renderLayoutForUser(facultyUser);

    expect(screen.getByText('Dr. Alan Turing')).toBeInTheDocument();
    expect(screen.getByText('Assigned Courses')).toBeInTheDocument();
    expect(screen.getByText('My Schedule')).toBeInTheDocument();
    expect(screen.getByText('Mark Attendance')).toBeInTheDocument();
    expect(screen.getByText('Enter Grades')).toBeInTheDocument();
  });

  it('renders student navigation items for ROLE_STUDENT', () => {
    const studentUser: User = {
      userId: 3,
      personId: 30,
      username: 'stu_ada',
      fullName: 'Ada Lovelace',
      email: 'ada.lovelace@campusconnect.edu',
      role: 'ROLE_STUDENT',
    };

    renderLayoutForUser(studentUser);

    expect(screen.getByText('Ada Lovelace')).toBeInTheDocument();
    expect(screen.getByText('Class Schedule')).toBeInTheDocument();
    expect(screen.getByText('Course Catalog')).toBeInTheDocument();
    expect(screen.getByText('My Enrollments')).toBeInTheDocument();
    expect(screen.getByText('Attendance Record')).toBeInTheDocument();
    expect(screen.getByText('Grades & Results')).toBeInTheDocument();
  });

  it('executes logout and redirects to /login when sign out is clicked', () => {
    const adminUser: User = {
      userId: 1,
      personId: 10,
      username: 'admin',
      fullName: 'System Administrator',
      email: 'admin@campusconnect.edu',
      role: 'ROLE_ADMIN',
    };

    renderLayoutForUser(adminUser);

    const logoutButton = screen.getByRole('button', { name: /Sign Out/i });
    fireEvent.click(logoutButton);

    expect(mockLogout).toHaveBeenCalledTimes(1);
    expect(mockNavigate).toHaveBeenCalledWith('/login');
  });
});
