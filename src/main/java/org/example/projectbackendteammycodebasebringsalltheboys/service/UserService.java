package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.RegistrationRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.RoleRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserRepository;
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

  @Transactional
  public User registerUser(RegistrationRequest request) {

    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      throw new IllegalStateException("User already exists");
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
    user.setRole(defaultRole);

    return userRepository.save(user);
  }
}
