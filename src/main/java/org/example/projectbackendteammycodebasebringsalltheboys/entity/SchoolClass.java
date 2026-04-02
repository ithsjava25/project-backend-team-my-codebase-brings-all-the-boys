package org.example.projectbackendteammycodebasebringsalltheboys.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "school_classes")
@Getter
@Setter
@NoArgsConstructor
public class SchoolClass extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String name; // e.g., "9A", "Class-2026-A"

  private String description;

  @OneToMany(mappedBy = "schoolClass", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Course> courses = new ArrayList<>();

  @OneToMany(mappedBy = "schoolClass", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ClassEnrollment> enrollments = new ArrayList<>();
}
