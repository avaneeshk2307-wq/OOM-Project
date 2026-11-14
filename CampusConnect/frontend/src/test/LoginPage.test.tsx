import { describe, it, expect, beforeEach, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { LoginPage } from '../pages/auth/LoginPage';
import { AuthProvider } from '../context/AuthContext';
import { apiClient } from '../api/client';

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

vi.mock('../api/client', () => ({
  apiClient: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

const renderLoginPage = () => {
  return render(
    <BrowserRouter>
      <AuthProvider>
        <LoginPage />
      </AuthProvider>
    </BrowserRouter>
  );
};

describe('LoginPage Component', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.clearAllMocks();
    (apiClient.get as any).mockResolvedValue({ data: null });
  });

  it('renders login form with role selector and default admin credentials', () => {
    renderLoginPage();

    expect(screen.getByText('CampusConnect')).toBeInTheDocument();
    expect(screen.getByText('University Campus Management Portal')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Admin Login/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Faculty Login/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Student Login/i })).toBeInTheDocument();

    const usernameInput = screen.getByPlaceholderText('e.g. admin, faculty, student') as HTMLInputElement;
    const passwordInput = screen.getByPlaceholderText('••••••••') as HTMLInputElement;

    expect(usernameInput.value).toBe('admin');
    expect(passwordInput.value).toBe('admin123');
  });

  it('switches role and presets credentials when role buttons are clicked', () => {
    renderLoginPage();

    const facultyBtn = screen.getByRole('button', { name: /Faculty Login/i });
    fireEvent.click(facultyBtn);

    const usernameInput = screen.getByPlaceholderText('e.g. admin, faculty, student') as HTMLInputElement;
    const passwordInput = screen.getByPlaceholderText('••••••••') as HTMLInputElement;

    expect(usernameInput.value).toBe('faculty');
    expect(passwordInput.value).toBe('faculty123');

    const studentBtn = screen.getByRole('button', { name: /Student Login/i });
    fireEvent.click(studentBtn);

    expect(usernameInput.value).toBe('student');
    expect(passwordInput.value).toBe('student123');
  });

  it('toggles password visibility when the eye button is clicked', () => {
    renderLoginPage();

    const passwordInput = screen.getByPlaceholderText('••••••••') as HTMLInputElement;
    expect(passwordInput.type).toBe('password');

    const toggleBtn = screen.getByLabelText('Show password');
    fireEvent.click(toggleBtn);
    expect(passwordInput.type).toBe('text');

    const hideBtn = screen.getByLabelText('Hide password');
    fireEvent.click(hideBtn);
    expect(passwordInput.type).toBe('password');
  });

  it('displays validation error if username or password is submitted empty', async () => {
    renderLoginPage();

    const usernameInput = screen.getByPlaceholderText('e.g. admin, faculty, student');
    const passwordInput = screen.getByPlaceholderText('••••••••');

    fireEvent.change(usernameInput, { target: { value: '' } });
    fireEvent.change(passwordInput, { target: { value: '' } });

    const submitBtn = screen.getByRole('button', { name: /Sign In as/i });
    const form = submitBtn.closest('form')!;
    fireEvent.submit(form);

    expect(await screen.findByText('Please provide both username and password.')).toBeInTheDocument();
  });

  it('successfully authenticates and redirects on valid credentials', async () => {
    (apiClient.post as any).mockResolvedValueOnce({
      data: {
        token: 'test-jwt-token',
        userId: 1,
        personId: 10,
        username: 'admin',
        fullName: 'System Administrator',
        email: 'admin@campusconnect.edu',
        role: 'ROLE_ADMIN',
      },
    });

    renderLoginPage();

    const submitBtn = screen.getByRole('button', { name: /Sign In as/i });
    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(apiClient.post).toHaveBeenCalledWith('/auth/login', {
        username: 'admin',
        password: 'admin123',
      });
      expect(mockNavigate).toHaveBeenCalledWith('/admin', { replace: true });
    });
  });

  it('displays error banner when authentication fails', async () => {
    (apiClient.post as any).mockRejectedValueOnce({
      response: {
        status: 401,
        data: { message: 'Invalid username or password' },
      },
    });

    renderLoginPage();

    const submitBtn = screen.getByRole('button', { name: /Sign In as/i });
    fireEvent.click(submitBtn);

    expect(await screen.findByText(/Invalid username or password/i)).toBeInTheDocument();
  });
});
