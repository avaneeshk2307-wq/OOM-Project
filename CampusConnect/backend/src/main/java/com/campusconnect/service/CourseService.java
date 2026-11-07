package com.campusconnect.service;

import com.campusconnect.dto.CourseRequest;
import com.campusconnect.dto.CourseResponse;
import java.util.List;

public interface CourseService {
    CourseResponse createCourse(CourseRequest request);
    CourseResponse getCourseById(Long id);
    List<CourseResponse> getAllCourses();
    List<CourseResponse> getCoursesByDepartment(Long departmentId);
    List<CourseResponse> getCoursesByFaculty(Long facultyId);
    CourseResponse updateCourse(Long id, CourseRequest request);
    CourseResponse assignFaculty(Long courseId, Long facultyId);
    void deleteCourse(Long id);
}
