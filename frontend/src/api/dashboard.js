import client from './client';

export const dashboardApi = {
  getStats: async () => {
    const response = await client.get('/dashboard/stats');
    return response.data;
  },
  getPendingSubmissions: async () => {
    const response = await client.get('/dashboard/pending-submissions');
    return response.data;
  },
  getUpcomingDeadlines: async () => {
    const response = await client.get('/dashboard/upcoming-deadlines');
    return response.data;
  }
};
