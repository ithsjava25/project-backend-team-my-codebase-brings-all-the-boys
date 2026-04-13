package org.example.projectbackendteammycodebasebringsalltheboys.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.AssignmentStatus;
import org.hibernate.annotations.SoftDelete;

@Entity
@Table(name = "assignments")
@SoftDelete(columnName = "deleted")
@Getter
@Setter
@NoArgsConstructor
public class Assignment extends BaseEntity {

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "creator_id", nullable = false)
  private User creator;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "course_id")
  private Course course;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AssignmentStatus status = AssignmentStatus.CREATED;

  @Column private java.time.LocalDateTime deadline;
}
