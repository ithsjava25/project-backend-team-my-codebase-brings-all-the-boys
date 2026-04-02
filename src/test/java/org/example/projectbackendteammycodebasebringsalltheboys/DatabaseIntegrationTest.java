package org.example.projectbackendteammycodebasebringsalltheboys;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.RoleRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DatabaseIntegrationTest {

  @Autowired private UserRepository userRepository;

  @Autowired private RoleRepository roleRepository;

  @Test
  @WithMockUser(username = "testuser")
  public void testUserAuditing() {
    Role role = new Role("ROLE_STUDENT");
    roleRepository.save(role);

    User user = new User();
    user.setUsername("testuser");
    user.setPassword("password");
    user.setEmail("test@example.com");
    user.setRole(role);

    User savedUser = userRepository.save(user);

    assertThat(savedUser.getCreatedAt()).isNotNull();
    assertThat(savedUser.getUpdatedAt()).isNotNull();
    assertThat(savedUser.getCreatedBy()).isEqualTo("testuser");
    assertThat(savedUser.getUpdatedBy()).isEqualTo("testuser");
    assertThat(savedUser.isDeleted()).isFalse();
  }

  @Test
  @WithMockUser(username = "admin")
  public void testUserUpdateAuditing() {
    Role role = new Role("ROLE_ADMIN");
    roleRepository.save(role);

    User user = new User();
    user.setUsername("admin");
    user.setPassword("password");
    user.setEmail("admin@example.com");
    user.setRole(role);

    User savedUser = userRepository.saveAndFlush(user);
    LocalDateTime firstUpdate = savedUser.getUpdatedAt();

    savedUser.setEmail("newadmin@example.com");
    User updatedUser = userRepository.saveAndFlush(savedUser);

    assertThat(updatedUser.getUpdatedAt()).isAfterOrEqualTo(firstUpdate);
    assertThat(updatedUser.getUpdatedBy()).isEqualTo("admin");
  }
}
