package org.example.projectbackendteammycodebasebringsalltheboys.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.FileMetadata;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Submission;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.UserAssignment;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.StudentAssignmentStatus;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.BadRequestException;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.FileMetadataRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.SubmissionRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserAssignmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserAssignmentServiceTest {

  @Mock private UserAssignmentRepository userAssignmentRepository;
  @Mock private SubmissionRepository submissionRepository;
  @Mock private FileMetadataRepository fileMetadataRepository;
  @Mock private ActivityLogService activityLogService;

  private UserAssignmentService userAssignmentService;

  @BeforeEach
  void setUp() {
    userAssignmentService =
        new UserAssignmentService(
            userAssignmentRepository,
            submissionRepository,
            fileMetadataRepository,
            activityLogService);
  }

  @Test
  @DisplayName("submitWork throws BadRequestException if status not ASSIGNED or TURNED_IN")
  void submitWork_invalidStatus_throwsException() {
    UserAssignment ua = new UserAssignment();
    ua.setStatus(StudentAssignmentStatus.EVALUATED);

    assertThatThrownBy(() -> userAssignmentService.submitWork(ua, "content", List.of()))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Cannot submit");
  }

  @Test
  @DisplayName("submitWork creates submission and updates status")
  void submitWork_valid_createsSubmission() {
    User student = new User();
    student.setId(UUID.randomUUID());
    Assignment assignment = new Assignment();
    assignment.setId(UUID.randomUUID());
    assignment.setTitle("Test Task");
    UserAssignment ua = new UserAssignment();
    ua.setStudent(student);
    ua.setAssignment(assignment);
    ua.setStatus(StudentAssignmentStatus.ASSIGNED);

    when(submissionRepository.save(any(Submission.class))).thenAnswer(inv -> inv.getArgument(0));

    userAssignmentService.submitWork(ua, "I did it", List.of());

    assertThat(ua.getStatus()).isEqualTo(StudentAssignmentStatus.TURNED_IN);
    assertThat(ua.getTurnedInAt()).isNotNull();
    verify(submissionRepository).save(any(Submission.class));
    verify(userAssignmentRepository).save(ua);
    verify(activityLogService).log(any(), any(), any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("submitWork attaches files and validates ownership")
  void submitWork_withFiles_attachesFiles() {
    User student = new User();
    student.setId(UUID.randomUUID());
    Assignment assignment = new Assignment();
    assignment.setId(UUID.randomUUID());
    UserAssignment ua = new UserAssignment();
    ua.setStudent(student);
    ua.setAssignment(assignment);
    ua.setStatus(StudentAssignmentStatus.ASSIGNED);

    FileMetadata file = new FileMetadata();
    file.setS3Key("key1");
    file.setUploader(student);

    when(fileMetadataRepository.findByS3Key("key1")).thenReturn(Optional.of(file));
    when(submissionRepository.save(any(Submission.class))).thenAnswer(inv -> inv.getArgument(0));

    userAssignmentService.submitWork(ua, "content", List.of("key1"));

    assertThat(file.getSubmission()).isNotNull();
    verify(fileMetadataRepository).save(file);
  }

  @Test
  @DisplayName("submitWork throws BadRequestException if file uploader mismatch")
  void submitWork_fileOwnerMismatch_throwsException() {
    User student = new User();
    student.setId(UUID.randomUUID());
    User other = new User();
    other.setId(UUID.randomUUID());
    UserAssignment ua = new UserAssignment();
    ua.setStudent(student);
    ua.setStatus(StudentAssignmentStatus.ASSIGNED);

    FileMetadata file = new FileMetadata();
    file.setUploader(other);

    when(fileMetadataRepository.findByS3Key("key1")).thenReturn(Optional.of(file));

    assertThatThrownBy(() -> userAssignmentService.submitWork(ua, "content", List.of("key1")))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("File does not belong");
  }

  @Test
  @DisplayName("evaluateAssignment updates grade and feedback")
  void evaluateAssignment_valid_updatesUa() {
    UserAssignment ua = new UserAssignment();
    ua.setStatus(StudentAssignmentStatus.TURNED_IN);

    userAssignmentService.evaluateAssignment(ua, "A", "Great job", new User());

    assertThat(ua.getStatus()).isEqualTo(StudentAssignmentStatus.EVALUATED);
    assertThat(ua.getGrade()).isEqualTo("A");
    assertThat(ua.getFeedback()).isEqualTo("Great job");
    verify(userAssignmentRepository).save(ua);
  }
}
