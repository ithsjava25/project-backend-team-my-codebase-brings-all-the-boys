import client from './client';

export const courseApi = {
    // ALL USERS
    // User specific courses
    getUsersCourses: async ({page = 0, size = 10} = {}) => {
        const response = await client.get(`/courses?page=${page}&size=${size}`);
        return response.data; // {content, totalPages, totalElements}
    },

    // Specific course
    getCourseById: async (id) => {
        const response = await client.get(`/courses/${id}`);
        return response.data;
    },


    // ADMIN
    getAllCourses: async ({page = 0, size = 10} = {}) => {
        const response = await client.get(`/admin/courses?page=${page}&size=${size}`);
        return response.data;
    },

    getCourseByIdAdmin: async (id) => {
        const response = await client.get(`/admin/courses/${id}`);
        return response.data;
    },

    createCourse: async (courseData) => {
        const response = await client.post('/courses', courseData);
        return response.data;
    },

    updateCourse: async (id, courseData) => {
        const response = await client.put(`/courses/${id}`, courseData);
        return response.data;
    },

    deleteCourse: async (id) => {
        await client.delete(`/courses/${id}`);
    },
};