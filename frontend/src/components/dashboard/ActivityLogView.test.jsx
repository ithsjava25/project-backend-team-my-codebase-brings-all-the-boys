import { vi, describe, it, expect, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
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
    // Mock browser APIs not implemented in JSDOM
    window.Element.prototype.scrollIntoView = vi.fn();
    window.Element.prototype.hasPointerCapture = vi.fn();
    window.Element.prototype.setPointerCapture = vi.fn();
    window.Element.prototype.releasePointerCapture = vi.fn();
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
        details: { name: 'Math 101' },
        timestamp: '2026-04-24T10:00:00Z',
      },
    ];
    activityLogApi.getAllLogs.mockResolvedValue({ content: mockLogs });

    render(<ActivityLogView />);

    await waitFor(() => {
      expect(screen.getByText('alice')).toBeInTheDocument();
      expect(screen.getByText(/skapade kursen "Math 101"/i)).toBeInTheDocument();
    });
  });

  it('forwards filters correctly to the API', async () => {
    const user = userEvent.setup();
    activityLogApi.getAllLogs.mockResolvedValue({ content: [] });
    
    render(<ActivityLogView />);
    
    // Initial call
    await waitFor(() => {
      expect(activityLogApi.getAllLogs).toHaveBeenCalled();
    });

    // Open "Åtgärd" select and choose "Skapad"
    const trigger = screen.getByText(/Alla åtgärder/i).closest('button');
    await user.click(trigger);
    
    // Radix Select renders options in a Portal, find by role option
    const option = await screen.findByRole('option', { name: /Skapad/i });
    await user.click(option);

    await waitFor(() => {
      expect(activityLogApi.getAllLogs).toHaveBeenCalledWith(
        0, 10, expect.objectContaining({ action: 'CREATED' }), expect.any(AbortSignal)
      );
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
