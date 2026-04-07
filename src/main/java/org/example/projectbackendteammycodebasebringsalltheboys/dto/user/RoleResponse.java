package org.example.projectbackendteammycodebasebringsalltheboys.dto.user;

import java.util.UUID;
import lombok.Data;

@Data
public class RoleResponse {
  private UUID id;
  private String name;
}
