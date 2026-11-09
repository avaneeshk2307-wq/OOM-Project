import { apiClient } from './client';
import { NotificationItem } from '../types/notification';

export const notificationApi = {
  getMyNotifications: async (): Promise<NotificationItem[]> => {
    const response = await apiClient.get<NotificationItem[]>('/notifications');
    return response.data;
  },

  getUnreadCount: async (): Promise<number> => {
    const response = await apiClient.get<{ unreadCount: number }>('/notifications/unread-count');
    return response.data.unreadCount;
  },

  markAsRead: async (id: number): Promise<void> => {
    await apiClient.put(`/notifications/${id}/read`);
  },

  markAllAsRead: async (): Promise<void> => {
    await apiClient.put('/notifications/read-all');
  },
};

