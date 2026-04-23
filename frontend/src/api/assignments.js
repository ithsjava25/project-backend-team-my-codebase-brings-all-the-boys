import client from './client';

export const assignmentApi = {
  getAllAssignments: async (params = {}) => {
    const response = await client.get('/assignments', { params });
    return response.data;
  },

  getAssignmentById: async (id) => {
    const response = await client.get(`/assignments/${id}`);
    return response.data;
    },

    createAssignment: async (assignmentData) => {
    const response = await client.post('/assignments', assignmentData);
    return response.data;
    },
    };