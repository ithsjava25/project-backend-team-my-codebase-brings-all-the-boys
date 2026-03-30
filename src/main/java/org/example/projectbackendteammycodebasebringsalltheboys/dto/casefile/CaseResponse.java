package org.example.projectbackendteammycodebasebringsalltheboys.dto.casefile;

import java.time.LocalDateTime;
import lombok.Data;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.AssignmentStatus;

@Data
public class CaseResponse {
  private Long id;
  private String title;
  private String description;
  private UserResponse creator;
  private AssignmentStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
