package com.campusconnect.service;

import com.campusconnect.dto.CreateFacultyRequest;
import com.campusconnect.dto.FacultyResponse;
import com.campusconnect.dto.UpdateFacultyRequest;
import java.util.List;

public interface FacultyService {
    FacultyResponse createFaculty(CreateFacultyRequest request);
    FacultyResponse getFacultyById(Long id);
    List<FacultyResponse> getAllFaculty();
    List<FacultyResponse> getFacultyByDepartment(Long departmentId);
    FacultyResponse updateFaculty(Long id, UpdateFacultyRequest request);
    void deleteFaculty(Long id);
}
