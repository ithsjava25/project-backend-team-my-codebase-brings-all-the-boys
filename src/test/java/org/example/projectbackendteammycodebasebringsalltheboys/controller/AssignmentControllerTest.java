package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment.AssignmentDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment.AssignmentResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.casefile.CaseResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.security.oauth.CustomOAuth2UserService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.AuthorizationService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.CaseService;
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

@WebMvcTest(controllers = AssignmentController.class)
@AutoConfigureMockMvc
@Import(TestViewConfig.class)
class AssignmentControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private CaseService caseService;

  @MockitoBean private UserService userService;

  @MockitoBean private AuthorizationService authorizationService;

  @MockitoBean private DtoMapper dtoMapper;

  @MockitoBean private CustomOAuth2UserService customOAuth2UserService;

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"ADMIN"})
  void getAllAssignments_AdminAccess() throws Exception {
    User admin = new User();
    admin.setUsername("admin");
    admin.setRole(new Role("ROLE_ADMIN"));

    Assignment assignment = new Assignment();
    assignment.setId(UUID.randomUUID());

    AssignmentResponse response = Mockito.mock(AssignmentResponse.class);

    Mockito.when(userService.getUserByUsername("admin")).thenReturn(Optional.of(admin));
    Mockito.when(caseService.getAllCases()).thenReturn(List.of(assignment));
    Mockito.when(authorizationService.canViewAssignment(admin, assignment)).thenReturn(true);
    Mockito.when(dtoMapper.toAssignmentResponse(assignment)).thenReturn(response);

    mockMvc.perform(get("/api/assignments")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "student",
      roles = {"STUDENT"})
  void getAllAssignments_StudentAccess() throws Exception {
    User student = new User();
    student.setUsername("student");
    student.setRole(new Role("ROLE_STUDENT"));

    Assignment assignment = new Assignment();
    assignment.setId(UUID.randomUUID());

    AssignmentResponse response = Mockito.mock(AssignmentResponse.class);

    Mockito.when(userService.getUserByUsername("student")).thenReturn(Optional.of(student));
    Mockito.when(caseService.getAllCases()).thenReturn(List.of(assignment));
    Mockito.when(authorizationService.canViewAssignment(student, assignment)).thenReturn(true);
    Mockito.when(dtoMapper.toAssignmentResponse(assignment)).thenReturn(response);

    mockMvc.perform(get("/api/assignments")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "student",
      roles = {"STUDENT"})
  void getAssignmentById_AuthorizedStudent() throws Exception {
    UUID id = UUID.randomUUID();

    User student = new User();
    student.setUsername("student");
    student.setRole(new Role("ROLE_STUDENT"));

    Assignment assignment = new Assignment();
    assignment.setId(id);

    AssignmentDetailResponse response = Mockito.mock(AssignmentDetailResponse.class);

    Mockito.when(userService.getUserByUsername("student")).thenReturn(Optional.of(student));
    Mockito.when(caseService.getCaseById(id)).thenReturn(Optional.of(assignment));
    Mockito.when(authorizationService.canAccessAssignmentDetails(student, assignment))
        .thenReturn(true);
    Mockito.when(dtoMapper.toAssignmentDetailResponse(assignment)).thenReturn(response);

    mockMvc.perform(get("/api/assignments/{id}", id)).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "user",
      roles = {"USER"})
  void getAssignmentById_UserAccess() throws Exception {
    UUID id = UUID.randomUUID();

    User user = new User();
    user.setUsername("user");
    user.setRole(new Role("ROLE_USER"));

    Assignment assignment = new Assignment();
    assignment.setId(id);

    AssignmentDetailResponse response = Mockito.mock(AssignmentDetailResponse.class);

    Mockito.when(userService.getUserByUsername("user")).thenReturn(Optional.of(user));
    Mockito.when(caseService.getCaseById(id)).thenReturn(Optional.of(assignment));
    Mockito.when(authorizationService.canAccessAssignmentDetails(user, assignment))
        .thenReturn(true);
    Mockito.when(dtoMapper.toAssignmentDetailResponse(assignment)).thenReturn(response);

    mockMvc.perform(get("/api/assignments/{id}", id)).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "teacher",
      roles = {"TEACHER"})
  void getMyCreatedAssignments_Success() throws Exception {
    User teacher = new User();
    teacher.setUsername("teacher");
    teacher.setRole(new Role("ROLE_TEACHER"));

    Assignment assignment = new Assignment();
    assignment.setId(UUID.randomUUID());

    CaseResponse response = Mockito.mock(CaseResponse.class);

    Mockito.when(userService.getUserByUsername("teacher")).thenReturn(Optional.of(teacher));
    Mockito.when(caseService.getCasesByCreator(teacher)).thenReturn(List.of(assignment));
    Mockito.when(dtoMapper.toCaseResponse(assignment)).thenReturn(response);

    mockMvc.perform(get("/api/assignments/my-created")).andExpect(status().isOk());
  }
}
