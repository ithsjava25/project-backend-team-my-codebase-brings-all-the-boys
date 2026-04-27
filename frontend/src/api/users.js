import client from './client';

export const userApi = {
    getAllUsers: async ({page = 0, size = 10, search = '', role = ''} = {}, signal = undefined) => {
        const params = new URLSearchParams({page, size});
        if (search) params.append('search', search);
        if (role) params.append('role', role);

        const response = await client.get(`/admin/users?${params.toString()}`, {signal});
        return response.data;
    },

    getUserById: async (id) => {
        const response = await client.get(`/admin/users/${id}`);
        return response.data;
    },

    createUser: async (userData) => {
        const response = await client.post('/admin/users', userData);
        return response.data;
    },

    updateUser: async (id, userData) => {
        const response = await client.put(`/admin/users/${id}`, userData);
        return response.data;
    },

    deleteUser: async (id) => {
        await client.delete(`/admin/users/${id}`);
    },

    getUserProfile: async (id) => {
        const response = await client.get(`/users/profile/${id}`);
        return response.data;
    },

    getTeachers: async () => {
        const response = await client.get('/users/teachers');
        return response.data;
    },

    getStudents: async (signal) => {
        const response = await client.get('/users/students', {signal});
        return response.data;
    },

    updateUserProfile: async (userData) => {
        const response = await client.put('/users/profile', userData);
        return response.data;
    },
};
