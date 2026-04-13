package org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.AssignmentStatus;

@Data
public class AssignmentResponse {
  private UUID id;
  private String title;
  private AssignmentStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deadline;
}
