package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.example.projectbackendteammycodebasebringsalltheboys.entity.FileMetadata;

import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Submission;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.UserAssignment;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.StudentAssignmentStatus;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.BadRequestException;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.FileMetadataRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.SubmissionRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAssignmentService {

    private final UserAssignmentRepository userAssignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final FileMetadataRepository fileMetadataRepository;
    private final ActivityLogService activityLogService;

    @Transactional
    public UserAssignment assignToStudent(Assignment assignment, User student, User assigner) {
        UserAssignment ua = new UserAssignment();
        ua.setAssignment(assignment);
        ua.setStudent(student);
        ua.setStatus(StudentAssignmentStatus.ASSIGNED);

        UserAssignment saved = userAssignmentRepository.save(ua);

        activityLogService.log(
                assigner,
                "ASSIGNED_CASE",
                "UserAssignment",
                saved.getId(),
                "Assigned case: " + assignment.getTitle() + " to student: " + student.getUsername());

        return saved;
    }

    @Transactional
    public void submitAssignment(UserAssignment ua) {
        submitWork(ua, "Automatic submission", List.of());
    }

    @Transactional
    public void submitWork(UserAssignment ua, String content, List<String> fileS3Keys) {
        boolean canSubmit =
                ua.getStatus() == StudentAssignmentStatus.ASSIGNED
                        || ua.getStatus() == StudentAssignmentStatus.TURNED_IN;

        if (!canSubmit) {
            throw new BadRequestException("Cannot submit assignment in status: " + ua.getStatus());
        }

        List<FileMetadata> filesToAttach = new java.util.ArrayList<>();

        for (String s3Key : fileS3Keys) {
            FileMetadata file =
                    fileMetadataRepository
                            .findByS3Key(s3Key)
                            .orElseThrow(
                                    () ->
                                            new BadRequestException(
                                                    "File not found for s3Key: " + s3Key));

            if (file.getUploader() == null || !file.getUploader().getId().equals(ua.getStudent().getId())) {
                throw new BadRequestException(
                        "File does not belong to the submitting student: " + s3Key);
            }

            if (file.getSubmission() != null) {
                throw new BadRequestException(
                        "File is already attached to another submission: " + s3Key);
            }

            filesToAttach.add(file);
        }

        Submission submission = new Submission();
        submission.setUserAssignment(ua);
        submission.setStudent(ua.getStudent());
        submission.setContent(content);
        submission.setSubmittedAt(LocalDateTime.now());
        Submission savedSubmission = submissionRepository.save(submission);

        for (FileMetadata file : filesToAttach) {
            file.setSubmission(savedSubmission);
            fileMetadataRepository.save(file);
        }

        if (ua.getStatus() == StudentAssignmentStatus.ASSIGNED) {
            ua.setStatus(StudentAssignmentStatus.TURNED_IN);
            ua.setTurnedInAt(LocalDateTime.now());
            userAssignmentRepository.save(ua);
        }

        activityLogService.log(
                ua.getStudent(),
                "SUBMITTED_ASSIGNMENT",
                "UserAssignment",
                ua.getId(),
                "Student turned in assignment: " + ua.getAssignment().getTitle());
    }

    @Transactional
    public void evaluateAssignment(UserAssignment ua, String grade, String feedback, User evaluator) {
        if (ua.getStatus() != StudentAssignmentStatus.TURNED_IN) {
            throw new BadRequestException("Cannot evaluate assignment in status: " + ua.getStatus());
        }
        ua.setStatus(StudentAssignmentStatus.EVALUATED);
        ua.setGrade(grade);
        ua.setFeedback(feedback);
        userAssignmentRepository.save(ua);

        activityLogService.log(
                evaluator,
                "EVALUATED_ASSIGNMENT",
                "UserAssignment",
                ua.getId(),
                "Teacher evaluated assignment with grade: " + grade);
    }

    @Transactional(readOnly = true)
    public List<UserAssignment> getAssignmentsForStudent(User student) {
        return userAssignmentRepository.findByStudent(student);
    }

    @Transactional(readOnly = true)
    public Optional<UserAssignment> getByAssignmentAndStudent(Assignment assignment, User student) {
        return userAssignmentRepository.findByAssignmentAndStudent(assignment, student);
    }
}
