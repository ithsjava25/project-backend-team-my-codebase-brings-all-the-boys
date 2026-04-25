package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.projectbackendteammycodebasebringsalltheboys.annotation.LogActivity;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.*;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Course;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ClassRole;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.UnauthorizedException;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.*;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final DtoMapper dtoMapper;
  private final SchoolClassRepository schoolClassRepository;
  private final CourseRepository courseRepository;
  private final ClassEnrollmentRepository classEnrollmentRepository;
  private final UserAssignmentRepository userAssignmentRepository;
  private final SubmissionRepository submissionRepository;
  private final FileMetadataRepository fileMetadataRepository;
  private final CommentRepository commentRepository;
  private final ActivityLogRepository activityLogRepository;
  private final AuthorizationService authorizationService;
  private final AssignmentRepository assignmentRepository;
  private final ClassEnrollmentService classEnrollmentService;

  @Transactional(readOnly = true)
  public UserProfileResponse getUserProfile(UUID id) {
    User target =
        userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));

    User actor = getCurrentUser();

    if (!authorizationService.canViewUserProfile(actor, target)) {
      throw new org.example.projectbackendteammycodebasebringsalltheboys.exception
          .ForbiddenException("You are not authorized to view this user's profile.");
    }

    Pageable limit = PageRequest.of(0, 100);
    List<SchoolClass> classes =
        schoolClassRepository.findByUserIdPaged(target.getId(), limit).getContent();

    // For courses, we need to check lead teacher AND assistants
    // Use a bounded set to deduplicate and prevent unbounded memory usage
    Set<Course> uniqueCourses = new LinkedHashSet<>();
    String roleName = target.getRole() != null ? target.getRole().getName() : "";

    if (roleName.equals("ROLE_TEACHER")) {
      uniqueCourses.addAll(
          courseRepository.findByLeadTeacherId(target.getId(), limit).getContent());
      uniqueCourses.addAll(courseRepository.findByAssistantsId(target.getId(), limit).getContent());
    } else if (roleName.equals("ROLE_STUDENT")) {
      uniqueCourses.addAll(
          courseRepository.findByEnrollments_UserId(target.getId(), limit).getContent());
    } else if (roleName.equals("ROLE_ADMIN")) {
      uniqueCourses.addAll(courseRepository.findAll(limit).getContent());
    }

    return dtoMapper.toUserProfileResponse(target, classes, new ArrayList<>(uniqueCourses));
  }

  @Transactional(readOnly = true)
  public Optional<User> getUserByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  @Transactional(readOnly = true)
  public Optional<User> getUserById(UUID id) {
    return userRepository.findById(id);
  }

  public User userRegistration(@NonNull RegistrationRequest request, @NonNull Role role) {
    String username = request.getUsername().trim();
    String email = request.getEmail().trim();
    String password = request.getPassword();

    if (userRepository.findByUsername(username).isPresent()) {
      throw new IllegalStateException("User already exists");
    }

    if (userRepository.findByEmail(email).isPresent()) {
      throw new IllegalStateException("Email already registered");
    }

    User user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password));
    user.setEmail(email);
    user.setRole(role);

    return userRepository.save(user);
  }

  @Transactional
  public User externalUserRegistration(@NonNull ExternalRegistrationRequest request) {

    if (!request.getPassword().equals(request.getConfirmPassword())) {
      throw new IllegalStateException("Passwords do not match");
    }

    Role defaultRole =
        roleRepository
            .findByName("ROLE_STUDENT")
            .orElseThrow(() -> new IllegalStateException("Default role not found"));

    return userRegistration(request, defaultRole);
  }

  @Transactional
  public Map<User, String> InternalUserRegistration(@NonNull String email, @NonNull Role role) {
    InternalRegistrationRequest request = new InternalRegistrationRequest();
    request.setUsername(email);
    request.setEmail(email);

    String password = RandomStringUtils.secure().nextAlphanumeric(8);
    request.setPassword(password);

    return Map.of(userRegistration(request, role), password);
  }

  @Transactional(noRollbackFor = Exception.class)
  public Map<User, String> bulkCreateUsers(@NonNull List<String> emails, @NonNull Role role) {
    Map<User, String> users = new HashMap<>();
    for (String email : emails) {
      try {
        users.putAll(InternalUserRegistration(email, role));
      } catch (Exception e) {
        // log somehow but register the none failing emails
      }
    }
    return users;
  }

  @Transactional(readOnly = true)
  public UserResponse toUserResponse(@NonNull User user) {
    UserResponse response = new UserResponse();
    response.setId(user.getId());
    response.setUsername(user.getUsername());
    response.setEmail(user.getEmail());

    if (user.getRole() != null) {
      RoleResponse roleResponse = new RoleResponse();
      roleResponse.setId(user.getRole().getId());
      roleResponse.setName(user.getRole().getName());
      response.setRole(roleResponse);
    }

    return response;
  }

  // --- New methods for User Management ---

  private String normalize(String value) {
    return (value == null || value.isBlank()) ? null : value.trim();
  }

  @Transactional(readOnly = true)
  public Page<User> searchUsers(String search, String roleName, Pageable pageable) {

    String normalizedSearch = normalize(search);
    String normalizedRoleName = normalize(roleName);

    return userRepository.findBySearchAndRole(normalizedSearch, normalizedRoleName, pageable);
  }

  @Transactional
  @LogActivity(action = ActivityAction.CREATED, entityType = EntityType.USER)
  public User createUser(UserRequest request) {
    if (userRepository.existsByUsername(request.getUsername())
        || userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalStateException("Username or email already exists");
    }
    Role role =
        roleRepository
            .findByName(request.getRoleName())
            .orElseThrow(
                () -> new IllegalStateException("Role not found: " + request.getRoleName()));

    User user = new User();
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword())); // Encode password
    user.setRole(role);
    return userRepository.save(user);
  }

  @Transactional
  @LogActivity(action = ActivityAction.UPDATED, entityType = EntityType.USER)
  public User updateUser(UUID id, UserRequest request) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

    User actor = getCurrentUser();

    // Update user details
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    // Only update password if provided and not empty
    if (request.getPassword() != null && !request.getPassword().isEmpty()) {
      user.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    // Update role
    Role role =
        roleRepository
            .findByName(request.getRoleName())
            .orElseThrow(
                () -> new IllegalStateException("Role not found: " + request.getRoleName()));
    user.setRole(role);

    User savedUser = userRepository.save(user);

    // Sync school classes if provided
    if (request.getSchoolClassIds() != null && !request.getSchoolClassIds().isEmpty()) {
      // 1. Deduplicate and bulk load
      List<UUID> uniqueIds = request.getSchoolClassIds().stream().distinct().toList();
      List<SchoolClass> classes = schoolClassRepository.findAllById(uniqueIds);

      if (classes.size() != uniqueIds.size()) {
        List<UUID> foundIds = classes.stream().map(SchoolClass::getId).toList();
        List<UUID> missingIds =
            uniqueIds.stream().filter(idReq -> !foundIds.contains(idReq)).toList();
        throw new NotFoundException("School classes not found: " + missingIds);
      }

      // 2. Remove existing enrollments
      classEnrollmentRepository.deleteByUserId(id);

      // 3. Add new enrollments
      ClassRole classRole = resolveClassRole(role.getName());

      for (SchoolClass sc : classes) {
        this.classEnrollmentService.enrollUser(savedUser, sc, classRole, actor);
      }
    }

    return savedUser;
  }

  @Transactional
  @LogActivity(action = ActivityAction.UPDATED, entityType = EntityType.USER)
  public User updateProfile(UserRequest request) {
    User currentUser = getCurrentUser();

    // Update allowed profile fields
    currentUser.setUsername(request.getUsername());
    currentUser.setEmail(request.getEmail());

    if (request.getPassword() != null && !request.getPassword().isEmpty()) {
      currentUser.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    return userRepository.save(currentUser);
  }

  private ClassRole resolveClassRole(String roleName) {
    return switch (roleName) {
      case "ROLE_TEACHER" -> ClassRole.TEACHER;
      case "ROLE_ADMIN" -> ClassRole.MENTOR;
      default -> ClassRole.STUDENT;
    };
  }

  @Transactional
  @LogActivity(action = ActivityAction.DELETED, entityType = EntityType.USER)
  public void deleteUser(UUID id) {
    User user =
        userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));

    User actor = getCurrentUser();

    // 1. Delete student-scoped records (Cascade is often LAZY or missing for soft-delete)
    // First delete comments linked to student's UserAssignments to avoid FK constraints
    commentRepository.deleteByUserAssignment_Student_Id(id);
    // Then delete comments authored by the student (e.g., on others' submissions)
    commentRepository.deleteByAuthor_Id(id);
    // Now safe to delete submissions and UserAssignments
    submissionRepository.deleteByUserAssignment_Student_Id(id);
    userAssignmentRepository.deleteByStudent_Id(id);

    // 2. Nullify references where the user is an uploader or creator
    fileMetadataRepository.nullifyUploader(id);
    assignmentRepository.nullifyCreator(id);

    // 3. Delete activity logs (cleanup redundant trace data)
    activityLogRepository.deleteByUser_Id(id);

    // 4. Handle Course associations
    courseRepository.nullifyLeadTeacher(id);
    courseRepository.removeAssistantFromAllCourses(id);

    // 5. Cleanup class enrollments
    // Manual cleanup is required because @SoftDelete turns deletions into UPDATEs,
    // which bypasses DDL-level @OnDelete(CASCADE) constraints.
    classEnrollmentRepository.deleteByUserId(id);

    // 6. Finally delete the user
    userRepository.delete(user);
  }

  @Transactional(readOnly = true)
  public List<User> getUsersByRole(String roleName) {
    return userRepository.findByRole_Name(roleName);
  }

  @Transactional(readOnly = true)
  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
      throw new UnauthorizedException("User not authenticated");
    }
    String username = authentication.getName();
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Current user not found"));
  }
}
