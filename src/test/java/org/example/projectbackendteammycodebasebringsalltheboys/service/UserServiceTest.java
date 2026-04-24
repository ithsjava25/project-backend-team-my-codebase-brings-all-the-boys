package org.example.projectbackendteammycodebasebringsalltheboys.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.ExternalRegistrationRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ClassRole;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private RoleRepository roleRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private DtoMapper dtoMapper;
  @Mock private SchoolClassRepository schoolClassRepository;
  @Mock private CourseRepository courseRepository;
  @Mock private ClassEnrollmentRepository classEnrollmentRepository;
  @Mock private AuthorizationService authorizationService;
  @Mock private UserAssignmentRepository userAssignmentRepository;
  @Mock private SubmissionRepository submissionRepository;
  @Mock private FileMetadataRepository fileMetadataRepository;
  @Mock private ActivityLogRepository activityLogRepository;
  @Mock private AssignmentRepository assignmentRepository;
  @Mock private CommentRepository commentRepository;
  @Mock private ClassEnrollmentService classEnrollmentService;

  private UserService userService;

  @BeforeEach
  void setUp() {
    userService =
        new UserService(
            userRepository,
            roleRepository,
            passwordEncoder,
            dtoMapper,
            schoolClassRepository,
            courseRepository,
            classEnrollmentRepository,
            userAssignmentRepository,
            submissionRepository,
            fileMetadataRepository,
            commentRepository,
            activityLogRepository,
            authorizationService,
            assignmentRepository,
            classEnrollmentService);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  // --- helpers ---

  private ExternalRegistrationRequest validRequest(String username, String password) {
    ExternalRegistrationRequest req = new ExternalRegistrationRequest();
    req.setUsername(username);
    req.setPassword(password);
    req.setConfirmPassword(password);
    req.setEmail(username + "@test.com");
    return req;
  }

  private Role studentRole() {
    Role role = new Role();
    role.setName("ROLE_STUDENT");
    return role;
  }

  // =========================================================
  // getUserByUsername
  // =========================================================

  @Test
  @DisplayName("getUserByUsername returns user when found")
  void getUserByUsername_found_returnsUser() {
    User user = new User();
    user.setUsername("alice");
    when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

    Optional<User> result = userService.getUserByUsername("alice");

    assertThat(result).isPresent();
    assertThat(result.get().getUsername()).isEqualTo("alice");
  }

  @Test
  @DisplayName("getUserByUsername returns empty when not found")
  void getUserByUsername_notFound_returnsEmpty() {
    when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

    Optional<User> result = userService.getUserByUsername("ghost");

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("getUserByUsername delegates to repository with exact username")
  void getUserByUsername_delegatesToRepository() {
    when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());

    userService.getUserByUsername("alice");

    verify(userRepository).findByUsername("alice");
    verifyNoMoreInteractions(userRepository);
  }

  // =========================================================
  // registerUser
  // =========================================================

  @Test
  @DisplayName("registerUser throws IllegalStateException when username already exists")
  void externalUser_Registration_duplicateUsername_throwsIllegalStateException() {
    when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.of(studentRole()));
    when(userRepository.findByUsername("alice")).thenReturn(Optional.of(new User()));

    ExternalRegistrationRequest req = validRequest("alice", "password123");

    assertThatThrownBy(() -> userService.externalUserRegistration(req))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("User already exists");

    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("registerUser throws IllegalStateException when passwords do not match")
  void externalUser_Registration_passwordMismatch_throwsIllegalStateException() {
    ExternalRegistrationRequest req = new ExternalRegistrationRequest();
    req.setUsername("alice");
    req.setPassword("password123");
    req.setConfirmPassword("different456");

    assertThatThrownBy(() -> userService.externalUserRegistration(req))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Passwords do not match");

    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("registerUser saves user with encoded password")
  void externalUser_validRequest_savesUserRegistrationWithEncodedPassword() {
    when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
    when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.of(studentRole()));
    when(passwordEncoder.encode("password123")).thenReturn("hashed");
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    userService.externalUserRegistration(validRequest("alice", "password123"));

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(captor.capture());

    User saved = captor.getValue();
    assertThat(saved.getUsername()).isEqualTo("alice");
    assertThat(saved.getPassword()).isEqualTo("hashed");
    assertThat(saved.getRole().getName()).isEqualTo("ROLE_STUDENT");
  }

  @Test
  @DisplayName("registerUser assigns ROLE_STUDENT to new user")
  void externalUser_Registration_validRequest_assignsStudentRole() {
    Role role = studentRole();
    when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
    when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.of(role));
    when(passwordEncoder.encode(anyString())).thenReturn("hashed");
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    userService.externalUserRegistration(validRequest("alice", "password123"));

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(captor.capture());
    assertThat(captor.getValue().getRole()).isSameAs(role);
  }

  // =========================================================
  // updateUser
  // =========================================================

  @Test
  @DisplayName("updateUser synchronizes school class enrollments")
  void updateUser_syncsSchoolClasses() {
    UUID userId = UUID.randomUUID();
    UUID classId1 = UUID.randomUUID();
    UUID classId2 = UUID.randomUUID();

    UserRequest request = new UserRequest();
    request.setUsername("updated");
    request.setEmail("updated@test.com");
    request.setRoleName("ROLE_STUDENT");
    request.setSchoolClassIds(List.of(classId1, classId2));

    User existing = new User();
    existing.setId(userId);
    existing.setRole(studentRole());

    SchoolClass sc1 = new SchoolClass();
    sc1.setId(classId1);
    SchoolClass sc2 = new SchoolClass();
    sc2.setId(classId2);

    when(userRepository.findById(userId)).thenReturn(Optional.of(existing));
    when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.of(studentRole()));
    when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(schoolClassRepository.findAllById(any())).thenReturn(List.of(sc1, sc2));

    // Mock authentication for getCurrentUser()
    Authentication auth = mock(Authentication.class);
    when(auth.getName()).thenReturn("admin");
    when(auth.isAuthenticated()).thenReturn(true);
    SecurityContextHolder.getContext().setAuthentication(auth);

    User adminUser = new User();
    adminUser.setUsername("admin");
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

    userService.updateUser(userId, request);

    verify(classEnrollmentRepository).deleteByUserId(userId);
    verify(classEnrollmentService).enrollUser(existing, sc1, ClassRole.STUDENT, adminUser);
    verify(classEnrollmentService).enrollUser(existing, sc2, ClassRole.STUDENT, adminUser);
  }

  @Test
  @DisplayName("updateUser throws NotFoundException if some class IDs are missing")
  void updateUser_missingClass_throwsException() {
    UUID userId = UUID.randomUUID();
    UUID classId1 = UUID.randomUUID();

    UserRequest request = new UserRequest();
    request.setSchoolClassIds(List.of(classId1));
    request.setRoleName("ROLE_STUDENT");

    User existing = new User();
    existing.setRole(studentRole());

    when(userRepository.findById(userId)).thenReturn(Optional.of(existing));
    when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.of(studentRole()));
    when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(schoolClassRepository.findAllById(any())).thenReturn(List.of());

    // Mock authentication for getCurrentUser()
    Authentication auth = mock(Authentication.class);
    when(auth.getName()).thenReturn("admin");
    when(auth.isAuthenticated()).thenReturn(true);
    SecurityContextHolder.getContext().setAuthentication(auth);

    User adminUser = new User();
    adminUser.setUsername("admin");
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

    assertThatThrownBy(() -> userService.updateUser(userId, request))
        .isInstanceOf(
            org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException
                .class)
        .hasMessageContaining("School classes not found");
  }
}
