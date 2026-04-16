import { it, expect, vi } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { useCourses } from './useCourses';
import { courseApi } from '../api/courses';
import { mockCourseFromApi } from '../test/fixtures/courseData';

vi.mock('../api/courses');

it('starts with loading true, courses empty and error null', () => {
  // Mock data
  vi.mocked(courseApi.getAllCourses).mockResolvedValue([mockCourseFromApi]);

  // Render hook
  const { result } = renderHook(() => useCourses());

  // Control of initial state
  expect(result.current.courses).toEqual([]);
  expect(result.current.loading).toBe(true);
  expect(result.current.error).toBe(null);
});

it('fetches courses on mount', async () => {
  // Mock data
  vi.mocked(courseApi.getAllCourses).mockResolvedValue([mockCourseFromApi]);

  // Render hook
  const { result } = renderHook(() => useCourses());

  // Assert + Wait (wait on data)
  await waitFor(() => {
    expect(result.current.courses).toEqual([mockCourseFromApi]);
    expect(result.current.loading).toBe(false);
  });

  // Verify API got called
  expect(courseApi.getAllCourses).toHaveBeenCalledTimes(1);
});

it('handles empty response from backend', async () => {
  // Mock empty array
  vi.mocked(courseApi.getAllCourses).mockResolvedValue([]);

  // Render hook
  const { result } = renderHook(() => useCourses());

  // Assert + Wait (empty array but no error)
  await waitFor(() => {
    expect(result.current.courses).toEqual([]);
    expect(result.current.loading).toBe(false);
    expect(result.current.error).toBe(null);
  });
});

it('handles API error and sets error message', async () => {
  // Mock empty array
  const error = Object.assign(new Error('Failed to fetch'), {
    response: { data: { message: 'Server error' } }
  });
  vi.mocked(courseApi.getAllCourses).mockRejectedValue(error);

  // Render hook
  const { result } = renderHook(() => useCourses());

  // Assert + Wait: Error, loading=false
  await waitFor(() => {
    expect(result.current.loading).toBe(false);
    expect(result.current.error).toBe('Server error');
    expect(result.current.courses).toEqual([]);
  });
});

it('handles network error without response data', async () => {
  // Mock network error
  const networkError = new Error('Network Error');
  vi.mocked(courseApi.getAllCourses).mockRejectedValue(networkError);

  // Render hook
  const { result } = renderHook(() => useCourses());

  // Assert + Wait for default error message
  await waitFor(() => {
    expect(result.current.loading).toBe(false);
    expect(result.current.error).toBe('Failed to fetch courses');
    expect(result.current.courses).toEqual([]);
  });
});