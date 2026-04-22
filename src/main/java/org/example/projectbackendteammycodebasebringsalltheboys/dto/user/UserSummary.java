package org.example.projectbackendteammycodebasebringsalltheboys.dto.user;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSummary {
  private UUID id;
  private String username;
}
