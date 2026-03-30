package org.example.projectbackendteammycodebasebringsalltheboys.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "activity_logs")
@Getter
@Setter
@NoArgsConstructor
public class ActivityLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false)
  private String action;

  @Column(nullable = false)
  private String entityType;

  private Long entityId;

  @Column(columnDefinition = "TEXT")
  private String details;

  @Column(nullable = false)
  private LocalDateTime timestamp = LocalDateTime.now();

  public ActivityLog(User user, String action, String entityType, Long entityId, String details) {
    this.user = user;
    this.action = action;
    this.entityType = entityType;
    this.entityId = entityId;
    this.details = details;
  }
}
