package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseDetailResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.course.CourseSurfaceResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Course;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.service.CourseService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.example.projectbackendteammycodebasebringsalltheboys.testConfig.NoSecurityWebMvcTest; // ✅ RÄTT IMPORT
import org.example.projectbackendteammycodebasebringsalltheboys.testConfig.TestViewConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@NoSecurityWebMvcTest(CourseController.class) // ✅ ANVÄND DETTA
@Import(TestViewConfig.class)
@ActiveProfiles("test")
class CourseControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private CourseService courseService;
  @MockitoBean private DtoMapper dtoMapper;
  @MockitoBean private UserService userService;

  @Test
  @DisplayName("GET /api/courses returns courses for authenticated user")
  @WithMockUser(
      username = "student",
      roles = {"STUDENT"})
  void getAccessibleCourses_returnsCourses() throws Exception {
    Course course1 = new Course();
    course1.setId(UUID.randomUUID());
    course1.setName("Java 1");

    Course course2 = new Course();
    course2.setId(UUID.randomUUID());
    course2.setName("Math 101");

    User mockUser = new User();
    mockUser.setId(UUID.randomUUID());
    mockUser.setUsername("student");

    Page<Course> mockPage = new PageImpl<>(List.of(course1, course2));

    when(userService.getUserByUsername("student")).thenReturn(Optional.of(mockUser));
    when(courseService.getAccessibleCourses(eq(mockUser), any(Pageable.class)))
        .thenReturn(mockPage);
    when(dtoMapper.toCourseSurfaceResponse(course1)).thenReturn(createResponse("Java 1"));
    when(dtoMapper.toCourseSurfaceResponse(course2)).thenReturn(createResponse("Math 101"));

    mockMvc
        .perform(get("/api/courses"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2));
  }

  @Test
  @DisplayName("GET /api/courses/{id} returns course when user has access")
  @WithMockUser(
      username = "teacher",
      roles = {"TEACHER"})
  void getCourseById_withAccess_returns200() throws Exception {
    UUID id = UUID.randomUUID();
    Course course = new Course();
    course.setId(id);
    course.setName("Physics");

    User mockUser = new User();
    mockUser.setId(UUID.randomUUID());
    mockUser.setUsername("teacher");

    when(userService.getUserByUsername("teacher")).thenReturn(Optional.of(mockUser));
    when(courseService.getAccessibleCourse(eq(id), eq(mockUser))).thenReturn(Optional.of(course));
    when(dtoMapper.toCourseDetailResponse(course)).thenReturn(new CourseDetailResponse());

    mockMvc.perform(get("/api/courses/" + id)).andExpect(status().isOk());
  }

  @Test
  @DisplayName("GET /api/courses/{id} returns 404 when user lacks access")
  @WithMockUser(
      username = "student",
      roles = {"STUDENT"})
  void getCourseById_withoutAccess_returns404() throws Exception {
    UUID id = UUID.randomUUID();

    User mockUser = new User();
    mockUser.setId(UUID.randomUUID());
    mockUser.setUsername("student");

    when(userService.getUserByUsername("student")).thenReturn(Optional.of(mockUser));
    when(courseService.getAccessibleCourse(eq(id), eq(mockUser))).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/courses/" + id)).andExpect(status().isNotFound());
  }

  private CourseSurfaceResponse createResponse(String name) {
    CourseSurfaceResponse response = new CourseSurfaceResponse();
    response.setName(name);
    return response;
  }
}
