package org.example.projectbackendteammycodebasebringsalltheboys.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;

@Entity
@SoftDelete(columnName = "deleted")
@Table(name = "submissions")
@Getter
@Setter
@NoArgsConstructor
public class Submission extends BaseEntity {

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_assignment_id", nullable = false)
  private UserAssignment userAssignment;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "student_id", nullable = false)
  private User student;

  @Column(columnDefinition = "TEXT")
  private String content;

  @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<FileMetadata> files = new ArrayList<>();

  private LocalDateTime submittedAt;

  @PrePersist
  protected void onCreate() {
    this.submittedAt = LocalDateTime.now();
  }
}
