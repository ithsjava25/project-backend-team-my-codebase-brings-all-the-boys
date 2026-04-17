import client from './client';

export const courseApi = {
    getAllCourses: async ({page = 0, size = 10} = {}) => {
        const response = await client.get(`/admin/courses?page=${page}&size=${size}`);
        return response.data;
    },

    getCourseById: async (id) => {
        const response = await client.get(`/admin/courses/${id}`);
        return response.data;
    },

    createCourse: async (courseData) => {
        const response = await client.post('/admin/courses', courseData);
        return response.data;
    },

    updateCourse: async (id, courseData) => {
        const response = await client.put(`/admin/courses/${id}`, courseData);
        return response.data;
    },

    deleteCourse: async (id) => {
        await client.delete(`/admin/courses/${id}`);
    },
};