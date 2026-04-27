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

    getEvaluatedAssignments: async (page = 0, size = 10, signal = undefined) => {
        const response = await client.get(`/user-assignments/evaluated`, { 
            params: { page, size },
            signal 
        });
        return response.data;
    },

    evaluate: async (id, evaluationData) => {
        const response = await client.post(`/user-assignments/${id}/evaluate`, evaluationData);
        return response.data;
    },

    submit: async (id, submissionData) => {
        const response = await client.post(`/user-assignments/${id}/submit`, submissionData);
        return response.data;
    },
};
