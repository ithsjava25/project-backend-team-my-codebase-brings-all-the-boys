package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.ClassEnrollment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ClassRole;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.security.oauth.CustomOAuth2UserService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.SchoolClassService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.example.projectbackendteammycodebasebringsalltheboys.testConfig.TestViewConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = SchoolClassController.class)
@AutoConfigureMockMvc
@Import(TestViewConfig.class)
class SchoolClassControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private SchoolClassService schoolClassService;
  @MockitoBean private UserService userService;
  @MockitoBean private DtoMapper dtoMapper;
  @MockitoBean private CustomOAuth2UserService customOAuth2UserService;

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"ADMIN"})
  void getAllSchoolClasses_AdminAccess() throws Exception {
    Mockito.when(schoolClassService.getAllSchoolClasses()).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/api/school-classes")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "teacher",
      roles = {"TEACHER"})
  void getAllSchoolClasses_TeacherAccess() throws Exception {
    Mockito.when(schoolClassService.getAllSchoolClasses()).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/api/school-classes")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "student",
      roles = {"STUDENT"})
  void getAllSchoolClasses_StudentAccess() throws Exception {
    Mockito.when(schoolClassService.getAllSchoolClasses()).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/api/school-classes")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "student",
      roles = {"STUDENT"})
  void getSchoolClassById_AuthorizedStudent() throws Exception {
    UUID classId = UUID.randomUUID();
    UUID studentId = UUID.randomUUID();

    User student = new User();
    student.setId(studentId);
    student.setUsername("student");
    student.setRole(new Role("ROLE_STUDENT"));

    ClassEnrollment enrollment = new ClassEnrollment();
    enrollment.setUser(student);
    enrollment.setClassRole(ClassRole.STUDENT);

    SchoolClass schoolClass = new SchoolClass();
    schoolClass.setId(classId);
    schoolClass.setEnrollments(new ArrayList<>(Collections.singletonList(enrollment)));

    SchoolClassDetailResponse response = Mockito.mock(SchoolClassDetailResponse.class);

    Mockito.when(userService.getUserByUsername("student")).thenReturn(Optional.of(student));
    Mockito.when(schoolClassService.getSchoolClassById(classId))
        .thenReturn(Optional.of(schoolClass));
    Mockito.when(dtoMapper.toSchoolClassDetailResponse(schoolClass)).thenReturn(response);

    mockMvc.perform(get("/api/school-classes/{id}", classId)).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "unauthorized_user",
      roles = {"USER"})
  void getSchoolClassById_UnauthorizedUser() throws Exception {
    UUID classId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    User unauthorizedUser = new User();
    unauthorizedUser.setId(userId);
    unauthorizedUser.setUsername("unauthorized_user");
    unauthorizedUser.setRole(new Role("ROLE_USER"));

    SchoolClass schoolClass = new SchoolClass();
    schoolClass.setId(classId);
    schoolClass.setEnrollments(new ArrayList<>());

    Mockito.when(userService.getUserByUsername("unauthorized_user"))
        .thenReturn(Optional.of(unauthorizedUser));
    Mockito.when(schoolClassService.getSchoolClassById(classId))
        .thenReturn(Optional.of(schoolClass));

    mockMvc.perform(get("/api/school-classes/{id}", classId)).andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
      username = "teacher",
      roles = {"TEACHER"})
  void getSchoolClassById_TeacherAccess() throws Exception {
    UUID classId = UUID.randomUUID();
    UUID teacherId = UUID.randomUUID();

    User teacher = new User();
    teacher.setId(teacherId);
    teacher.setUsername("teacher");
    teacher.setRole(new Role("ROLE_TEACHER"));

    SchoolClass schoolClass = new SchoolClass();
    schoolClass.setId(classId);
    schoolClass.setEnrollments(new ArrayList<>());

    SchoolClassDetailResponse response = Mockito.mock(SchoolClassDetailResponse.class);

    Mockito.when(userService.getUserByUsername("teacher")).thenReturn(Optional.of(teacher));
    Mockito.when(schoolClassService.getSchoolClassById(classId))
        .thenReturn(Optional.of(schoolClass));
    Mockito.when(dtoMapper.toSchoolClassDetailResponse(schoolClass)).thenReturn(response);

    mockMvc.perform(get("/api/school-classes/{id}", classId)).andExpect(status().isOk());
  }

  @Test
  void getSchoolClassById_Unauthenticated() throws Exception {
    UUID classId = UUID.randomUUID();

    mockMvc
        .perform(get("/api/school-classes/{id}", classId))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("/oauth2/authorization/github"));
  }

  @Test
  @WithMockUser(
      username = "student",
      roles = {"STUDENT"})
  void getSchoolClassById_NotFound() throws Exception {
    UUID classId = UUID.randomUUID();
    UUID studentId = UUID.randomUUID();

    User student = new User();
    student.setId(studentId);
    student.setUsername("student");
    student.setRole(new Role("ROLE_STUDENT"));

    Mockito.when(userService.getUserByUsername("student")).thenReturn(Optional.of(student));
    Mockito.when(schoolClassService.getSchoolClassById(classId)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/school-classes/{id}", classId)).andExpect(status().isNotFound());
  }
}
