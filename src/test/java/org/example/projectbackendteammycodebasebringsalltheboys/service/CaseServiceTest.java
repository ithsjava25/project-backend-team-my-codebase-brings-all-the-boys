package org.example.projectbackendteammycodebasebringsalltheboys.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment.AssignmentResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Course;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.UserAssignment;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.StudentAssignmentStatus;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.AssignmentRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.CommentRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.CourseRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserAssignmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CaseServiceTest {

  @Mock private AssignmentRepository assignmentRepository;
  @Mock private UserAssignmentRepository userAssignmentRepository;
  @Mock private DtoMapper dtoMapper;
  @Mock private AuthorizationService authorizationService;
  @Mock private CourseRepository courseRepository;
  @Mock private ActivityLogService activityLogService;
  @Mock private CommentRepository commentRepository;

  private CaseService caseService;

  @BeforeEach
  void setUp() {
    caseService =
        new CaseService(
            assignmentRepository,
            userAssignmentRepository,
            dtoMapper,
            authorizationService,
            courseRepository,
            activityLogService,
            commentRepository);
  }

  @Test
  @DisplayName("getAccessibleAssignmentsDto returns student status for ROLE_STUDENT")
  void getAccessibleAssignmentsDto_student_returnsStatus() {
    User student = new User();
    student.setId(UUID.randomUUID());
    Role role = new Role();
    role.setName("ROLE_STUDENT");
    student.setRole(role);

    Assignment assignment1 = new Assignment();
    assignment1.setId(UUID.randomUUID());
    Assignment assignment2 = new Assignment();
    assignment2.setId(UUID.randomUUID());

    List<Assignment> assignments = List.of(assignment1, assignment2);
    Page<Assignment> page = new PageImpl<>(assignments);
    Pageable pageable = mock(Pageable.class);

    when(assignmentRepository.findByStudentEnrollment(student.getId(), pageable)).thenReturn(page);

    UserAssignment ua1 = new UserAssignment();
    ua1.setAssignment(assignment1);
    ua1.setStatus(StudentAssignmentStatus.TURNED_IN);
    when(userAssignmentRepository.findByStudentAndAssignmentIn(student, assignments))
        .thenReturn(List.of(ua1));

    AssignmentResponse resp1 = new AssignmentResponse();
    resp1.setId(assignment1.getId());
    AssignmentResponse resp2 = new AssignmentResponse();
    resp2.setId(assignment2.getId());

    when(dtoMapper.toAssignmentResponse(assignment1)).thenReturn(resp1);
    when(dtoMapper.toAssignmentResponse(assignment2)).thenReturn(resp2);

    Page<AssignmentResponse> result = caseService.getAccessibleAssignmentsDto(student, pageable);

    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent().get(0).getStudentStatus())
        .isEqualTo(StudentAssignmentStatus.TURNED_IN);
    assertThat(result.getContent().get(1).getStudentStatus()).isNull();
  }

  @Test
  @DisplayName("createCase saves assignment with correct details")
  void createCase_savesAssignment() {
    LocalDateTime endDate = LocalDateTime.now().plusDays(1);
    User creator = new User();
    creator.setId(UUID.randomUUID());
    Course course = new Course();
    course.setId(UUID.randomUUID());

    when(assignmentRepository.save(any(Assignment.class))).thenAnswer(inv -> inv.getArgument(0));

    Assignment result = caseService.createCase("Title", "Description", creator, course, endDate);

    assertThat(result.getTitle()).isEqualTo("Title");
    assertThat(result.getDescription()).isEqualTo("Description");
    assertThat(result.getCreator()).isEqualTo(creator);
    assertThat(result.getCourse()).isEqualTo(course);
    assertThat(result.getDeadline()).isEqualTo(endDate);

    verify(assignmentRepository).save(any(Assignment.class));
  }

  @Test
  @DisplayName("getAllCases returns all assignments from repository")
  void getAllCases_returnsAll() {
    List<Assignment> assignments = List.of(new Assignment(), new Assignment());
    when(assignmentRepository.findAll()).thenReturn(assignments);

    List<Assignment> result = caseService.getAllCases();

    assertThat(result).hasSize(2);
    verify(assignmentRepository).findAll();
  }

  @Test
  @DisplayName("getCaseById returns assignment when found")
  void getCaseById_found_returnsAssignment() {
    UUID id = UUID.randomUUID();
    Assignment assignment = new Assignment();
    assignment.setId(id);
    when(assignmentRepository.findById(id)).thenReturn(Optional.of(assignment));

    Optional<Assignment> result = caseService.getCaseById(id);

    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(id);
  }

  @Test
  @DisplayName("getCasesByCreator returns assignments for specific creator")
  void getCasesByCreator_returnsFiltered() {
    User creator = new User();
    List<Assignment> assignments = List.of(new Assignment());
    when(assignmentRepository.findByCreator(creator)).thenReturn(assignments);

    List<Assignment> result = caseService.getCasesByCreator(creator);

    assertThat(result).hasSize(1);
    verify(assignmentRepository).findByCreator(creator);
  }
}
