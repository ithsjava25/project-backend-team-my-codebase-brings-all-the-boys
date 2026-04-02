package org.example.projectbackendteammycodebasebringsalltheboys.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
public class Course extends BaseEntity {

  @Column(nullable = false)
  private String name; // e.g., "Java Backend 1"

  @Column(columnDefinition = "TEXT")
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "school_class_id", nullable = false)
  private SchoolClass schoolClass;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lead_teacher_id")
  private User leadTeacher;

  @ManyToMany
  @JoinTable(
      name = "course_assistants",
      joinColumns = @JoinColumn(name = "course_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  private List<User> assistants = new ArrayList<>();

  @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Assignment> assignments = new ArrayList<>();
}
