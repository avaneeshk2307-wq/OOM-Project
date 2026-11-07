package com.campusconnect.service;

import com.campusconnect.dto.*;
import com.campusconnect.entity.*;
import com.campusconnect.enums.EnrollmentStatus;
import com.campusconnect.exception.ResourceNotFoundException;
import com.campusconnect.event.GradePublishedEvent;
import com.campusconnect.patterns.strategy.GradingResult;
import com.campusconnect.patterns.strategy.GradingStrategy;
import com.campusconnect.patterns.strategy.GradingStrategyFactory;
import com.campusconnect.patterns.strategy.RelativeGradingStrategy;
import com.campusconnect.repository.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;
    private final AssessmentComponentRepository assessmentComponentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final GradingStrategy gradingStrategy;
    private final GradingStrategyFactory gradingStrategyFactory;
    private final RelativeGradingStrategy relativeGradingStrategy;
    private final ApplicationEventPublisher eventPublisher;

    public GradeServiceImpl(
            GradeRepository gradeRepository,
            AssessmentComponentRepository assessmentComponentRepository,
            EnrollmentRepository enrollmentRepository,
            CourseRepository courseRepository,
            StudentRepository studentRepository,
            @Qualifier("absoluteGradingStrategy") GradingStrategy gradingStrategy,
            GradingStrategyFactory gradingStrategyFactory,
            RelativeGradingStrategy relativeGradingStrategy,
            ApplicationEventPublisher eventPublisher
    ) {
        this.gradeRepository = gradeRepository;
        this.assessmentComponentRepository = assessmentComponentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.gradingStrategy = gradingStrategy;
        this.gradingStrategyFactory = gradingStrategyFactory;
        this.relativeGradingStrategy = relativeGradingStrategy;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public AssessmentResponse createAssessment(CreateAssessmentRequest request, String username) {
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.courseId()));

        AssessmentComponent component = new AssessmentComponent(
                course,
                request.name(),
                request.type(),
                request.maxMarks(),
                request.weightagePercentage()
        );

        course.addAssessmentComponent(component);
        AssessmentComponent saved = assessmentComponentRepository.save(component);

        return mapToAssessmentResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentResponse> getAssessmentsByCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        }
        return assessmentComponentRepository.findByCourseId(courseId)
                .stream()
                .map(this::mapToAssessmentResponse)
                .toList();
    }

    @Override
    public GradeResponse recordGrade(SubmitGradeRequest request, String username) {
        Enrollment enrollment = enrollmentRepository.findById(request.enrollmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + request.enrollmentId()));

        AssessmentComponent assessment = assessmentComponentRepository.findById(request.assessmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found with id: " + request.assessmentId()));

        if (!assessment.getCourse().getId().equals(enrollment.getCourse().getId())) {
            throw new IllegalArgumentException("Assessment does not belong to the enrolled course");
        }

        // Apply Strategy Pattern dynamically based on request mode
        GradingStrategy activeStrategy = request.gradingMode() != null
                ? gradingStrategyFactory.getStrategy(request.gradingMode())
                : gradingStrategy;

        GradingResult result = activeStrategy.calculateGrade(request.marksObtained(), assessment.getMaxMarks());

        Optional<Grade> existingOpt = gradeRepository.findByEnrollmentIdAndAssessmentId(enrollment.getId(), assessment.getId());
        Grade grade;
        if (existingOpt.isPresent()) {
            grade = existingOpt.get();
            grade.setMarks(request.marksObtained(), result.gradeLetter().getDisplay());
        } else {
            grade = new Grade(enrollment, assessment, request.marksObtained(), result.gradeLetter().getDisplay());
        }

        Grade saved = gradeRepository.save(grade);

        // Notify Observers via Spring Event Publisher
        eventPublisher.publishEvent(new GradePublishedEvent(
                enrollment.getStudent().getId(),
                assessment.getCourse().getId(),
                assessment.getCourse().getCode(),
                assessment.getName(),
                saved.getMarksObtained(),
                assessment.getMaxMarks(),
                saved.getLetterGrade()
        ));

        return mapToGradeResponse(saved, result);
    }

    @Override
    public List<GradeResponse> recordBatchGrades(List<SubmitGradeRequest> requests, String username) {
        List<GradeResponse> responses = new ArrayList<>();
        for (SubmitGradeRequest req : requests) {
            responses.add(recordGrade(req, username));
        }
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeResponse> getGradesByCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        }

        return gradeRepository.findByAssessmentCourseId(courseId).stream()
                .map(g -> {
                    GradingResult res = gradingStrategy.calculateGrade(g.getMarksObtained(), g.getAssessment().getMaxMarks());
                    return mapToGradeResponse(g, res);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeResponse> getGradesByStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }

        return gradeRepository.findByEnrollmentStudentId(studentId).stream()
                .map(g -> {
                    GradingResult res = gradingStrategy.calculateGrade(g.getMarksObtained(), g.getAssessment().getMaxMarks());
                    return mapToGradeResponse(g, res);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentCourseGradeReport> getStudentGradeReports(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        List<StudentCourseGradeReport> reports = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            if (enrollment.getStatus() == EnrollmentStatus.ENROLLED || enrollment.getStatus() == EnrollmentStatus.APPROVED) {
                Course course = enrollment.getCourse();
                List<Grade> grades = gradeRepository.findByEnrollmentId(enrollment.getId());

                List<GradeResponse> gradeResponses = new ArrayList<>();
                BigDecimal totalWeightedScore = BigDecimal.ZERO;

                for (Grade g : grades) {
                    GradingResult res = gradingStrategy.calculateGrade(g.getMarksObtained(), g.getAssessment().getMaxMarks());
                    gradeResponses.add(mapToGradeResponse(g, res));

                    // (marks / maxMarks) * weightage
                    BigDecimal componentScore = g.getMarksObtained()
                            .multiply(g.getAssessment().getWeightagePercentage())
                            .divide(g.getAssessment().getMaxMarks(), 2, RoundingMode.HALF_UP);
                    totalWeightedScore = totalWeightedScore.add(componentScore);
                }

                GradingResult overallResult = gradingStrategy.calculateGrade(totalWeightedScore, BigDecimal.valueOf(100));

                reports.add(new StudentCourseGradeReport(
                        course.getId(),
                        course.getCode(),
                        course.getTitle(),
                        course.getCredits(),
                        enrollment.getId(),
                        gradeResponses,
                        totalWeightedScore,
                        overallResult.gradeLetter().getDisplay(),
                        overallResult.gradePoint()
                ));
            }
        }

        return reports;
    }

    @Override
    @Transactional(readOnly = true)
    public CourseGradeDistributionResponse getCourseGradeDistribution(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        List<Grade> grades = gradeRepository.findByAssessmentCourseId(courseId);

        if (grades.isEmpty()) {
            return new CourseGradeDistributionResponse(
                    course.getId(), course.getCode(), course.getTitle(),
                    0, 0.0, 0.0, 0.0, 0.0,
                    Collections.emptyMap(), Collections.emptyMap()
            );
        }

        List<Double> percentages = grades.stream()
                .map(g -> g.getMarksObtained()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(g.getAssessment().getMaxMarks(), 2, RoundingMode.HALF_UP)
                        .doubleValue())
                .toList();

        double sum = percentages.stream().mapToDouble(Double::doubleValue).sum();
        double mean = sum / percentages.size();

        double variance = percentages.stream()
                .mapToDouble(p -> Math.pow(p - mean, 2))
                .sum() / percentages.size();
        double stdDev = Math.sqrt(variance);

        double maxScore = percentages.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        double minScore = percentages.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);

        Map<String, Long> absoluteCounts = new LinkedHashMap<>();
        Map<String, Long> relativeCounts = new LinkedHashMap<>();

        for (String letter : List.of("A+", "A", "B+", "B", "C+", "C", "D", "F")) {
            absoluteCounts.put(letter, 0L);
            relativeCounts.put(letter, 0L);
        }

        for (Grade g : grades) {
            GradingResult absResult = gradingStrategy.calculateGrade(g.getMarksObtained(), g.getAssessment().getMaxMarks());
            String absDisplay = absResult.gradeLetter().getDisplay();
            absoluteCounts.put(absDisplay, absoluteCounts.getOrDefault(absDisplay, 0L) + 1);

            GradingResult relResult = relativeGradingStrategy.calculateGradeWithCohort(g.getMarksObtained(), g.getAssessment().getMaxMarks(), mean, stdDev);
            String relDisplay = relResult.gradeLetter().getDisplay();
            relativeCounts.put(relDisplay, relativeCounts.getOrDefault(relDisplay, 0L) + 1);
        }

        return new CourseGradeDistributionResponse(
                course.getId(),
                course.getCode(),
                course.getTitle(),
                grades.size(),
                Math.round(mean * 100.0) / 100.0,
                Math.round(stdDev * 100.0) / 100.0,
                maxScore,
                minScore,
                absoluteCounts,
                relativeCounts
        );
    }

    private AssessmentResponse mapToAssessmentResponse(AssessmentComponent c) {
        return new AssessmentResponse(
                c.getId(),
                c.getCourse().getId(),
                c.getCourse().getCode(),
                c.getName(),
                c.getType(),
                c.getMaxMarks(),
                c.getWeightagePercentage(),
                c.getCreatedAt()
        );
    }

    private GradeResponse mapToGradeResponse(Grade g, GradingResult result) {
        return new GradeResponse(
                g.getId(),
                g.getEnrollment().getId(),
                g.getEnrollment().getStudent().getId(),
                g.getEnrollment().getStudent().getName(),
                g.getEnrollment().getStudent().getRollNumber(),
                g.getAssessment().getId(),
                g.getAssessment().getName(),
                g.getAssessment().getType(),
                g.getMarksObtained(),
                g.getAssessment().getMaxMarks(),
                g.getLetterGrade(),
                result.gradePoint(),
                result.remark(),
                g.getGradedAt()
        );
    }
}
