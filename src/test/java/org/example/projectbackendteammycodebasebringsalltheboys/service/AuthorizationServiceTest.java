package org.example.projectbackendteammycodebasebringsalltheboys.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Comment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.UserAssignment;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.ClassEnrollmentRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.CourseRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserAssignmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

  @Mock private UserAssignmentRepository userAssignmentRepository;
  @Mock private ClassEnrollmentRepository classEnrollmentRepository;
  @Mock private CourseRepository courseRepository;

  private AuthorizationService authorizationService;

  @BeforeEach
  void setUp() {
    authorizationService =
        new AuthorizationService(
            userAssignmentRepository, classEnrollmentRepository, courseRepository);
  }

  private User createUser(String roleName) {
    User user = new User();
    user.setId(UUID.randomUUID());
    Role role = new Role();
    role.setName(roleName);
    user.setRole(role);
    return user;
  }

  private Assignment createAssignment(User creator) {
    Assignment assignment = new Assignment();
    assignment.setId(UUID.randomUUID());
    assignment.setCreator(creator);
    return assignment;
  }

  @Test
  @DisplayName("canAccessCase returns true for admin")
  void canAccessCase_admin_returnsTrue() {
    User admin = createUser("ROLE_ADMIN");
    Assignment assignment = createAssignment(null);
    assertThat(authorizationService.canAccessCase(admin, assignment)).isTrue();
  }

  @Test
  @DisplayName("canAccessCase returns true for teacher who is creator")
  void canAccessCase_teacherCreator_returnsTrue() {
    User teacher = createUser("ROLE_TEACHER");
    Assignment assignment = createAssignment(teacher);
    assertThat(authorizationService.canAccessCase(teacher, assignment)).isTrue();
  }

  @Test
  @DisplayName("canAccessCase returns false for teacher who is not creator")
  void canAccessCase_teacherNotCreator_returnsFalse() {
    User teacher = createUser("ROLE_TEACHER");
    User other = createUser("ROLE_TEACHER");
    Assignment assignment = createAssignment(other);
    assertThat(authorizationService.canAccessCase(teacher, assignment)).isFalse();
  }

  @Test
  @DisplayName("canAccessCase returns true for student assigned to case")
  void canAccessCase_studentAssigned_returnsTrue() {
    User student = createUser("ROLE_STUDENT");
    Assignment assignment = createAssignment(null);
    when(userAssignmentRepository.findByAssignmentAndStudent(assignment, student))
        .thenReturn(Optional.of(new UserAssignment()));

    assertThat(authorizationService.canAccessCase(student, assignment)).isTrue();
  }

  @Test
  @DisplayName("canAccessCase returns false for student not assigned to case")
  void canAccessCase_studentNotAssigned_returnsFalse() {
    User student = createUser("ROLE_STUDENT");
    Assignment assignment = createAssignment(null);
    when(userAssignmentRepository.findByAssignmentAndStudent(assignment, student))
        .thenReturn(Optional.empty());

    assertThat(authorizationService.canAccessCase(student, assignment)).isFalse();
  }

  @Test
  @DisplayName("canModifyComment returns true for admin")
  void canModifyComment_admin_returnsTrue() {
    User admin = createUser("ROLE_ADMIN");
    Comment comment = new Comment();
    comment.setAuthor(createUser("ROLE_STUDENT"));
    assertThat(authorizationService.canModifyComment(admin, comment)).isTrue();
  }

  @Test
  @DisplayName("canModifyComment returns true for author")
  void canModifyComment_author_returnsTrue() {
    User student = createUser("ROLE_STUDENT");
    Comment comment = new Comment();
    comment.setAuthor(student);
    assertThat(authorizationService.canModifyComment(student, comment)).isTrue();
  }

  @Test
  @DisplayName("canModifyComment returns false for other student")
  void canModifyComment_otherStudent_returnsFalse() {
    User student = createUser("ROLE_STUDENT");
    User other = createUser("ROLE_STUDENT");
    Comment comment = new Comment();
    comment.setAuthor(other);
    assertThat(authorizationService.canModifyComment(student, comment)).isFalse();
  }
}
