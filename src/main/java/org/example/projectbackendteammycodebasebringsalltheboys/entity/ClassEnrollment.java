package org.example.projectbackendteammycodebasebringsalltheboys.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ClassRole;

@Entity
@Table(
    name = "class_enrollments",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_class_enrollment_user_class",
          columnNames = {"user_id", "school_class_id"})
    })
@Getter
@Setter
@NoArgsConstructor
public class ClassEnrollment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "school_class_id", nullable = false)
  private SchoolClass schoolClass;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ClassRole classRole; // e.g., STUDENT, MENTOR, TEACHER
}
