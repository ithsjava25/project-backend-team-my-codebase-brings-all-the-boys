import client from './client';

export const courseApi = {
  getAllCourses: async () => {
    const response = await client.get('/courses');
    return response.data;
  },

  getCourseById: async (id) => {
    const response = await client.get(`/courses/${id}`);
    return response.data;
  },
};