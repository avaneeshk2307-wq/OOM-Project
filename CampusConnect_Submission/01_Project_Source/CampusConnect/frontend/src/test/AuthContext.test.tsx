import React from 'react';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { render, screen, act } from '@testing-library/react';
import { AuthProvider, useAuth } from '../context/AuthContext';
import { User } from '../types/auth';

const TestConsumer: React.FC = () => {
  const { user, token, login, logout, hasRole } = useAuth();

  const mockAdminUser: User = {
    userId: 1,
    personId: 10,
    username: 'admin',
    fullName: 'System Administrator',
    email: 'admin@campusconnect.edu',
    role: 'ROLE_ADMIN',
  };

  return (
    <div>
      <div data-testid="user-info">{user ? user.username : 'no-user'}</div>
      <div data-testid="user-role">{user ? user.role : 'no-role'}</div>
      <div data-testid="token-info">{token || 'no-token'}</div>
      <div data-testid="is-admin">{hasRole('ROLE_ADMIN') ? 'yes-admin' : 'no-admin'}</div>
      <div data-testid="is-faculty">{hasRole('ROLE_FACULTY') ? 'yes-faculty' : 'no-faculty'}</div>
      <div data-testid="is-admin-or-faculty">{hasRole(['ROLE_ADMIN', 'ROLE_FACULTY']) ? 'yes' : 'no'}</div>
      <button onClick={() => login('mock-jwt-token', mockAdminUser)}>Login</button>
      <button onClick={logout}>Logout</button>
    </div>
  );
};

describe('AuthContext & AuthProvider', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.clearAllMocks();
  });

  it('provides default initial unauthenticated state when storage is empty', () => {
    render(
      <AuthProvider>
        <TestConsumer />
      </AuthProvider>
    );

    expect(screen.getByTestId('user-info')).toHaveTextContent('no-user');
    expect(screen.getByTestId('token-info')).toHaveTextContent('no-token');
    expect(screen.getByTestId('is-admin')).toHaveTextContent('no-admin');
  });

  it('updates state and persists to localStorage on login', () => {
    render(
      <AuthProvider>
        <TestConsumer />
      </AuthProvider>
    );

    act(() => {
      screen.getByText('Login').click();
    });

    expect(screen.getByTestId('user-info')).toHaveTextContent('admin');
    expect(screen.getByTestId('user-role')).toHaveTextContent('ROLE_ADMIN');
    expect(screen.getByTestId('token-info')).toHaveTextContent('mock-jwt-token');
    expect(screen.getByTestId('is-admin')).toHaveTextContent('yes-admin');
    expect(screen.getByTestId('is-faculty')).toHaveTextContent('no-faculty');
    expect(screen.getByTestId('is-admin-or-faculty')).toHaveTextContent('yes');

    expect(localStorage.getItem('token')).toBe('mock-jwt-token');
    expect(JSON.parse(localStorage.getItem('user')!)).toEqual(
      expect.objectContaining({ username: 'admin', role: 'ROLE_ADMIN' })
    );
  });

  it('clears state and removes items from localStorage on logout', () => {
    render(
      <AuthProvider>
        <TestConsumer />
      </AuthProvider>
    );

    act(() => {
      screen.getByText('Login').click();
    });
    expect(screen.getByTestId('user-info')).toHaveTextContent('admin');

    act(() => {
      screen.getByText('Logout').click();
    });
    expect(screen.getByTestId('user-info')).toHaveTextContent('no-user');
    expect(screen.getByTestId('token-info')).toHaveTextContent('no-token');
    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('user')).toBeNull();
  });
});
