import client from './client';

export const courseApi = {
    // ALL USERS
    // User specific courses
    getUsersCourses: async () => {
        const response = await client.get('/courses');
        return response.data;
    },

    // Specific course
    getCourseById: async (id) => {
        const response = await client.get(`/courses/${id}`);
        return response.data;
    },


    // ADMIN
    getAllCourses: async () => {
        const response = await client.get('/admin/courses');
        return response.data;
    },

    getCourseByIdAdmin: async (id) => {
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