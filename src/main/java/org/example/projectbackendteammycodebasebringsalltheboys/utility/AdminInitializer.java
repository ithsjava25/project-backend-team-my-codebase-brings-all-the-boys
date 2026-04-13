package org.example.projectbackendteammycodebasebringsalltheboys.utility;

import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.RoleRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final RoleRepository roleRepository;

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
    String adminEmail = "admin@example.com";
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
      defaultAdmin.setUsername("admin");
      defaultAdmin.setEmail(adminEmail);
      defaultAdmin.setPassword(passwordEncoder.encode("admin"));
      defaultAdmin.setRole(adminRole);

      userRepository.save(defaultAdmin);

      System.out.println("Default admin account created");
    }
  }
}
