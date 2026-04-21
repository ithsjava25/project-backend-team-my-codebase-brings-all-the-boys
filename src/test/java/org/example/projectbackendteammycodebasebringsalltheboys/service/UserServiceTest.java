package org.example.projectbackendteammycodebasebringsalltheboys.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.ExternalRegistrationRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
            classEnrollmentRepository);
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
  // registerUser — duplicate username
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

  // =========================================================
  // registerUser — password mismatch
  // =========================================================

  @Test
  @DisplayName("registerUser throws IllegalStateException when passwords do not match")
  void externalUser_Registration_passwordMismatch_throwsIllegalStateException() {
    // when(userRepository.findByUsername("alice")).thenReturn(Optional.empty()); <-- Never used,
    // caused test to fail.

    ExternalRegistrationRequest req = new ExternalRegistrationRequest();
    req.setUsername("alice");
    req.setPassword("password123");
    req.setConfirmPassword("different456");

    assertThatThrownBy(() -> userService.externalUserRegistration(req))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Passwords do not match");

    verify(userRepository, never()).save(any());
    verifyNoInteractions(roleRepository, passwordEncoder);
  }

  // =========================================================
  // registerUser — missing default role
  // =========================================================

  @Test
  @DisplayName("registerUser throws IllegalStateException when ROLE_STUDENT is missing")
  void externalUser_Registration_missingDefaultRole_throwsIllegalStateException() {
    // when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
    // when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.empty());

    ExternalRegistrationRequest req = validRequest("alice", "password123");

    assertThatThrownBy(() -> userService.externalUserRegistration(req))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Default role not found");

    verify(userRepository, never()).save(any());
  }

  // =========================================================
  // registerUser — happy path
  // =========================================================

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
  @DisplayName("registerUser never stores raw password")
  void externalUser_Registration_validRequest_doesNotStoreRawPassword() {
    when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
    when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.of(studentRole()));
    when(passwordEncoder.encode(anyString())).thenReturn("hashed");
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    userService.externalUserRegistration(validRequest("alice", "password123"));

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(captor.capture());

    assertThat(captor.getValue().getPassword()).isNotEqualTo("password123");
  }

  @Test
  @DisplayName("registerUser returns the saved user")
  void externalUser_validRequest_returnsSavedUserRegistration() {
    User saved = new User();
    saved.setUsername("alice");
    saved.setPassword("hashed");
    saved.setRole(studentRole());

    when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
    when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.of(studentRole()));
    when(passwordEncoder.encode(anyString())).thenReturn("hashed");
    when(userRepository.save(any(User.class))).thenReturn(saved);

    User result = userService.externalUserRegistration(validRequest("alice", "password123"));

    assertThat(result).isSameAs(saved);
  }

  @Test
  @DisplayName("registerUser encodes the password before saving")
  void externalUser_Registration_validRequest_encodesPasswordBeforeSave() {
    when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
    when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.of(studentRole()));
    when(passwordEncoder.encode("password123")).thenReturn("hashed");
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    userService.externalUserRegistration(validRequest("alice", "password123"));

    // encoder must be called before save
    var inOrder = inOrder(passwordEncoder, userRepository);
    inOrder.verify(passwordEncoder).encode("password123");
    inOrder.verify(userRepository).save(any(User.class));
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
}
