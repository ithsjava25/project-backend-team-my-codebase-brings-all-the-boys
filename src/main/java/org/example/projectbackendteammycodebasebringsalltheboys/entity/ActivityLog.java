package org.example.projectbackendteammycodebasebringsalltheboys.entity;

import jakarta.persistence.*;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityStatus;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "activity_logs")
@Getter
@Setter
@NoArgsConstructor
public class ActivityLog {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  private User user;

  private UUID parentId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ActivityAction action;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EntityType entityType;

  private UUID childId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private Map<String, Object> details = new HashMap<>();

  @Column(nullable = false)
  private LocalDateTime timestamp;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ActivityStatus status;

  public ActivityLog(
      User user,
      UUID parentId,
      ActivityAction action,
      EntityType entityType,
      UUID childId,
      Map<String, Object> details,
      ActivityStatus status,
      Clock clock) {
    this.user = user;
    this.parentId = parentId;
    this.action = action;
    this.entityType = entityType;
    this.childId = childId;
    this.details = details;
    this.status = status;
    this.timestamp = LocalDateTime.now(clock);
  }
}
