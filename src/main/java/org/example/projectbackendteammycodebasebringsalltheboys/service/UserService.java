package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.RegistrationRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.RoleResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserResponse;
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

  @Transactional
  public User registerUser(@NonNull RegistrationRequest request) {

    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      throw new IllegalStateException("User already exists");
    }

    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new IllegalStateException("Email already registered");
    }

    if (!request.getPassword().equals(request.getConfirmPassword())) {
      throw new IllegalStateException("Passwords do not match");
    }

    Role defaultRole =
        roleRepository
            .findByName("ROLE_STUDENT")
            .orElseThrow(() -> new IllegalStateException("Default role not found"));

    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setEmail(request.getEmail());
    user.setRole(defaultRole);

    return userRepository.save(user);
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
