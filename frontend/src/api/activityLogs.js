import client from './client';

export const activityLogApi = {
  getAllLogs: async (page = 0, size = 20) => {
    const response = await client.get(`/activity-logs?page=${page}&size=${size}`);
    return response.data;
  },

  getUserLogs: async (userId, page = 0, size = 20) => {
    const response = await client.get(`/activity-logs/user/${userId}?page=${page}&size=${size}`);
    return response.data;
  },

  getEntityLogs: async (entityType, entityId, page = 0, size = 20) => {
    const response = await client.get(`/activity-logs/entity/${entityType}/${entityId}?page=${page}&size=${size}`);
    return response.data;
  }
};