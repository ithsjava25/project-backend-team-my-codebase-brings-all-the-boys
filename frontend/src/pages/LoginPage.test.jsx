import { vi, beforeEach, describe, it, expect } from 'vitest';
import { renderWithProviders } from '@/test/utils';
import { fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import LoginPage from './LoginPage';

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

  it('successful register redirects to /dashboard and calls refetch', async () => {
    const mockNavigate = vi.fn();
    const mockRefetch = vi.fn();
    useNavigate.mockReturnValue(mockNavigate);
    useAuthContext.mockReturnValue({
      user: null,
      loading: false,
      refetch: mockRefetch,
    });
    client.post.mockResolvedValue({ data: { success: true } });

    const user = userEvent.setup();
    const { container, getByText, findByLabelText } = renderWithProviders(<LoginPage />);

    await user.click(getByText('Register'));

    const emailInput = await findByLabelText(/email/i);
    const usernameInput = container.querySelector('input[type="text"]');
    const passwordInputs = container.querySelectorAll('input[type="password"]');
    const submitButton = container.querySelector('button[type="submit"]');

    await user.type(usernameInput, 'newuser');
    await user.type(emailInput, 'new@example.com');
    await user.type(passwordInputs[0], 'password123');
    await user.type(passwordInputs[1], 'password123');
    await user.click(submitButton);

    await waitFor(() => {
      expect(client.post).toHaveBeenCalledWith('/auth/register', {
        username: 'newuser',
        email: 'new@example.com',
        password: 'password123',
        confirmPassword: 'password123',
      });
      expect(mockRefetch).toHaveBeenCalled();
      expect(mockNavigate).toHaveBeenCalledWith('/dashboard');
    });
  });

  it('displays fallback error message for network errors', async () => {
    const mockNavigate = vi.fn();
    const mockRefetch = vi.fn();
    useNavigate.mockReturnValue(mockNavigate);
    useAuthContext.mockReturnValue({
      user: null,
      loading: false,
      refetch: mockRefetch,
    });
    client.post.mockRejectedValue(new Error('Network Error'));

    const { container, getByText } = renderWithProviders(<LoginPage />);

    const usernameInput = container.querySelector('input[type="text"]');
    const passwordInput = container.querySelector('input[type="password"]');
    const submitButton = container.querySelector('button[type="submit"]');

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(getByText('Unexpected error. Please try again.')).toBeInTheDocument();
      expect(mockNavigate).not.toHaveBeenCalledWith('/dashboard');
    });
  });
});
