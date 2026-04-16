import { vi, beforeEach, describe, it, expect } from 'vitest';
import { renderWithProviders } from '@/test/utils';
import { fireEvent, waitFor } from '@testing-library/react';
import LoginPage from './LoginPage';

Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
});

vi.mock('react-router-dom', async () => ({
  ...(await vi.importActual('react-router-dom')),
  useNavigate: vi.fn(),
}));

vi.mock('../context/AuthContext', () => ({
  AuthProvider: ({ children }) => children,
  useAuthContext: vi.fn(),
}));

vi.mock('../api/client', () => ({
  default: {
    post: vi.fn(),
  },
}));

describe('LoginPage', () => {
  let useAuthContext, useNavigate, client;

  beforeEach(async () => {
    vi.clearAllMocks();
    useAuthContext = (await import('../context/AuthContext')).useAuthContext;
    useNavigate = (await import('react-router-dom')).useNavigate;
    client = (await import('../api/client')).default;
  });

  it('redirects to /dashboard when user is already logged in', async () => {
    const mockNavigate = vi.fn();
    useNavigate.mockReturnValue(mockNavigate);
    useAuthContext.mockReturnValue({
      user: { id: '1', username: 'testuser' },
      loading: false,
      refetch: vi.fn(),
    });

    renderWithProviders(<LoginPage />);

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith('/dashboard');
    });
  });

  it('shows login form when user is not logged in', async () => {
    const mockNavigate = vi.fn();
    useNavigate.mockReturnValue(mockNavigate);
    useAuthContext.mockReturnValue({
      user: null,
      loading: false,
      refetch: vi.fn(),
    });

    const { container, getByText } = renderWithProviders(<LoginPage />);

    await waitFor(() => {
      expect(mockNavigate).not.toHaveBeenCalled();
    });

    expect(getByText('ITBS Studentportal')).toBeInTheDocument();
    expect(getByText('Enter your credentials to access your account')).toBeInTheDocument();
    expect(getByText('Login')).toBeInTheDocument();
    expect(getByText('Register')).toBeInTheDocument();
    expect(container.querySelector('input[type="text"]')).toBeInTheDocument();
    expect(container.querySelector('input[type="password"]')).toBeInTheDocument();
  });

  it('successful login redirects to /dashboard and calls refetch', async () => {
    const mockNavigate = vi.fn();
    const mockRefetch = vi.fn();
    useNavigate.mockReturnValue(mockNavigate);
    useAuthContext.mockReturnValue({
      user: null,
      loading: false,
      refetch: mockRefetch,
    });
    client.post.mockResolvedValue({ data: { success: true } });

    const { container } = renderWithProviders(<LoginPage />);

    const usernameInput = container.querySelector('input[type="text"]');
    const passwordInput = container.querySelector('input[type="password"]');
    const submitButton = container.querySelector('button[type="submit"]');

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(client.post).toHaveBeenCalledWith('/auth/login', {
        username: 'testuser',
        password: 'password123',
      });
      expect(mockRefetch).toHaveBeenCalled();
      expect(mockNavigate).toHaveBeenCalledWith('/dashboard');
    });
  });

  it('failed login displays error message', async () => {
    const mockNavigate = vi.fn();
    const mockRefetch = vi.fn();
    useNavigate.mockReturnValue(mockNavigate);
    useAuthContext.mockReturnValue({
      user: null,
      loading: false,
      refetch: mockRefetch,
    });
    client.post.mockRejectedValue({
      response: {
        data: { error: 'Invalid credentials' },
      },
    });

    const { container, getByText } = renderWithProviders(<LoginPage />);

    const usernameInput = container.querySelector('input[type="text"]');
    const passwordInput = container.querySelector('input[type="password"]');
    const submitButton = container.querySelector('button[type="submit"]');

    fireEvent.change(usernameInput, { target: { value: 'wronguser' } });
    fireEvent.change(passwordInput, { target: { value: 'wrongpass' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(getByText('Invalid credentials')).toBeInTheDocument();
      expect(mockNavigate).not.toHaveBeenCalledWith('/dashboard');
    });
  });
});
