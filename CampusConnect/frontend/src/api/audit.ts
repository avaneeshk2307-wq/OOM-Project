import { apiClient } from './client';
import { AuditLogResponse, PaginatedAuditLogs } from '../types/audit';

export const auditApi = {
  getAuditLogs: async (action?: string, page = 0, size = 20): Promise<PaginatedAuditLogs> => {
    const params = new URLSearchParams();
    if (action) params.append('action', action);
    params.append('page', page.toString());
    params.append('size', size.toString());

    const response = await apiClient.get<PaginatedAuditLogs>(`/admin/audit-logs?${params.toString()}`);
    return response.data;
  },

  getRecentLogs: async (): Promise<AuditLogResponse[]> => {
    const response = await apiClient.get<AuditLogResponse[]>('/admin/audit-logs/recent');
    return response.data;
  }
};

