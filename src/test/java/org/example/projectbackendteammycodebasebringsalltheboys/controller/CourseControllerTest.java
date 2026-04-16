package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Course;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.security.oauth.CustomOAuth2UserService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.AuthorizationService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.CourseService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CourseController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CourseControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private CourseService courseService;
  @MockitoBean private UserService userService;
  @MockitoBean private AuthorizationService authorizationService;
  @MockitoBean private DtoMapper dtoMapper;
  @MockitoBean private CustomOAuth2UserService customOAuth2UserService;

  private User mockUser() {
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setUsername("testuser");
    Role role = new Role();
    role.setName("ROLE_ADMIN");
    user.setRole(role);
    return user;
  }

  @Test
  @DisplayName("GET /admin/courses/{id} returns 200 when found")
  @WithMockUser(
      username = "testuser",
      roles = {"ADMIN"})
  void getCourseById_found_returns200() throws Exception {
    UUID id = UUID.randomUUID();
    Course course = new Course();
    course.setId(id);

    when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(mockUser()));
    when(courseService.getCourseById(id)).thenReturn(Optional.of(course));
    when(dtoMapper.toCourseDetailResponse(course)).thenReturn(new CourseDetailResponse());

    mockMvc.perform(get("/admin/courses/" + id)).andExpect(status().isOk());
  }

  @Test
  @DisplayName("GET /admin/courses/{id} returns 404 when not found")
  @WithMockUser(
      username = "testuser",
      roles = {"ADMIN"})
  void getCourseById_notFound_returns404() throws Exception {
    UUID id = UUID.randomUUID();
    when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(mockUser()));
    when(courseService.getCourseById(id)).thenReturn(Optional.empty());

    mockMvc.perform(get("/admin/courses/" + id)).andExpect(status().isNotFound());
  }
}
