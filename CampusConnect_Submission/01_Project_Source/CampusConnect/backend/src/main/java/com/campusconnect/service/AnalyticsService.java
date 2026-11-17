package com.campusconnect.service;

import com.campusconnect.dto.CampusAnalyticsResponse;
import com.campusconnect.entity.*;
import com.campusconnect.enums.AttendanceStatus;
import com.campusconnect.enums.EnrollmentStatus;
import com.campusconnect.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final DepartmentRepository departmentRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final GradeRepository gradeRepository;
    private final TimetableRepository timetableRepository;

    public AnalyticsService(
            DepartmentRepository departmentRepository,
            StudentRepository studentRepository,
            FacultyRepository facultyRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            AttendanceRecordRepository attendanceRecordRepository,
            GradeRepository gradeRepository,
            TimetableRepository timetableRepository
    ) {
        this.departmentRepository = departmentRepository;
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.gradeRepository = gradeRepository;
        this.timetableRepository = timetableRepository;
    }

    public CampusAnalyticsResponse getCampusAnalytics() {
        // 1. Department Metrics
        List<Department> departments = departmentRepository.findAll();
        List<CampusAnalyticsResponse.DepartmentMetric> departmentMetrics = departments.stream().map(d -> {
            long students = studentRepository.findByDepartmentId(d.getId()).size();
            long faculties = facultyRepository.findByDepartmentId(d.getId()).size();
            long courses = courseRepository.findByDepartmentId(d.getId()).size();
            return new CampusAnalyticsResponse.DepartmentMetric(d.getId(), d.getCode(), d.getName(), students, faculties, courses);
        }).toList();

        // 2. Course Capacity Metrics
        List<Course> courses = courseRepository.findAll();
        List<CampusAnalyticsResponse.CourseCapacityMetric> courseMetrics = courses.stream().map(c -> {
            long enrolled = enrollmentRepository.countByCourseIdAndStatus(c.getId(), EnrollmentStatus.ENROLLED);
            double pct = c.getCapacity() > 0 ? Math.round(((double) enrolled / c.getCapacity()) * 1000.0) / 10.0 : 0.0;
            return new CampusAnalyticsResponse.CourseCapacityMetric(c.getId(), c.getCode(), c.getTitle(), enrolled, c.getCapacity(), pct);
        }).toList();

        // 3. Attendance Health
        List<AttendanceRecord> records = attendanceRecordRepository.findAll();
        long totalRecords = records.size();
        long presentRecords = records.stream()
                .filter(r -> r.getStatus() == AttendanceStatus.PRESENT || r.getStatus() == AttendanceStatus.LATE)
                .count();
        double campusWidePercentage = totalRecords > 0
                ? Math.round(((double) presentRecords / totalRecords) * 1000.0) / 10.0
                : 100.0;

        List<Student> students = studentRepository.findAll();
        long shortageCount = 0;
        long goodStandingCount = 0;

        for (Student s : students) {
            List<Enrollment> enrollments = enrollmentRepository.findByStudentId(s.getId());
            boolean hasShortage = false;
            for (Enrollment e : enrollments) {
                long totalSess = attendanceRecordRepository.countTotalByCourseAndStudent(e.getCourse().getId(), s.getId());
                long attendedSess = attendanceRecordRepository.countAttendedByCourseAndStudent(e.getCourse().getId(), s.getId());
                if (totalSess > 0) {
                    double p = ((double) attendedSess / totalSess) * 100.0;
                    if (p < 75.0) {
                        hasShortage = true;
                        break;
                    }
                }
            }
            if (hasShortage) {
                shortageCount++;
            } else {
                goodStandingCount++;
            }
        }

        CampusAnalyticsResponse.AttendanceHealthMetric attendanceHealth = new CampusAnalyticsResponse.AttendanceHealthMetric(
                totalRecords, presentRecords, campusWidePercentage, goodStandingCount, shortageCount
        );

        // 4. Grade Distribution
        List<Grade> grades = gradeRepository.findAll();
        Map<String, Long> gradeDist = new LinkedHashMap<>();
        for (String letter : List.of("A+", "A", "B+", "B", "C+", "C", "D", "F")) {
            gradeDist.put(letter, 0L);
        }
        for (Grade g : grades) {
            String l = g.getLetterGrade();
            if (l != null && gradeDist.containsKey(l)) {
                gradeDist.put(l, gradeDist.get(l) + 1);
            }
        }

        // 5. Faculty Workload
        List<Faculty> faculties = facultyRepository.findAll();
        List<CampusAnalyticsResponse.FacultyWorkloadMetric> workloadMetrics = faculties.stream().map(f -> {
            List<Course> taughtCourses = courseRepository.findByInstructorId(f.getId());
            int credits = taughtCourses.stream().mapToInt(Course::getCredits).sum();
            long slots = timetableRepository.findByFacultyId(f.getId()).size();
            String deptCode = f.getDepartment() != null ? f.getDepartment().getCode() : "N/A";
            return new CampusAnalyticsResponse.FacultyWorkloadMetric(
                    f.getId(), f.getName(), deptCode, taughtCourses.size(), credits, slots
            );
        }).toList();

        return new CampusAnalyticsResponse(
                departmentMetrics,
                courseMetrics,
                attendanceHealth,
                gradeDist,
                workloadMetrics
        );
    }
}

