package org.example.projectbackendteammycodebasebringsalltheboys.dto.dashboard;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PendingSubmissionDTO {
  private UUID userAssignmentId;
  private String assignmentTitle;
  private String studentName;
  private LocalDateTime submittedAt;
  private UUID courseId;
  private String courseName;
}
