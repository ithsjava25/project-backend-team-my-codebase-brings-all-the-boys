import client from './client';

export const userApi = {
    getAllUsers: async ({page = 0, size = 10} = {}) => {
        const response = await client.get(`/admin/users?page=${page}&size=${size}`);
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
};
