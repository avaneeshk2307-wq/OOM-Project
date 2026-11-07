package com.campusconnect.service;

import com.campusconnect.entity.Enrollment;
import com.campusconnect.entity.Student;
import com.campusconnect.exception.ResourceNotFoundException;
import com.campusconnect.patterns.builder.AcademicTranscriptReport;
import com.campusconnect.patterns.builder.ConcreteTranscriptBuilder;
import com.campusconnect.patterns.builder.TranscriptDirector;
import com.campusconnect.patterns.strategy.GradingStrategy;
import com.campusconnect.repository.EnrollmentRepository;
import com.campusconnect.repository.GradeRepository;
import com.campusconnect.repository.StudentRepository;
import com.campusconnect.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TranscriptService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepository;
    private final TranscriptDirector transcriptDirector;
    private final GradingStrategy gradingStrategy;
    private final UserAccountRepository userAccountRepository;

    public TranscriptService(
            StudentRepository studentRepository,
            EnrollmentRepository enrollmentRepository,
            GradeRepository gradeRepository,
            TranscriptDirector transcriptDirector,
            @Qualifier("absoluteGradingStrategy") GradingStrategy gradingStrategy,
            UserAccountRepository userAccountRepository
    ) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.gradeRepository = gradeRepository;
        this.transcriptDirector = transcriptDirector;
        this.gradingStrategy = gradingStrategy;
        this.userAccountRepository = userAccountRepository;
    }

    public AcademicTranscriptReport generateTranscriptForStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

        // Employ GoF Builder Pattern
        ConcreteTranscriptBuilder builder = new ConcreteTranscriptBuilder();
        return transcriptDirector.constructTranscript(builder, student, enrollments, gradeRepository, gradingStrategy);
    }

    public AcademicTranscriptReport generateTranscriptForUsername(String username) {
        Long studentId = userAccountRepository.findByUsername(username)
                .map(acc -> acc.getPerson().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        return generateTranscriptForStudent(studentId);
    }
}
