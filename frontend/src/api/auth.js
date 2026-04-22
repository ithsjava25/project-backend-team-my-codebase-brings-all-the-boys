import client from './client';

export const auth = {
    register: async (registrationData) => {
        const response = await client.post('/auth/register', registrationData);
        return response.data;
    },

    login: async (username, password) => {
        const response = await client.post('/auth/login', { username, password });
        return response.data;
    },

    getCurrentUser: async () => {
        const response = await client.get('/auth/me');
        return response.data;
    },
};