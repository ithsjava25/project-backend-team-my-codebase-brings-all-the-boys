import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor, act } from '@testing-library/react';
import { useAuth } from './useAuth';
import client from '../api/client';

vi.mock('../api/client');

const mockUser = { id: '1', username: 'anna', email: 'anna@school.com' };

describe('useAuth', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('starts with loading true and user null', () => {
    vi.mocked(client.get).mockResolvedValue({ data: mockUser });

    const { result } = renderHook(() => useAuth());

    expect(result.current.user).toBe(null);
    expect(result.current.loading).toBe(true);
  });

  it('fetches user on mount', async () => {
    vi.mocked(client.get).mockResolvedValue({ data: mockUser });

    const { result } = renderHook(() => useAuth());

    await waitFor(() => {
      expect(result.current.user).toEqual(mockUser);
      expect(result.current.loading).toBe(false);
    });

    expect(client.get).toHaveBeenCalledWith('/auth/me');
  });

  it('sets user to null on 401', async () => {
    const error = Object.assign(new Error('Unauthorized'), {
      response: { status: 401 },
    });
    vi.mocked(client.get).mockRejectedValue(error);

    const { result } = renderHook(() => useAuth());

    await waitFor(() => expect(result.current.loading).toBe(false));

    expect(result.current.user).toBe(null);
  });

  it('sets user to null on 403', async () => {
    const error = Object.assign(new Error('Forbidden'), {
      response: { status: 403 },
    });
    vi.mocked(client.get).mockRejectedValue(error);

    const { result } = renderHook(() => useAuth());

    await waitFor(() => expect(result.current.loading).toBe(false));

    expect(result.current.user).toBe(null);
  });

  it('logs out the user', async () => {
    vi.mocked(client.get).mockResolvedValue({ data: mockUser });
    vi.mocked(client.post).mockResolvedValue({});

    const { result } = renderHook(() => useAuth());

    await waitFor(() => expect(result.current.user).toEqual(mockUser));

    await act(async () => {
      await result.current.logout();
    });

    expect(client.post).toHaveBeenCalledWith('/auth/logout');
    expect(result.current.user).toBe(null);
  });
});