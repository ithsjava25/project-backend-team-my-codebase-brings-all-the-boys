import client from './client';

export const assignment = {
    createAssignment: async (assignmentData) => {
        const response = await client.post('/assignments', assignmentData);
        return response.data;
    },

    getAllAssignments: async () => {
        const response = await client.get('/assignments');
        return response.data;
    },

    getAssignmentById: async (id) => {
        const response = await client.get(`/assignments/${id}`);
        return response.data;
    },

    getMyCreatedAssignments: async () => {
        const response = await client.get('/assignments/my-created');
        return response.data;
    },
};