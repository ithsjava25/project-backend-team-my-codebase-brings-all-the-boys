package org.example.projectbackendteammycodebasebringsalltheboys.dto.user;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.Data;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;

@Data
public class ActivityLogResponse {
  private UUID id;
  private ActivityAction action;
  private EntityType entityType;
  private UUID entityId;
  private Map<String, Object> details;
  private LocalDateTime timestamp;
  private String actorUsername;
}
