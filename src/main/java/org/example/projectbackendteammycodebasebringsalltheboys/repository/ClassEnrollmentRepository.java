package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.ClassEnrollment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ClassRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassEnrollmentRepository extends JpaRepository<ClassEnrollment, UUID> {
  List<ClassEnrollment> findByUser(User user);

  List<ClassEnrollment> findBySchoolClass(SchoolClass schoolClass);

  Optional<ClassEnrollment> findByUserAndSchoolClass(User user, SchoolClass schoolClass);

  List<ClassEnrollment> findBySchoolClassAndClassRole(SchoolClass schoolClass, ClassRole classRole);

  boolean existsByUserAndSchoolClass(User user, SchoolClass schoolClass);

  boolean existsByUserAndSchoolClassAndClassRoleIn(
      User user, SchoolClass schoolClass, List<ClassRole> roles);

  @org.springframework.data.jpa.repository.Query(
      "SELECT count(e1) > 0 FROM ClassEnrollment e1 JOIN ClassEnrollment e2 ON e1.schoolClass = e2.schoolClass WHERE e1.user.id = :userId1 AND e2.user.id = :userId2")
  boolean hasSharedSchoolClass(UUID userId1, UUID userId2);

  void deleteByUserId(UUID userId);
}
