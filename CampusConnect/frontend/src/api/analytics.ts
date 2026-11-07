import { apiClient } from './client';
import { CampusAnalyticsResponse } from '../types/analytics';

export const analyticsApi = {
  getOverview: async (): Promise<CampusAnalyticsResponse> => {
    const response = await apiClient.get<CampusAnalyticsResponse>('/analytics/overview');
    return response.data;
  },
};
