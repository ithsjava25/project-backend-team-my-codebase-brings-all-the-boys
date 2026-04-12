package org.example.projectbackendteammycodebasebringsalltheboys.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.ClassEnrollment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityStatus;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ClassRole;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.ClassEnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClassEnrollmentService {

  private final ClassEnrollmentRepository enrollmentRepository;
  private final ActivityLogService activityLogService;

  @Transactional
  public ClassEnrollment enrollUser(
      User user, SchoolClass schoolClass, ClassRole role, User actor) {
    Optional<ClassEnrollment> existing =
        enrollmentRepository.findByUserAndSchoolClass(user, schoolClass);

    if (existing.isPresent()) {
      ClassEnrollment enrollment = existing.get();
      enrollment.setClassRole(role);
      ClassEnrollment saved = enrollmentRepository.save(enrollment);
      activityLogService.log(
          actor,
          schoolClass.getId(),
          ActivityAction.UPDATED,
          EntityType.CLASS_ENROLLMENT,
          saved.getId(),
          Map.of("enrolledUser", user.getUsername(), "role", role.name()),
          ActivityStatus.SUCCESS);
      return saved;
    }

    ClassEnrollment enrollment = new ClassEnrollment();
    enrollment.setUser(user);
    enrollment.setSchoolClass(schoolClass);
    enrollment.setClassRole(role);

    ClassEnrollment saved = enrollmentRepository.save(enrollment);

    activityLogService.log(
        actor,
        schoolClass.getId(),
        ActivityAction.ADDED,
        EntityType.CLASS_ENROLLMENT,
        saved.getId(),
        Map.of("enrolledUser", user.getUsername(), "role", role.name()),
        ActivityStatus.SUCCESS);

    return saved;
  }

  @Transactional(readOnly = true)
  public List<ClassEnrollment> getEnrollmentsByClass(SchoolClass schoolClass) {
    return enrollmentRepository.findBySchoolClass(schoolClass);
  }

  @Transactional(readOnly = true)
  public List<ClassEnrollment> getEnrollmentsByUser(User user) {
    return enrollmentRepository.findByUser(user);
  }

  @Transactional(readOnly = true)
  public boolean isUserInClass(User user, SchoolClass schoolClass) {
    return enrollmentRepository.findByUserAndSchoolClass(user, schoolClass).isPresent();
  }

  @Transactional(readOnly = true)
  public boolean hasRoleInClass(User user, SchoolClass schoolClass, ClassRole role) {
    return enrollmentRepository
        .findByUserAndSchoolClass(user, schoolClass)
        .map(e -> e.getClassRole() == role)
        .orElse(false);
  }

  @Transactional(readOnly = true)
  public List<ClassEnrollment> getMentorsForClass(SchoolClass schoolClass) {
    return enrollmentRepository.findBySchoolClassAndClassRole(schoolClass, ClassRole.MENTOR);
  }
}
