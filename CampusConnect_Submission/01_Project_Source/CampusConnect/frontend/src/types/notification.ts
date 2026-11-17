export type NotificationType = 'GRADE_ALERT' | 'ATTENDANCE_SHORTAGE' | 'ENROLLMENT_UPDATE' | 'SYSTEM';

export interface NotificationItem {
  id: number;
  title: string;
  message: string;
  type: NotificationType;
  isRead: boolean;
  createdAt: string;
}

