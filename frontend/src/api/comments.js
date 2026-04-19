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
};