package com.campusconnect.service;

import com.campusconnect.dto.CreateFacultyRequest;
import com.campusconnect.dto.FacultyResponse;
import com.campusconnect.dto.UpdateFacultyRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface FacultyService {
    FacultyResponse createFaculty(CreateFacultyRequest request);
    FacultyResponse getFacultyById(Long id);
    List<FacultyResponse> getAllFaculty();
    Page<FacultyResponse> getAllFacultyPaged(Pageable pageable);
    List<FacultyResponse> getFacultyByDepartment(Long departmentId);
    FacultyResponse updateFaculty(Long id, UpdateFacultyRequest request);
    void deleteFaculty(Long id);
}
