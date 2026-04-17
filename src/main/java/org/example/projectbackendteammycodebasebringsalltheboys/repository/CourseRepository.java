package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Course;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

  List<Course> findBySchoolClass(SchoolClass schoolClass);

  Optional<Course> findByNameAndSchoolClass(String name, SchoolClass schoolClass);

  boolean existsById(@NonNull UUID id); // Added for checking existence before deletion

  List<Course> findBySchoolClassId(UUID schoolClassId);
  List<Course> findByLeadTeacherId(UUID teacherId);
  List<Course> findByAssistantsId(UUID assistantId);

  @Query("SELECT DISTINCT c FROM Course c JOIN c.schoolClass sc JOIN sc.enrollments e WHERE e.user.id = :userId")
  List<Course> findByEnrollments_UserId(UUID userId);
}
