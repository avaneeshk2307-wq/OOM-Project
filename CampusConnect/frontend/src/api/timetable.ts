import { apiClient } from './client';
import { Classroom, CreateClassroomRequest, CreateTimetableRequest, TimetableEntry } from '../types/timetable';

export const timetableApi = {
  // Timetables
  getAllTimetables: async (): Promise<TimetableEntry[]> => {
    const response = await apiClient.get<TimetableEntry[]>('/timetable');
    return response.data;
  },

  getMyTimetable: async (): Promise<TimetableEntry[]> => {
    const response = await apiClient.get<TimetableEntry[]>('/timetable/my-timetable');
    return response.data;
  },

  getTimetableByCourse: async (courseId: number): Promise<TimetableEntry[]> => {
    const response = await apiClient.get<TimetableEntry[]>(`/timetable/course/${courseId}`);
    return response.data;
  },

  getTimetableByFaculty: async (facultyId: number): Promise<TimetableEntry[]> => {
    const response = await apiClient.get<TimetableEntry[]>(`/timetable/faculty/${facultyId}`);
    return response.data;
  },

  createTimetableEntry: async (data: CreateTimetableRequest): Promise<TimetableEntry> => {
    const response = await apiClient.post<TimetableEntry>('/timetable', data);
    return response.data;
  },

  deleteTimetableEntry: async (id: number): Promise<void> => {
    await apiClient.delete(`/timetable/${id}`);
  },

  // Classrooms
  getClassrooms: async (): Promise<Classroom[]> => {
    const response = await apiClient.get<Classroom[]>('/timetable/classrooms');
    return response.data;
  },

  createClassroom: async (data: CreateClassroomRequest): Promise<Classroom> => {
    const response = await apiClient.post<Classroom>('/timetable/classrooms', data);
    return response.data;
  },

  deleteClassroom: async (id: number): Promise<void> => {
    await apiClient.delete(`/timetable/classrooms/${id}`);
  },
};

