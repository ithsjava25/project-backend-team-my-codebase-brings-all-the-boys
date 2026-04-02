package org.example.projectbackendteammycodebasebringsalltheboys.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.StudentAssignmentStatus;

@Entity
@Table(
    name = "user_assignments",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_user_assignments_assignment_student",
          columnNames = {"assignment_id", "student_id"})
    })
@Getter
@Setter
@NoArgsConstructor
public class UserAssignment extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assignment_id", nullable = false)
  private Assignment assignment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private User student;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StudentAssignmentStatus status = StudentAssignmentStatus.ASSIGNED;

  @Column(columnDefinition = "TEXT")
  private String feedback;

  private String grade;

  private LocalDateTime turnedInAt;

  @OneToMany(mappedBy = "userAssignment", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Submission> submissions = new ArrayList<>();
}
