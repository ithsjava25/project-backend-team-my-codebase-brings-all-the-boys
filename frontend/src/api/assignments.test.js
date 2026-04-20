import { describe, it, expect, vi, beforeEach } from 'vitest';
import { assignmentApi } from './assignments';
import client from './client';

vi.mock('./client');

const mockAssignment = { id: '1', title: 'Assignment 1' };

describe('assignmentApi', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getAllAssignments', () => {
    it('calls the correct endpoint and returns data', async () => {
      vi.mocked(client.get).mockResolvedValue({ data: [mockAssignment] });

      const result = await assignmentApi.getAllAssignments();

      expect(client.get).toHaveBeenCalledWith('/assignments');
      expect(result).toEqual([mockAssignment]);
    });

    it('returns an empty array when backend responds with []', async () => {
      vi.mocked(client.get).mockResolvedValue({ data: [] });

      const result = await assignmentApi.getAllAssignments();

      expect(result).toEqual([]);
    });

    it('rethrows network errors', async () => {
      vi.mocked(client.get).mockRejectedValue(new Error('Network Error'));

      await expect(assignmentApi.getAllAssignments()).rejects.toThrow('Network Error');
    });
  });

  describe('getAssignmentById', () => {
    it('calls the correct endpoint with the assignment id and returns data', async () => {
      vi.mocked(client.get).mockResolvedValue({ data: mockAssignment });

      const result = await assignmentApi.getAssignmentById('1');

      expect(client.get).toHaveBeenCalledWith('/assignments/1');
      expect(result).toEqual(mockAssignment);
    });

    it('rethrows axios errors (e.g. 404)', async () => {
      const axiosError = Object.assign(new Error('Request failed with status code 404'), {
        response: { status: 404, data: { message: 'Not found' } },
      });
      vi.mocked(client.get).mockRejectedValue(axiosError);

      await expect(assignmentApi.getAssignmentById('999')).rejects.toThrow('404');
    });

    it('rethrows network errors without a response', async () => {
      vi.mocked(client.get).mockRejectedValue(new Error('Network Error'));

      await expect(assignmentApi.getAssignmentById('1')).rejects.toThrow('Network Error');
    });
  });
});