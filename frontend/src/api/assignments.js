import client from './client';

export const assignmentApi = {
  getAllAssignments: async () => {
    const response = await client.get('/assignments');
    return response.data;
  },

  getAssignmentById: async (id) => {
    const response = await client.get(`/assignments/${id}`);
    return response.data;
  },
};