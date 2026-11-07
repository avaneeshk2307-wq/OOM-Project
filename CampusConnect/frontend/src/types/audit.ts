export type AuditAction =
  | 'CREATE_STUDENT'
  | 'UPDATE_STUDENT'
  | 'PROMOTE_STUDENT'
  | 'CREATE_FACULTY'
  | 'UPDATE_FACULTY'
  | 'CREATE_COURSE'
  | 'UPDATE_COURSE'
  | 'ASSIGN_FACULTY'
  | 'CREATE_DEPARTMENT'
  | 'SCHEDULE_TIMETABLE'
  | 'DELETE_TIMETABLE'
  | 'ENROLLMENT_STATUS_CHANGE'
  | 'SUBMIT_GRADE'
  | 'RECORD_ATTENDANCE'
  | 'POST_ANNOUNCEMENT'
  | 'SECURITY_EVENT';

export interface AuditLogResponse {
  id: number;
  action: AuditAction;
  entityName: string;
  entityId?: string;
  performedByUsername: string;
  details?: string;
  ipAddress?: string;
  createdAt: string;
}

export interface PaginatedAuditLogs {
  content: AuditLogResponse[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}
