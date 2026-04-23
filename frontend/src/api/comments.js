import client from './client';

export const commentApi = {
  getCommentsByAssignment: async (assignmentId) => {
    const response = await client.get(`/comments/assignment/${assignmentId}`);
    return response.data;
  },

  addComment: async (assignmentId, text) => {
    const response = await client.post(`/comments/assignment/${assignmentId}`, { text });
    return response.data;
  },

  getPersonalComments: async (userAssignmentId) => {
    const response = await client.get(`/comments/user-assignment/${userAssignmentId}`);
    return response.data;
  },

  addPersonalComment: async (userAssignmentId, text) => {
    const response = await client.post(`/comments/user-assignment/${userAssignmentId}`, { text });
    return response.data;
  },
};