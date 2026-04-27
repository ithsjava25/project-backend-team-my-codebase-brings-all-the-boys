package org.example.projectbackendteammycodebasebringsalltheboys.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.ClassEnrollment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ClassRole;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.ClassEnrollmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClassEnrollmentServiceTest {

  @Mock private ClassEnrollmentRepository enrollmentRepository;
  @Mock private ActivityLogService activityLogService;

  private ClassEnrollmentService enrollmentService;

  @BeforeEach
  void setUp() {
    enrollmentService = new ClassEnrollmentService(enrollmentRepository, activityLogService);
  }

  @Test
  @DisplayName("enrollUser creates new enrollment if not exists")
  void enrollUser_newEnrollment_savesAndLogs() {
    User user = new User();
    user.setUsername("user1");
    SchoolClass schoolClass = new SchoolClass();
    schoolClass.setName("Class A");
    schoolClass.setId(UUID.randomUUID());
    User actor = new User();

    when(enrollmentRepository.findByUserAndSchoolClass(user, schoolClass))
        .thenReturn(Optional.empty());
    when(enrollmentRepository.save(any(ClassEnrollment.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    ClassEnrollment result =
        enrollmentService.enrollUser(user, schoolClass, ClassRole.STUDENT, actor);

    assertThat(result.getUser()).isEqualTo(user);
    assertThat(result.getSchoolClass()).isEqualTo(schoolClass);
    assertThat(result.getClassRole()).isEqualTo(ClassRole.STUDENT);
    verify(enrollmentRepository).save(any(ClassEnrollment.class));
    verify(activityLogService)
        .log(eq(actor), eq(schoolClass.getId()), any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("enrollUser updates existing enrollment if exists")
  void enrollUser_existingEnrollment_updatesAndLogs() {
    User user = new User();
    user.setUsername("user1");
    SchoolClass schoolClass = new SchoolClass();
    schoolClass.setName("Class A");
    schoolClass.setId(UUID.randomUUID());
    User actor = new User();
    ClassEnrollment existing = new ClassEnrollment();
    existing.setClassRole(ClassRole.STUDENT);

    when(enrollmentRepository.findByUserAndSchoolClass(user, schoolClass))
        .thenReturn(Optional.of(existing));
    when(enrollmentRepository.save(any(ClassEnrollment.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    ClassEnrollment result =
        enrollmentService.enrollUser(user, schoolClass, ClassRole.MENTOR, actor);

    assertThat(result.getClassRole()).isEqualTo(ClassRole.MENTOR);
    verify(enrollmentRepository).save(existing);
    verify(activityLogService)
        .log(eq(actor), eq(schoolClass.getId()), any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("isUserInClass returns true if enrollment found")
  void isUserInClass_found_returnsTrue() {
    User user = new User();
    SchoolClass sc = new SchoolClass();
    when(enrollmentRepository.findByUserAndSchoolClass(user, sc))
        .thenReturn(Optional.of(new ClassEnrollment()));

    assertThat(enrollmentService.isUserInClass(user, sc)).isTrue();
  }

  @Test
  @DisplayName("hasRoleInClass returns true if role matches")
  void hasRoleInClass_matches_returnsTrue() {
    User user = new User();
    SchoolClass sc = new SchoolClass();
    ClassEnrollment e = new ClassEnrollment();
    e.setClassRole(ClassRole.MENTOR);
    when(enrollmentRepository.findByUserAndSchoolClass(user, sc)).thenReturn(Optional.of(e));

    assertThat(enrollmentService.hasRoleInClass(user, sc, ClassRole.MENTOR)).isTrue();
    assertThat(enrollmentService.hasRoleInClass(user, sc, ClassRole.STUDENT)).isFalse();
  }
}
