package org.example.projectbackendteammycodebasebringsalltheboys.dto.user;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ActivityLogResponse {
  private Long id;
  private UserResponse user;
  private String action;
  private String entityType;
  private Long entityId;
  private String details;
  private LocalDateTime timestamp;
}
