package com.campusconnect.service;

import com.campusconnect.dto.DepartmentRequest;
import com.campusconnect.dto.DepartmentResponse;
import com.campusconnect.entity.Department;
import com.campusconnect.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByCode(request.code().trim().toUpperCase())) {
            throw new IllegalArgumentException("Department with code '" + request.code() + "' already exists.");
        }

        Department department = new Department(
                request.code(),
                request.name(),
                request.description()
        );

        Department saved = departmentRepository.save(department);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + id));
        return mapToResponse(dept);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + id));

        dept.updateDetails(request.name(), request.description());
        Department updated = departmentRepository.save(dept);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + id));

        if (!dept.getStudents().isEmpty() || !dept.getFacultyMembers().isEmpty() || !dept.getCourses().isEmpty()) {
            throw new IllegalStateException("Cannot delete department with active students, faculty, or curriculum courses.");
        }

        departmentRepository.delete(dept);
    }

    private DepartmentResponse mapToResponse(Department d) {
        return new DepartmentResponse(
                d.getId(),
                d.getCode(),
                d.getName(),
                d.getDescription(),
                d.getStudents() != null ? d.getStudents().size() : 0,
                d.getFacultyMembers() != null ? d.getFacultyMembers().size() : 0,
                d.getCourses() != null ? d.getCourses().size() : 0,
                d.getCreatedAt()
        );
    }
}
