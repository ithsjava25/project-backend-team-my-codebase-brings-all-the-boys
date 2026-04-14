package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.*;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.RoleRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

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
}
