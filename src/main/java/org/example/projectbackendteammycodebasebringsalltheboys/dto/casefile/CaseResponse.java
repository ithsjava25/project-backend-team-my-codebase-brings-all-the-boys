package org.example.projectbackendteammycodebasebringsalltheboys.dto.casefile;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.AssignmentStatus;

@Data
public class CaseResponse {
  private UUID id;
  private String title;
  private String description;
  private UserResponse creator;
  private AssignmentStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
