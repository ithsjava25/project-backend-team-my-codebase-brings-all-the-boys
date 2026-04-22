import client from './client';

export const schoolClass = {
    getAllSchoolClasses: async () => {
        const response = await client.get('/school-classes');
        return response.data;
    },

    getSchoolClassById: async (id) => {
        const response = await client.get(`/school-classes/${id}`);
        return response.data;
    },
};