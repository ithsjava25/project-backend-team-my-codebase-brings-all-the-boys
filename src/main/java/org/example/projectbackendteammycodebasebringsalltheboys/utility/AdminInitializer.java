package org.example.projectbackendteammycodebasebringsalltheboys.utility;

import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.RoleRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final RoleRepository roleRepository;

  @Value("${bootstrap.admin.username}")
  private String adminUsername;
  @Value("${bootstrap.admin.email}")
  private String adminEmail;
  @Value("${bootstrap.admin.password}")
  private String adminPassword;

  public AdminInitializer(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      RoleRepository roleRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.roleRepository = roleRepository;
  }

  @Override
  public void run(String @NonNull ... args) {
    if (adminPassword == null || adminPassword.length() < 12) {
      throw new IllegalStateException("bootstrap.admin.password must be set and strong");
      }
    String adminRoleName = "ROLE_ADMIN";

    Role adminRole =
        roleRepository
            .findByName(adminRoleName)
            .orElseGet(
                () -> {
                  Role role = new Role();
                  role.setName(adminRoleName);
                  return roleRepository.save(role);
                });

    if (userRepository.findByEmail(adminEmail).isEmpty()) {
      User defaultAdmin = new User();
      defaultAdmin.setUsername(adminUsername.trim());
      defaultAdmin.setEmail(adminEmail);
      defaultAdmin.setPassword(passwordEncoder.encode(adminPassword));
      defaultAdmin.setRole(adminRole);

      userRepository.save(defaultAdmin);

      System.out.println("Default admin account created");
    }
  }
}
