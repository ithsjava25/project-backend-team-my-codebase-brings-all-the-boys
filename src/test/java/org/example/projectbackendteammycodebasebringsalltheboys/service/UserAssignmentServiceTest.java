package org.example.projectbackendteammycodebasebringsalltheboys.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
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
  @Mock private AuthorizationService authorizationService;

  private UserAssignmentService userAssignmentService;

  @BeforeEach
  void setUp() {
    userAssignmentService =
        new UserAssignmentService(
            userAssignmentRepository,
            submissionRepository,
            fileMetadataRepository,
            activityLogService,
            authorizationService);
  }

  @Test
  @DisplayName("submitWork throws BadRequestException if status not ASSIGNED or TURNED_IN")
  void submitWork_invalidStatus_throwsException() {
    Assignment assignment = new Assignment();
    assignment.setDeadline(LocalDateTime.now().plusDays(1));

    UserAssignment ua = new UserAssignment();
    ua.setStatus(StudentAssignmentStatus.EVALUATED);
    ua.setAssignment(assignment);

    assertThatThrownBy(() -> userAssignmentService.submitWork(ua, "content", List.of()))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Cannot submit");
  }

  @Test
  @DisplayName("submitWork creates submission and updates status")
  void submitWork_valid_createsSubmission() {
    User student = new User();
    LocalDateTime deadline = LocalDateTime.now().plusDays(1);
    student.setId(UUID.randomUUID());
    Assignment assignment = new Assignment();
    assignment.setId(UUID.randomUUID());
    assignment.setTitle("Test Task");
    assignment.setDeadline(deadline);
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
    Assignment assignment = new Assignment();
    assignment.setDeadline(LocalDateTime.now().plusDays(1));
    User student = new User();
    student.setId(UUID.randomUUID());
    User other = new User();
    other.setId(UUID.randomUUID());
    UserAssignment ua = new UserAssignment();
    ua.setStudent(student);
    ua.setStatus(StudentAssignmentStatus.ASSIGNED);
    ua.setAssignment(assignment);

    FileMetadata file = new FileMetadata();
    file.setUploader(other);

    when(fileMetadataRepository.findByS3Key("key1")).thenReturn(Optional.of(file));

    assertThatThrownBy(() -> userAssignmentService.submitWork(ua, "content", List.of("key1")))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("File does not belong");
  }

  @Test
  @DisplayName("submitWork throws BadRequestException if deadline has passed")
  void submitWork_deadlinePassed_throwsException() {
    Assignment assignment = new Assignment();
    assignment.setDeadline(LocalDateTime.now().minusHours(1));
    UserAssignment ua = new UserAssignment();
    ua.setAssignment(assignment);
    ua.setStatus(StudentAssignmentStatus.ASSIGNED);

    assertThatThrownBy(() -> userAssignmentService.submitWork(ua, "too late", List.of()))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("The deadline for this assignment has passed.");
  }

  @Test
  @DisplayName("evaluateAssignment updates grade and feedback for TURNED_IN")
  void evaluateAssignment_valid_updatesUa() {
    UserAssignment ua = new UserAssignment();
    ua.setStatus(StudentAssignmentStatus.TURNED_IN);

    userAssignmentService.evaluateAssignment(ua, "A", "Great job", new User());

    assertThat(ua.getStatus()).isEqualTo(StudentAssignmentStatus.EVALUATED);
    assertThat(ua.getGrade()).isEqualTo("A");
    assertThat(ua.getFeedback()).isEqualTo("Great job");
    verify(userAssignmentRepository).save(ua);
  }

  @Test
  @DisplayName("evaluateAssignment allows re-grading for EVALUATED status")
  void evaluateAssignment_regrading_updatesUa() {
    UserAssignment ua = new UserAssignment();
    ua.setStatus(StudentAssignmentStatus.EVALUATED);
    ua.setGrade("C");

    userAssignmentService.evaluateAssignment(ua, "A", "Improved", new User());

    assertThat(ua.getStatus()).isEqualTo(StudentAssignmentStatus.EVALUATED);
    assertThat(ua.getGrade()).isEqualTo("A");
    verify(userAssignmentRepository).save(ua);
  }

  @Test
  @DisplayName("evaluateAssignment throws IllegalStateException for invalid status")
  void evaluateAssignment_invalidStatus_throwsException() {
    UserAssignment ua = new UserAssignment();
    ua.setStatus(StudentAssignmentStatus.ASSIGNED);

    assertThatThrownBy(
            () -> userAssignmentService.evaluateAssignment(ua, "A", "Feedback", new User()))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Cannot evaluate assignment in status");
  }

  @Test
  @DisplayName("getOrCreateForStudent returns existing if present")
  void getOrCreateForStudent_existing_returnsExisting() {
    Assignment a = new Assignment();
    User student = new User();
    UserAssignment existing = new UserAssignment();
    when(userAssignmentRepository.findByAssignmentAndStudent(a, student))
        .thenReturn(Optional.of(existing));

    UserAssignment result = userAssignmentService.getOrCreateForStudent(a, student);

    assertThat(result).isSameAs(existing);
    verify(userAssignmentRepository, never()).save(any());
  }

  @Test
  @DisplayName("getOrCreateForStudent creates new if absent and authorized")
  void getOrCreateForStudent_absent_createsNew() {
    Assignment a = new Assignment();
    User student = new User();
    org.example.projectbackendteammycodebasebringsalltheboys.entity.Role role =
        new org.example.projectbackendteammycodebasebringsalltheboys.entity.Role();
    role.setName("ROLE_STUDENT");
    student.setRole(role);
    student.setUsername("alice");

    when(userAssignmentRepository.findByAssignmentAndStudent(a, student))
        .thenReturn(Optional.empty());
    when(authorizationService.canViewAssignment(student, a)).thenReturn(true);
    when(userAssignmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    UserAssignment result = userAssignmentService.getOrCreateForStudent(a, student);

    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(StudentAssignmentStatus.ASSIGNED);
    verify(userAssignmentRepository).save(any());
  }
}
