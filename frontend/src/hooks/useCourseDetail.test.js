import { describe, it, expect, vi, beforeEach } from 'vitest';
import {act, renderHook, waitFor} from '@testing-library/react';
import { useCourseDetail } from './useCourseDetail';
import { courseApi } from '../api/courses';
import { mockCourseFromApi } from '../test/fixtures/courseData';

vi.mock('../api/courses');

describe('useCourseDetail', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('starts with loading true when courseId is provided', () => {
    vi.mocked(courseApi.getCourseById).mockResolvedValue(mockCourseFromApi);

    const { result } = renderHook(() => useCourseDetail('course-123'));

    expect(result.current.course).toBe(null);
    expect(result.current.loading).toBe(true);
    expect(result.current.error).toBe(null);
  });

  it('fetches course on mount with courseId parameter', async () => {
    vi.mocked(courseApi.getCourseById).mockResolvedValue(mockCourseFromApi);

    const { result } = renderHook(() => useCourseDetail('course-123'));

    await waitFor(() => {
      expect(result.current.course).toEqual(mockCourseFromApi);
      expect(result.current.loading).toBe(false);
    });

    expect(courseApi.getCourseById).toHaveBeenCalledWith('course-123');
    expect(courseApi.getCourseById).toHaveBeenCalledTimes(1);
  });

  it('handles null or undefined courseId gracefully', () => {
    const { result } = renderHook(() => useCourseDetail(null));

    expect(result.current.course).toBe(null);
    expect(result.current.loading).toBe(false);
    expect(result.current.error).toBe(null);
    expect(courseApi.getCourseById).not.toHaveBeenCalled();
  });

  it('refetches course when courseId changes', async () => {
    const course1 = { ...mockCourseFromApi, id: 'course-1', name: 'Course 1' };
    const course2 = { ...mockCourseFromApi, id: 'course-2', name: 'Course 2' };

    vi.mocked(courseApi.getCourseById)
      .mockResolvedValueOnce(course1)
      .mockResolvedValueOnce(course2);

    const { result, rerender } = renderHook(
      ({ courseId }) => useCourseDetail(courseId),
      { initialProps: { courseId: 'course-1' } }
    );

    await waitFor(() => {
      expect(result.current.course).toEqual(course1);
    });

    act(() => {
      rerender({ courseId: 'course-2' });
    });

    await waitFor(() => {
      expect(result.current.course).toEqual(course2);
    });

    expect(courseApi.getCourseById).toHaveBeenCalledTimes(2);
    expect(courseApi.getCourseById).toHaveBeenNthCalledWith(1, 'course-1');
    expect(courseApi.getCourseById).toHaveBeenNthCalledWith(2, 'course-2');
  });

  it('handles API error and sets error message', async () => {
    const error = Object.assign(new Error('Not found'), {
      response: { data: { message: 'Course not found' } }
    });
    vi.mocked(courseApi.getCourseById).mockRejectedValue(error);

    const { result } = renderHook(() => useCourseDetail('course-123'));

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
      expect(result.current.error).toBe('Course not found');
      expect(result.current.course).toBe(null);
    });
  });

  it('handles network error without response data', async () => {
    const networkError = new Error('Network Error');
    vi.mocked(courseApi.getCourseById).mockRejectedValue(networkError);

    const { result } = renderHook(() => useCourseDetail('course-123'));

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
      expect(result.current.error).toBe('Failed to fetch course');
      expect(result.current.course).toBe(null);
    });
  });

  it('does not update state after component unmounts', async () => {
    vi.mocked(courseApi.getCourseById).mockResolvedValue(mockCourseFromApi);
    const errorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});
    const { result, unmount } = renderHook(() => useCourseDetail('course-123'));

    unmount();
    await new Promise(resolve => setTimeout(resolve, 0));

    expect(result.current.course).toBe(null);
    expect(errorSpy).not.toHaveBeenCalledWith(
      expect.stringContaining('unmounted component')
    );
    errorSpy.mockRestore();
  });
});