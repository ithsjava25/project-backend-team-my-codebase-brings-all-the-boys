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

  @EntityGraph(attributePaths = {"courses", "enrollments"})
  @Query("SELECT sc FROM SchoolClass sc WHERE sc.id = :id")
  Optional<SchoolClass> findDetailById(UUID id);

  @EntityGraph(attributePaths = {"courses"})
  @Query("SELECT DISTINCT sc FROM SchoolClass sc")
  List<SchoolClass> findAllWithDetails();

  @EntityGraph(attributePaths = {"courses"})
  @Query("SELECT DISTINCT sc FROM SchoolClass sc JOIN sc.enrollments e WHERE e.user.id = :userId")
  List<SchoolClass> findByEnrollments_User_Id(UUID userId);

  @Query("SELECT DISTINCT sc FROM SchoolClass sc JOIN sc.enrollments e WHERE e.user.id = :userId")
  Page<SchoolClass> findByEnrollments_UserId(UUID userId, Pageable pageable);
}
