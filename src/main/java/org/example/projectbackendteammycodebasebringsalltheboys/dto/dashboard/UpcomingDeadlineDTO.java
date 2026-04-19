package org.example.projectbackendteammycodebasebringsalltheboys.dto.dashboard;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpcomingDeadlineDTO {
  private UUID assignmentId;
  private String title;
  private LocalDateTime deadline;
  private String courseName;
  private String status; // For student: e.g. "ASSIGNED", "TURNED_IN"
}
