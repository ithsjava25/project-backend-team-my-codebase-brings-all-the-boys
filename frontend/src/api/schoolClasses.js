import client from './client';

export const schoolClassApi = {
    // ALL USERS
    getAllSchoolClasses: async (params = {}, signal) => {
        // Compatibility: handle if first arg is an AbortSignal
        let effectiveParams = params;
        let effectiveSignal = signal;
        if (params instanceof AbortSignal) {
            effectiveSignal = params;
            effectiveParams = {};
        }
        const response = await client.get('/school-classes', { 
            params: effectiveParams, 
            signal: effectiveSignal 
        });
        return response.data;
    },

    getSchoolClassById: async (id, signal) => {
        const response = await client.get(`/school-classes/${id}`, { signal });
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
        await client.post(`/school-classes/admin/${classId}/enroll`, null, {
            params: { userId, role }
        });
    },

    removeEnrollment: async (classId, userId) => {
        await client.delete(`/school-classes/admin/${classId}/enroll/${userId}`);
    },
};
