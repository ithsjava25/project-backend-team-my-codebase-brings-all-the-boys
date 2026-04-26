package org.example.projectbackendteammycodebasebringsalltheboys.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashSet;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.ClassEnrollment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ClassRole;
import org.example.projectbackendteammycodebasebringsalltheboys.storage.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DtoMapperTest {

  @Mock private StorageService storageService;

  @Mock private DtoMapper dtoMapper;

  @BeforeEach
  void setUp() {
    dtoMapper = new DtoMapper(storageService, dtoMapper.getUserAssignmentRepository());
  }

  @Test
  @DisplayName("toUserResponse maps user fields correctly")
  void toUserResponse_mapsFields() {
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setUsername("alice");
    user.setEmail("alice@example.com");
    Role role = new Role();
    role.setName("ROLE_STUDENT");
    user.setRole(role);

    UserResponse result = dtoMapper.toUserResponse(user);

    assertThat(result.getId()).isEqualTo(user.getId());
    assertThat(result.getUsername()).isEqualTo("alice");
    assertThat(result.getEmail()).isEqualTo("alice@example.com");
    assertThat(result.getRole().getName()).isEqualTo("ROLE_STUDENT");
  }

  @Test
  @DisplayName("toSchoolClassDetailResponse filters students and teachers correctly")
  void toSchoolClassDetailResponse_filtersRoles() {
    SchoolClass sc = new SchoolClass();
    sc.setName("Class 1");
    sc.setEnrollments(new LinkedHashSet<>());
    sc.setCourses(new LinkedHashSet<>());

    User studentUser = new User();
    studentUser.setUsername("student1");
    ClassEnrollment studentEnrollment = new ClassEnrollment();
    studentEnrollment.setUser(studentUser);
    studentEnrollment.setClassRole(ClassRole.STUDENT);
    sc.getEnrollments().add(studentEnrollment);

    User teacherUser = new User();
    teacherUser.setUsername("teacher1");
    ClassEnrollment teacherEnrollment = new ClassEnrollment();
    teacherEnrollment.setUser(teacherUser);
    teacherEnrollment.setClassRole(ClassRole.MENTOR);
    sc.getEnrollments().add(teacherEnrollment);

    SchoolClassDetailResponse result = dtoMapper.toSchoolClassDetailResponse(sc);

    assertThat(result.getStudents()).hasSize(1);
    assertThat(result.getStudents().get(0).getUsername()).isEqualTo("student1");
    assertThat(result.getTeachers()).hasSize(1);
    assertThat(result.getTeachers().get(0).getUsername()).isEqualTo("teacher1");
  }

  @Test
  @DisplayName("toUserResponse returns null if input is null")
  void toUserResponse_nullInput_returnsNull() {
    assertThat(dtoMapper.toUserResponse(null)).isNull();
  }
}
