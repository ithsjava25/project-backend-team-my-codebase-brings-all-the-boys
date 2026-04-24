import { vi, describe, it, expect, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { ActivityLogView } from './ActivityLogView';
import { activityLogApi } from '@/api/activityLogs';
import { userApi } from '@/api/users';
import { useAuthContext } from '@/context/AuthContext';

vi.mock('@/api/activityLogs', () => ({
  activityLogApi: {
    getAllLogs: vi.fn(),
    getUserLogs: vi.fn(),
    getEntityLogs: vi.fn(),
  },
}));

vi.mock('@/api/users', () => ({
  userApi: {
    getAllUsers: vi.fn(),
  },
}));

vi.mock('@/context/AuthContext', () => ({
  useAuthContext: vi.fn(),
}));

describe('ActivityLogView', () => {
  const mockUser = { id: 'u1', username: 'admin', role: { name: 'ROLE_ADMIN' } };

  beforeEach(() => {
    vi.clearAllMocks();
    useAuthContext.mockReturnValue({ user: mockUser });
    userApi.getAllUsers.mockResolvedValue({ content: [] });
  });

  it('renders loading state initially', () => {
    activityLogApi.getAllLogs.mockReturnValue(new Promise(() => {}));
    render(<ActivityLogView />);
    expect(screen.getByText(/Laddar loggar/i)).toBeInTheDocument();
  });

  it('renders filters for admin users', async () => {
    activityLogApi.getAllLogs.mockResolvedValue({ content: [] });
    render(<ActivityLogView />);
    
    await waitFor(() => {
      // Use getAllByText for labels since they might appear in multiple places (select trigger vs label)
      expect(screen.getAllByText(/Användare/i).length).toBeGreaterThan(0);
      expect(screen.getAllByText(/Åtgärd/i).length).toBeGreaterThan(0);
      expect(screen.getAllByText(/Typ/i).length).toBeGreaterThan(0);
    });
  });

  it('renders logs correctly', async () => {
    const mockLogs = [
      {
        id: 'l1',
        actorUsername: 'alice',
        action: 'CREATED',
        entityType: 'COURSE',
        timestamp: '2026-04-24T10:00:00Z',
      },
    ];
    activityLogApi.getAllLogs.mockResolvedValue({ content: mockLogs });

    render(<ActivityLogView />);

    await waitFor(() => {
      expect(screen.getByText('alice')).toBeInTheDocument();
      // Action/Type are formatted in the component
      expect(screen.getByText(/Skapad/i)).toBeInTheDocument();
      expect(screen.getByText(/Kurs/i)).toBeInTheDocument();
    });
  });

  it('hides filters for non-admin users', async () => {
    useAuthContext.mockReturnValue({ user: { ...mockUser, role: { name: 'ROLE_STUDENT' } } });
    activityLogApi.getUserLogs.mockResolvedValue({ content: [] });
    
    render(<ActivityLogView />);
    
    await waitFor(() => {
      expect(screen.queryByText(/Användare/i)).not.toBeInTheDocument();
      expect(screen.queryByText(/Åtgärd/i)).not.toBeInTheDocument();
      expect(screen.queryByText(/Typ/i)).not.toBeInTheDocument();
    });
  });
});
