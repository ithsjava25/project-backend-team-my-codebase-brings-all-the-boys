package org.example.projectbackendteammycodebasebringsalltheboys.entity;

import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;

@Entity
@SoftDelete(columnName = "deleted")
@Table(name = "school_classes")
@Getter
@Setter
@NoArgsConstructor
public class SchoolClass extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String name; // e.g., "9A", "Class-2026-A"

  private String description;

  @OneToMany(mappedBy = "schoolClass", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Course> courses = new LinkedHashSet<>();

  @OneToMany(mappedBy = "schoolClass", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<ClassEnrollment> enrollments = new LinkedHashSet<>();
}
