import client from './client';

export const activityLogApi = {
    getAllLogs: async (page = 0, size = 10, filters = {}, signal = undefined) => {
        const params = new URLSearchParams({ page, size });
        Object.entries(filters).forEach(([key, value]) => {
            if (value) params.append(key, value);
        });
        const response = await client.get(`/activity-logs?${params.toString()}`, { signal });
        return response.data;
    },

    getUserLogs: async (userId, page = 0, size = 10, signal = undefined) => {
        const response = await client.get(`/activity-logs/user/${userId}?page=${page}&size=${size}`, { signal });
        return response.data;
    },

    getEntityLogs: async (entityType, entityId, page = 0, size = 10, signal = undefined) => {
        const response = await client.get(`/activity-logs/entity/${entityType}/${entityId}?page=${page}&size=${size}`, { signal });
        return response.data;
    }
};