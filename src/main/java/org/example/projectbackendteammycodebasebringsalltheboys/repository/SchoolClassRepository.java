package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, UUID> {
  Optional<SchoolClass> findByName(String name);

  @EntityGraph(attributePaths = {"courses"})
  @Query("SELECT sc FROM SchoolClass sc WHERE sc.id = :id")
  Optional<SchoolClass> findWithCoursesById(UUID id);

  @EntityGraph(attributePaths = {"enrollments"})
  @Query("SELECT sc FROM SchoolClass sc WHERE sc.id = :id")
  Optional<SchoolClass> findWithEnrollmentsById(UUID id);

  @EntityGraph(attributePaths = {"courses"})
  @Query("SELECT DISTINCT sc FROM SchoolClass sc")
  List<SchoolClass> findAllWithDetails();

  /**
   * @deprecated Use {@link #findByUserIdPaged(UUID, Pageable)} for better performance.
   */
  @Deprecated
  @EntityGraph(attributePaths = {"courses"})
  @Query("SELECT DISTINCT sc FROM SchoolClass sc JOIN sc.enrollments e WHERE e.user.id = :userId")
  List<SchoolClass> findByUserId(UUID userId);

  @Query("SELECT DISTINCT sc FROM SchoolClass sc JOIN sc.enrollments e WHERE e.user.id = :userId")
  Page<SchoolClass> findByUserIdPaged(UUID userId, Pageable pageable);
}
