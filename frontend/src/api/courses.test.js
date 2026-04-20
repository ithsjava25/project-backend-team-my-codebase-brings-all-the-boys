import { describe, it, expect, vi, beforeEach } from 'vitest';
import { courseApi } from './courses';
import client from './client';
import { mockCourseFromApi } from '../test/fixtures/courseData';

vi.mock('./client');

describe('courseApi', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getAllCourses', () => {
    it('calls the correct endpoint and returns data', async () => {
      vi.mocked(client.get).mockResolvedValue({
        data: {
          content:
            [mockCourseFromApi], totalPages: 1, totalElements: 1}
      });

      const result = await courseApi.getAllCourses();

      expect(client.get).toHaveBeenCalledWith('/admin/courses?page=0&size=10');
      expect(result).toEqual({content: [mockCourseFromApi], totalPages: 1, totalElements: 1});
    });

    it('returns an empty array when backend responds with []', async () => {
      vi.mocked(client.get).mockResolvedValue({ data: [] });

      const result = await courseApi.getAllCourses();

      expect(result).toEqual([]);
    });

    it('rethrows network errors', async () => {
      vi.mocked(client.get).mockRejectedValue(new Error('Network Error'));

      await expect(courseApi.getAllCourses()).rejects.toThrow('Network Error');
    });
  });

  describe('getCourseById', () => {
    it('calls the correct endpoint with the course id and returns data', async () => {
      vi.mocked(client.get).mockResolvedValue({ data: mockCourseFromApi });

      const result = await courseApi.getCourseById('123');

      expect(client.get).toHaveBeenCalledWith('/courses/123');
      expect(result).toEqual(mockCourseFromApi);
    });

    it('rethrows axios errors (e.g. 404)', async () => {
      const axiosError = Object.assign(new Error('Request failed with status code 404'), {
        response: { status: 404, data: { message: 'Not found' } },
      });
      vi.mocked(client.get).mockRejectedValue(axiosError);

      await expect(courseApi.getCourseById('999')).rejects.toThrow('404');
    });

    it('rethrows network errors without a response', async () => {
      vi.mocked(client.get).mockRejectedValue(new Error('Network Error'));

      await expect(courseApi.getCourseById('123')).rejects.toThrow('Network Error');
    });
  });
});