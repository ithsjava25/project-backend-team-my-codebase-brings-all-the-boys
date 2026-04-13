package org.example.projectbackendteammycodebasebringsalltheboys.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Course;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.AssignmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CaseServiceTest {

  @Mock private AssignmentRepository assignmentRepository;

  private CaseService caseService;

  @BeforeEach
  void setUp() {
    caseService = new CaseService(assignmentRepository);
  }

  @Test
  @DisplayName("createCase saves assignment with correct details")
  void createCase_savesAssignment() {
    User creator = new User();
    creator.setId(UUID.randomUUID());
    Course course = new Course();
    course.setId(UUID.randomUUID());

    when(assignmentRepository.save(any(Assignment.class))).thenAnswer(inv -> inv.getArgument(0));

    Assignment result = caseService.createCase("Title", "Description", creator, course);

    assertThat(result.getTitle()).isEqualTo("Title");
    assertThat(result.getDescription()).isEqualTo("Description");
    assertThat(result.getCreator()).isEqualTo(creator);
    assertThat(result.getCourse()).isEqualTo(course);

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
