package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Course;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

  List<Course> findBySchoolClass(SchoolClass schoolClass);

  Optional<Course> findByNameAndSchoolClass(String name, SchoolClass schoolClass);

  boolean existsById(@NonNull UUID id); // Added for checking existence before deletion

  @Modifying
  @Query("UPDATE Course c SET c.leadTeacher = null WHERE c.leadTeacher.id = :userId")
  void nullifyLeadTeacher(@Param("userId") UUID userId);

  @Modifying
  @Query(value = "DELETE FROM course_assistants WHERE user_id = :userId", nativeQuery = true)
  void removeAssistantFromAllCourses(@Param("userId") UUID userId);

  @Query(
      "SELECT DISTINCT c FROM Course c JOIN c.schoolClass sc JOIN sc.enrollments e WHERE e.user.id = :userId")
  Page<Course> findByEnrollments_UserId(UUID userId, Pageable pageable);

  @SuppressWarnings("unused")
  Page<Course> findBySchoolClass(SchoolClass schoolClass, Pageable pageable);

  Page<Course> findByLeadTeacherId(UUID teacherId, Pageable pageable);

  @Query(
      "SELECT DISTINCT c FROM Course c "
          + "LEFT JOIN c.assistants a "
          + "WHERE c.leadTeacher.id = :teacherId OR a.id = :teacherId")
  Page<Course> findAccessibleByTeacher(UUID teacherId, Pageable pageable);

  @SuppressWarnings("unused")
  Page<Course> findByAssistantsId(UUID assistantId, Pageable pageable);

  @Query(
      "SELECT CASE WHEN EXISTS ("
          + "SELECT 1 FROM Course c "
          + "LEFT JOIN c.schoolClass sc "
          + "LEFT JOIN sc.enrollments e1 "
          + "LEFT JOIN sc.enrollments e2 "
          + "LEFT JOIN c.assistants a1 "
          + "LEFT JOIN c.assistants a2 "
          + "WHERE (e1.user.id = :userId1 OR c.leadTeacher.id = :userId1 OR a1.id = :userId1) "
          + "AND (e2.user.id = :userId2 OR c.leadTeacher.id = :userId2 OR a2.id = :userId2)"
          + ") THEN true ELSE false END")
  boolean hasSharedCourse(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2);
}
