package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.ForbiddenException;
import org.example.projectbackendteammycodebasebringsalltheboys.exception.NotFoundException;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.security.oauth.CustomOAuth2UserService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.ClassEnrollmentService;
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
  @MockitoBean private ClassEnrollmentService classEnrollmentService;
  @MockitoBean private UserService userService;
  @MockitoBean private DtoMapper dtoMapper;
  @MockitoBean private CustomOAuth2UserService customOAuth2UserService;

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"ADMIN"})
  void getAllSchoolClasses_AdminAccess() throws Exception {
    User admin = new User();
    admin.setUsername("admin");
    admin.setRole(new Role("ROLE_ADMIN"));

    Mockito.when(userService.getUserByUsername("admin")).thenReturn(Optional.of(admin));
    Mockito.when(
            schoolClassService.getAccessibleSchoolClassesDto(
                Mockito.eq(admin), any(org.springframework.data.domain.Pageable.class)))
        .thenReturn(Collections.emptyList());

    mockMvc.perform(get("/api/school-classes")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "teacher",
      roles = {"TEACHER"})
  void getAllSchoolClasses_TeacherAccess() throws Exception {
    User teacher = new User();
    teacher.setUsername("teacher");
    teacher.setRole(new Role("ROLE_TEACHER"));

    Mockito.when(userService.getUserByUsername("teacher")).thenReturn(Optional.of(teacher));
    Mockito.when(
            schoolClassService.getAccessibleSchoolClassesDto(
                Mockito.eq(teacher), any(org.springframework.data.domain.Pageable.class)))
        .thenReturn(Collections.emptyList());

    mockMvc.perform(get("/api/school-classes")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "student",
      roles = {"STUDENT"})
  void getAllSchoolClasses_StudentAccess() throws Exception {
    User student = new User();
    student.setUsername("student");
    student.setRole(new Role("ROLE_STUDENT"));

    Mockito.when(userService.getUserByUsername("student")).thenReturn(Optional.of(student));
    Mockito.when(
            schoolClassService.getAccessibleSchoolClassesDto(
                Mockito.eq(student), any(org.springframework.data.domain.Pageable.class)))
        .thenReturn(Collections.emptyList());

    mockMvc.perform(get("/api/school-classes")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "student",
      roles = {"STUDENT"})
  void getSchoolClassById_AuthorizedStudent() throws Exception {
    UUID classId = UUID.randomUUID();
    User student = new User();
    student.setUsername("student");
    student.setRole(new Role("ROLE_STUDENT"));

    SchoolClassDetailResponse response = new SchoolClassDetailResponse();

    Mockito.when(userService.getUserByUsername("student")).thenReturn(Optional.of(student));
    Mockito.when(schoolClassService.getSchoolClassDetailDto(classId, student)).thenReturn(response);

    mockMvc.perform(get("/api/school-classes/{id}", classId)).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "unauthorized_user",
      roles = {"USER"})
  void getSchoolClassById_UnauthorizedUser() throws Exception {
    UUID classId = UUID.randomUUID();
    User unauthorizedUser = new User();
    unauthorizedUser.setUsername("unauthorized_user");
    unauthorizedUser.setRole(new Role("ROLE_USER"));

    Mockito.when(userService.getUserByUsername("unauthorized_user"))
        .thenReturn(Optional.of(unauthorizedUser));
    Mockito.when(schoolClassService.getSchoolClassDetailDto(classId, unauthorizedUser))
        .thenThrow(new ForbiddenException("Access denied"));

    mockMvc.perform(get("/api/school-classes/{id}", classId)).andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
      username = "teacher",
      roles = {"TEACHER"})
  void getSchoolClassById_TeacherAccess() throws Exception {
    UUID classId = UUID.randomUUID();
    User teacher = new User();
    teacher.setUsername("teacher");
    teacher.setRole(new Role("ROLE_TEACHER"));

    SchoolClassDetailResponse response = new SchoolClassDetailResponse();

    Mockito.when(userService.getUserByUsername("teacher")).thenReturn(Optional.of(teacher));
    Mockito.when(schoolClassService.getSchoolClassDetailDto(classId, teacher)).thenReturn(response);

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
    User student = new User();
    student.setUsername("student");
    student.setRole(new Role("ROLE_STUDENT"));

    Mockito.when(userService.getUserByUsername("student")).thenReturn(Optional.of(student));
    Mockito.when(schoolClassService.getSchoolClassDetailDto(classId, student))
        .thenThrow(new NotFoundException("Not found"));

    mockMvc.perform(get("/api/school-classes/{id}", classId)).andExpect(status().isNotFound());
  }
}
