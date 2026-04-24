package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.assignment.UserAssignmentResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.UserAssignment;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.AssignmentRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserAssignmentRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.service.AuthorizationService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserAssignmentService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.example.projectbackendteammycodebasebringsalltheboys.testConfig.TestViewConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UserAssignmentController.class)
@AutoConfigureMockMvc
@Import(TestViewConfig.class)
class UserAssignmentControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private UserAssignmentService userAssignmentService;
  @MockitoBean private UserAssignmentRepository userAssignmentRepository;
  @MockitoBean private AssignmentRepository assignmentRepository;
  @MockitoBean private UserRepository userRepository;
  @MockitoBean private UserService userService;
  @MockitoBean private AuthorizationService authorizationService;
  @MockitoBean private DtoMapper dtoMapper;

  @Test
  @WithMockUser(
      username = "student",
      roles = {"STUDENT"})
  void getMyAssignment_Success() throws Exception {
    UUID assignmentId = UUID.randomUUID();
    User student = new User();
    student.setUsername("student");
    Assignment assignment = new Assignment();

    when(userService.getUserByUsername("student")).thenReturn(Optional.of(student));
    when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
    when(userAssignmentService.getOrCreateForStudent(assignment, student))
        .thenReturn(new UserAssignment());

    mockMvc.perform(get("/api/user-assignments/my/{id}", assignmentId)).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "teacher",
      roles = {"TEACHER"})
  void getEvaluatedAssignments_Success() throws Exception {
    User teacher = new User();
    when(userService.getCurrentUser()).thenReturn(teacher);

    mockMvc.perform(get("/api/user-assignments/evaluated")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "student",
      roles = {"STUDENT"})
  void submitWork_Success() throws Exception {
    UUID uaId = UUID.randomUUID();
    User student = new User();
    student.setId(UUID.randomUUID());
    UserAssignment ua = new UserAssignment();
    ua.setStudent(student);

    when(userAssignmentRepository.findById(uaId)).thenReturn(Optional.of(ua));
    when(userService.getCurrentUser()).thenReturn(student);

    UserAssignmentResponse response = new UserAssignmentResponse();
    response.setId(uaId);
    when(dtoMapper.toUserAssignmentResponse(any())).thenReturn(response);

    String body =
        """
        {
          "content": "My work",
          "fileS3Keys": []
        }
        """;

    mockMvc
        .perform(
            post("/api/user-assignments/{id}/submit", uaId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(uaId.toString()));
  }
}
