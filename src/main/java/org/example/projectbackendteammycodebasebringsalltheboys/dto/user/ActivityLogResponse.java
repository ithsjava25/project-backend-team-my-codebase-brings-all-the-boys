package org.example.projectbackendteammycodebasebringsalltheboys.dto.user;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class ActivityLogResponse {
  private UUID id;
  private String action;
  private String entityType;
  private UUID entityId;
  private String details;
  private LocalDateTime timestamp;
}
