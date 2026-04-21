package org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.AssignmentStatus;

@Data
public class AssignmentUpdateRequest {
  private String title;
  private String description;
  private LocalDateTime deadline;
  private AssignmentStatus status;
  private UUID courseId;
}
