package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, UUID> {
  Optional<SchoolClass> findByName(String name);

  @Query("SELECT DISTINCT sc FROM SchoolClass sc JOIN sc.enrollments e WHERE e.user.id = :userId")
  List<SchoolClass> findByEnrollments_UserId(UUID userId);
}
