import { apiClient } from './client';
import { AcademicTranscriptReport } from '../types/transcript';

export const transcriptApi = {
  getMyTranscript: async (): Promise<AcademicTranscriptReport> => {
    const response = await apiClient.get<AcademicTranscriptReport>('/transcripts/my-transcript');
    return response.data;
  },

  getStudentTranscript: async (studentId: number): Promise<AcademicTranscriptReport> => {
    const response = await apiClient.get<AcademicTranscriptReport>(`/transcripts/student/${studentId}`);
    return response.data;
  },
};
