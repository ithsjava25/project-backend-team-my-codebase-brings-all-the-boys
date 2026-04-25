package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.annotation.LogActivity;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.FileMetadata;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Submission;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.UserAssignment;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityStatus;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.StudentAssignmentStatus;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.BadRequestException;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.FileMetadataRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.SubmissionRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserAssignmentRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAssignmentService {

  private final UserAssignmentRepository userAssignmentRepository;
  private final SubmissionRepository submissionRepository;
  private final FileMetadataRepository fileMetadataRepository;
  private final ActivityLogService activityLogService;
  private final AuthorizationService authorizationService;

  @LogActivity(
      action = ActivityAction.ASSIGNED,
      entityType = EntityType.USER_ASSIGNMENT,
      parentIdParamIndex = 0,
      actorParamIndex = 2)
  @Transactional
  public UserAssignment assignToStudent(Assignment assignment, User student, User assigner) {
    UserAssignment ua = new UserAssignment();
    ua.setAssignment(assignment);
    ua.setStudent(student);
    ua.setStatus(StudentAssignmentStatus.ASSIGNED);

    return userAssignmentRepository.save(ua);
  }

  @Transactional
  public void submitAssignment(UserAssignment ua) {
    submitWork(ua, "Automatic submission", List.of());
  }

  @Transactional
  public void submitWork(UserAssignment ua, String content, List<String> fileS3Keys) {
    if (ua.getAssignment().getDeadline() != null
        && LocalDateTime.now().isAfter(ua.getAssignment().getDeadline())) {
      throw new BadRequestException("The deadline for this assignment has passed.");
    }

    if (ua.getAssignment().getCourse() != null
        && ua.getAssignment().getCourse().getEndDate() != null
        && LocalDateTime.now().isAfter(ua.getAssignment().getCourse().getEndDate())) {
      throw new BadRequestException("The course for this assignment has ended.");
    }

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
              .orElseThrow(() -> new BadRequestException("File not found for s3Key: " + s3Key));

      if (file.getUploader() == null
          || !file.getUploader().getId().equals(ua.getStudent().getId())) {
        throw new BadRequestException("File does not belong to the submitting student: " + s3Key);
      }

      if (file.getSubmission() != null) {
        throw new BadRequestException("File is already attached to another submission: " + s3Key);
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

    Map<String, Object> submitDetails = new LinkedHashMap<>();
    submitDetails.put("assignmentTitle", ua.getAssignment().getTitle());
    submitDetails.put("fileCount", fileS3Keys.size());
    activityLogService.log(
        ua.getStudent(),
        ua.getAssignment().getId(),
        ActivityAction.ADDED,
        EntityType.SUBMISSION,
        null,
        submitDetails,
        ActivityStatus.SUCCESS);
  }

  @Transactional
  public void evaluateAssignment(UserAssignment ua, String grade, String feedback, User evaluator) {
    if (ua.getStatus() != StudentAssignmentStatus.TURNED_IN
        && ua.getStatus() != StudentAssignmentStatus.EVALUATED) {
      throw new IllegalStateException(
          "Cannot evaluate assignment in status: "
              + ua.getStatus()
              + ". Must be TURNED_IN or EVALUATED (for re-grading).");
    }

    StudentAssignmentStatus priorStatus = ua.getStatus();
    ua.setStatus(StudentAssignmentStatus.EVALUATED);
    ua.setGrade(grade);
    ua.setFeedback(feedback);
    userAssignmentRepository.save(ua);

    // Explicitly log the action to distinguish re-evaluation
    activityLogService.log(
        evaluator,
        ua.getAssignment().getId(),
        priorStatus == StudentAssignmentStatus.EVALUATED
            ? ActivityAction.RE_EVALUATED
            : ActivityAction.EVALUATED,
        EntityType.USER_ASSIGNMENT,
        ua.getId(),
        Map.of("grade", grade),
        ActivityStatus.SUCCESS);
  }

  @Transactional(readOnly = true)
  public List<UserAssignment> getAssignmentsForStudent(User student) {
    return userAssignmentRepository.findByStudent(student);
  }

  @Transactional(readOnly = true)
  public org.springframework.data.domain.Page<UserAssignment> getEvaluatedAssignmentsForTeacher(
      User user, Pageable pageable) {
    if (user.getRole() != null && "ROLE_ADMIN".equals(user.getRole().getName())) {
      return userAssignmentRepository.findByStatus(StudentAssignmentStatus.EVALUATED, pageable);
    }
    return userAssignmentRepository.findByStatusAndTeacherConnection(
        StudentAssignmentStatus.EVALUATED, user.getId(), pageable);
  }

  @Transactional(readOnly = true)
  public Optional<UserAssignment> getByAssignmentAndStudent(Assignment assignment, User student) {
    return userAssignmentRepository.findByAssignmentAndStudent(assignment, student);
  }

  @Transactional
  public UserAssignment getOrCreateForStudent(Assignment assignment, User student) {
    Optional<UserAssignment> existing =
        userAssignmentRepository.findByAssignmentAndStudent(assignment, student);

    if (existing.isPresent()) {
      return existing.get();
    }

    // Lazy create if authorized student
    if (student.getRole() != null
        && "ROLE_STUDENT".equals(student.getRole().getName())
        && authorizationService.canViewAssignment(student, assignment)) {
      UserAssignment ua = new UserAssignment();
      ua.setAssignment(assignment);
      ua.setStudent(student);
      ua.setStatus(StudentAssignmentStatus.ASSIGNED);
      return userAssignmentRepository.save(ua);
    }

    throw new org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException(
        "UserAssignment not found and could not be created for user: " + student.getUsername());
  }

  @Transactional(readOnly = true)
  public Optional<UserAssignment> getById(UUID id) {
    return userAssignmentRepository.findById(id);
  }
}
