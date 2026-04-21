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

  void deleteByUserId(UUID userId);
}
