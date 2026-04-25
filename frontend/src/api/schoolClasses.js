import client from './client';

export const schoolClassApi = {
    // ALL USERS
    getAllSchoolClasses: async (params = {}, signal) => {
        const response = await client.get('/school-classes', { params, signal });
        return response.data;
    },

    getSchoolClassById: async (id) => {
        const response = await client.get(`/school-classes/${id}`);
        return response.data;
    },

    // ADMIN
    createSchoolClass: async (classData) => {
        const response = await client.post('/school-classes/admin', classData);
        return response.data;
    },

    updateSchoolClass: async (id, classData) => {
        const response = await client.put(`/school-classes/admin/${id}`, classData);
        return response.data;
    },

    deleteSchoolClass: async (id) => {
        await client.delete(`/school-classes/admin/${id}`);
    },

    enrollUser: async (classId, userId, role) => {
        await client.post(`/school-classes/admin/${classId}/enroll?userId=${userId}&role=${role}`);
    },

    removeEnrollment: async (classId, userId) => {
        await client.delete(`/school-classes/admin/${classId}/enroll/${userId}`);
    },
};
