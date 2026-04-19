package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.projectbackendteammycodebasebringsalltheboys.annotation.LogActivity;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.*;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.UnauthorizedException;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.RoleRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
  private final DtoMapper
      dtoMapper; // Assume DtoMapper is available and has toUserRequest/toUserResponse

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

  @Transactional(readOnly = true)
  public Page<User> findAllUsers(Pageable pageable) {
    return userRepository.findAll(pageable);
  }

  @Transactional(readOnly = true)
  public Page<User> searchUsers(String search, String roleName, Pageable pageable) {
    if ((search == null || search.isBlank()) && (roleName == null || roleName.isBlank())) {
      return findAllUsers(pageable);
    }
    return userRepository.findBySearchAndRole(search, roleName, pageable);
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

    return userRepository.save(user);
  }

  @Transactional
  @LogActivity(action = ActivityAction.DELETED, entityType = EntityType.USER)
  public void deleteUser(UUID id) {
    if (!userRepository.existsById(id)) {
      throw new NotFoundException("User not found with id: " + id);
    }
    userRepository.deleteById(id);
  }

  @Transactional(readOnly = true)
  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !authentication.isAuthenticated()
        || !(authentication.getPrincipal() instanceof String)) {
      throw new UnauthorizedException("User not authenticated");
    }
    String username = (String) authentication.getPrincipal();
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Current user not found"));
  }
}
