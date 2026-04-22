import client from './client';

export const schoolClassApi = {
    // ALL USERS
    getAllSchoolClasses: async () => {
        const response = await client.get('/school-classes');
        return response.data;
    },

    getSchoolClassById: async (id) => {
        const response = await client.get(`/school-classes/${id}`);
        return response.data;
    },

    // ADMIN
    createSchoolClass: async (classData) => {
        const response = await client.post('/admin/school-classes', classData);
        return response.data;
    },

    updateSchoolClass: async (id, classData) => {
        const response = await client.put(`/admin/school-classes/${id}`, classData);
        return response.data;
    },

    deleteSchoolClass: async (id) => {
        await client.delete(`/admin/school-classes/${id}`);
    },

    enrollUser: async (classId, userId, role) => {
        await client.post(`/admin/school-classes/${classId}/enroll?userId=${userId}&role=${role}`);
    },

    removeEnrollment: async (classId, userId) => {
        await client.delete(`/admin/school-classes/${classId}/enroll/${userId}`);
    },
};
