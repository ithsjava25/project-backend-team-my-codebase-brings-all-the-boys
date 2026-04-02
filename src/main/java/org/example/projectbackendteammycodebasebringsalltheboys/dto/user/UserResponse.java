package org.example.projectbackendteammycodebasebringsalltheboys.dto.user;

import java.util.UUID;
import lombok.Data;

@Data
public class UserResponse {
  private UUID id;
  private String username;
  private String email;
  private RoleResponse role;
}
