package org.example.projectbackendteammycodebasebringsalltheboys.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequest {
  @NotBlank private String username;

  @NotBlank @Email private String email;

  private String password; // Optional for updates, required for creation

  @NotBlank private String roleName; // Name of the role, e.g., "ROLE_ADMIN", "ROLE_TEACHER"

  private java.util.List<java.util.UUID> schoolClassIds;
  private java.util.List<java.util.UUID> courseIds;
}
