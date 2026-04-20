import { vi, beforeEach, describe, it, expect } from 'vitest';
import { ProtectedRoute } from './ProtectedRoute';
import { renderWithProviders } from '@/test/utils';

vi.mock('react-router-dom', async () => ({
  ...(await vi.importActual('react-router-dom')),
  Navigate: vi.fn(({ to }) => <div data-testid="navigate" data-to={to} />),
}));

vi.mock('../context/AuthContext', () => ({
  AuthProvider: ({ children }) => children,
  useAuthContext: vi.fn(),
}));

describe('ProtectedRoute', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('shows loading indicator when authentication is loading', async () => {
    const { useAuthContext } = await import('../context/AuthContext');
    useAuthContext.mockReturnValue({ user: null, loading: true });

    const { container } = renderWithProviders(
      <ProtectedRoute><div>Secret</div></ProtectedRoute>
    );

    expect(container.textContent).toContain('Loading...');
  });

  it('redirects to /login when user is not authenticated', async () => {
    const { useAuthContext } = await import('../context/AuthContext');
    useAuthContext.mockReturnValue({ user: null, loading: false });

    const { getByTestId } = renderWithProviders(
      <ProtectedRoute><div>Secret</div></ProtectedRoute>
    );

    const navigate = getByTestId('navigate');
    expect(navigate).toHaveAttribute('data-to', '/login');
  });

  it('renders children when user is authenticated', async () => {
    const { useAuthContext } = await import('../context/AuthContext');
    useAuthContext.mockReturnValue({
      user: { id: '1', username: 'Anna', email: 'anna@example.com' },
      loading: false,
    });

    const { container } = renderWithProviders(
      <ProtectedRoute><div>Secret content</div></ProtectedRoute>
    );

    expect(container.textContent).toContain('Secret content');
  });

  it('does not redirect when user is authenticated', async () => {
    const { useAuthContext } = await import('../context/AuthContext');
    useAuthContext.mockReturnValue({
      user: { id: '1', username: 'Anna' },
      loading: false,
    });

    const { queryByTestId } = renderWithProviders(
      <ProtectedRoute><div>Secret</div></ProtectedRoute>
    );

    expect(queryByTestId('navigate')).toBeNull();
  });
});