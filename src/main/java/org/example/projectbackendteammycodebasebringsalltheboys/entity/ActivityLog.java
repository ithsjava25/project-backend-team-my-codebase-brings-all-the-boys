package org.example.projectbackendteammycodebasebringsalltheboys.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;

@Entity
@Table(name = "activity_logs")
@Getter
@Setter
@NoArgsConstructor
public class ActivityLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long caseId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false)
  private ActivityAction action;

  @Column(nullable = false)
  private EntityType entityType;

  private Long entityId;

  @Column(columnDefinition = "TEXT")
  private String details;

  @Column(nullable = false)
  private LocalDateTime timestamp = LocalDateTime.now();

  public ActivityLog(User user, Long caseId, ActivityAction action, EntityType entityType, Long entityId, String details) {
    this.user = user;
    this.caseId = caseId;
    this.action = action;
    this.entityType = entityType;
    this.entityId = entityId;
    this.details = details;
  }
}
