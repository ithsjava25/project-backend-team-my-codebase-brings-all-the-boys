package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Course;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
  List<Course> findBySchoolClass(SchoolClass schoolClass);

  Optional<Course> findByNameAndSchoolClass(String name, SchoolClass schoolClass);

  boolean existsById(UUID id); // Added for checking existence before deletion
}
