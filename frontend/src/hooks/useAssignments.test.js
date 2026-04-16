import { it, expect, vi, beforeEach, describe } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { useAssignments } from './useAssignments';
import { assignmentApi } from '../api/assignments';
import { mockAssignment } from '../test/fixtures/assignmentData';

vi.mock('../api/assignments');

describe('useAssignments', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('starts with loading true, assignments empty and error null', async () => {
    vi.mocked(assignmentApi.getAllAssignments).mockResolvedValue([mockAssignment]);

    const { result } = renderHook(() => useAssignments());

    expect(result.current.assignments).toEqual([]);
    expect(result.current.loading).toBe(true);
    expect(result.current.error).toBe(null);

    await waitFor(() => expect(result.current.loading).toBe(false));
  });

  it('fetches assignments on mount', async () => {
    vi.mocked(assignmentApi.getAllAssignments).mockResolvedValue([mockAssignment]);

    const { result } = renderHook(() => useAssignments());

    await waitFor(() => {
      expect(result.current.assignments).toEqual([mockAssignment]);
      expect(result.current.loading).toBe(false);
    });

    expect(assignmentApi.getAllAssignments).toHaveBeenCalledTimes(1);
  });

  it('handles empty response from backend', async () => {
    vi.mocked(assignmentApi.getAllAssignments).mockResolvedValue([]);

    const { result } = renderHook(() => useAssignments());

    await waitFor(() => {
      expect(result.current.assignments).toEqual([]);
      expect(result.current.loading).toBe(false);
      expect(result.current.error).toBe(null);
    });
  });

  it('handles API error and sets error message', async () => {
    const error = Object.assign(new Error('Failed to fetch'), {
      response: { data: { message: 'Server error' } }
    });
    vi.mocked(assignmentApi.getAllAssignments).mockRejectedValue(error);

    const { result } = renderHook(() => useAssignments());

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
      expect(result.current.error).toBe('Server error');
      expect(result.current.assignments).toEqual([]);
    });
  });

  it('handles network error without response data', async () => {
    const networkError = new Error('Network Error');
    vi.mocked(assignmentApi.getAllAssignments).mockRejectedValue(networkError);

    const { result } = renderHook(() => useAssignments());

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
      expect(result.current.error).toBe('Failed to fetch assignments');
      expect(result.current.assignments).toEqual([]);
    });
  });
});