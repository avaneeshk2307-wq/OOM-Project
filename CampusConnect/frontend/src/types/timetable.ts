export type DayOfWeek = 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY';

export type RoomType = 'LECTURE_HALL' | 'LABORATORY' | 'SEMINAR_ROOM' | 'AUDITORIUM';

export interface Classroom {
  id: number;
  roomNumber: string;
  building: string;
  capacity: number;
  roomType: RoomType;
  createdAt: string;
}

export interface CreateClassroomRequest {
  roomNumber: string;
  building: string;
  capacity: number;
  roomType: RoomType;
}

export interface TimetableEntry {
  id: number;
  courseId: number;
  courseCode: string;
  courseTitle: string;
  classroomId: number;
  roomNumber: string;
  building: string;
  classroomCapacity: number;
  facultyId: number;
  facultyName: string;
  dayOfWeek: DayOfWeek;
  startTime: string; // HH:mm
  endTime: string;   // HH:mm
  createdAt: string;
}

export interface CreateTimetableRequest {
  courseId: number;
  classroomId: number;
  facultyId?: number;
  dayOfWeek: DayOfWeek;
  startTime: string;
  endTime: string;
}
