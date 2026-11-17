export interface Announcement {
  id: number;
  title: string;
  content: string;
  targetRole: string | null;
  courseId: number | null;
  courseCode: string | null;
  authorName: string;
  createdAt: string;
}

export interface CreateAnnouncementRequest {
  title: string;
  content: string;
  targetRole: string | null;
  courseId: number | null;
}
