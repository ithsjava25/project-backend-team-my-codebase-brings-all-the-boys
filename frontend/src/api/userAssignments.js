import client from './client';

export const userAssignmentApi = {
    getByAssignmentAndStudent: async (assignmentId, studentId) => {
        const response = await client.get(`/user-assignments/assignment/${assignmentId}/student/${studentId}`);
        return response.data;
    },

    getByAssignment: async (assignmentId) => {
        const response = await client.get(`/user-assignments/assignment/${assignmentId}`);
        return response.data;
    },

    getMyAssignment: async (assignmentId) => {
        const response = await client.get(`/user-assignments/my/${assignmentId}`);
        return response.data;
    },

    evaluate: async (id, evaluationData) => {
        const response = await client.post(`/user-assignments/${id}/evaluate`, evaluationData);
        return response.data;
    },
};
