package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.security.oauth.CustomOAuth2UserService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.ActivityLogService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.example.projectbackendteammycodebasebringsalltheboys.testConfig.TestViewConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ActivityLogController.class)
@AutoConfigureMockMvc
@Import(TestViewConfig.class)
class ActivityLogControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ActivityLogService activityLogService;

  @MockitoBean private UserService userService;

  @MockitoBean private DtoMapper dtoMapper;

  @MockitoBean private CustomOAuth2UserService customOAuth2UserService;

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"ADMIN"})
  void getUserActivityLogs_AdminAccess() throws Exception {
    UUID adminId = UUID.randomUUID();
    UUID targetUserId = UUID.randomUUID();

    User admin = new User();
    admin.setId(adminId);
    admin.setUsername("admin");
    admin.setRole(new Role("ROLE_ADMIN"));

    User targetUser = new User();
    targetUser.setId(targetUserId);
    targetUser.setUsername("alice_student");
    targetUser.setRole(new Role("ROLE_STUDENT"));

    Page<?> emptyPage = new PageImpl<>(Collections.emptyList());

    Mockito.when(userService.getUserByUsername("admin")).thenReturn(Optional.of(admin));
    Mockito.when(userService.getUserById(targetUserId)).thenReturn(Optional.of(targetUser));
    Mockito.when(
            activityLogService.getLogsForUser(Mockito.eq(targetUser), Mockito.any(Pageable.class)))
        .thenReturn((Page) emptyPage);

    mockMvc
        .perform(
            get("/api/activity-logs/user/{userId}", targetUserId)
                .param("page", "0")
                .param("size", "10"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "alice_student",
      roles = {"STUDENT"})
  void getUserActivityLogs_UserAccessOwnLogs() throws Exception {
    UUID aliceId = UUID.randomUUID();

    User alice = new User();
    alice.setId(aliceId);
    alice.setUsername("alice_student");
    alice.setRole(new Role("ROLE_STUDENT"));

    Page<?> emptyPage = new PageImpl<>(Collections.emptyList());

    Mockito.when(userService.getUserByUsername("alice_student")).thenReturn(Optional.of(alice));
    Mockito.when(userService.getUserById(aliceId)).thenReturn(Optional.of(alice));
    Mockito.when(activityLogService.getLogsForUser(Mockito.eq(alice), Mockito.any(Pageable.class)))
        .thenReturn((Page) emptyPage);

    mockMvc
        .perform(
            get("/api/activity-logs/user/{userId}", aliceId).param("page", "0").param("size", "10"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "bob_student",
      roles = {"STUDENT"})
  void getUserActivityLogs_StudentAccessOtherUserLogs_Forbidden() throws Exception {
    UUID bobId = UUID.randomUUID();
    UUID aliceId = UUID.randomUUID();

    User bob = new User();
    bob.setId(bobId);
    bob.setUsername("bob_student");
    bob.setRole(new Role("ROLE_STUDENT"));

    Mockito.when(userService.getUserByUsername("bob_student")).thenReturn(Optional.of(bob));

    mockMvc
        .perform(
            get("/api/activity-logs/user/{userId}", aliceId).param("page", "0").param("size", "10"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(
      username = "admin",
      roles = {"ADMIN"})
  void getEntityActivityLogs_AdminAccess() throws Exception {
    UUID adminId = UUID.randomUUID();
    UUID entityId = UUID.randomUUID();

    User admin = new User();
    admin.setId(adminId);
    admin.setUsername("admin");
    admin.setRole(new Role("ROLE_ADMIN"));

    Page<?> emptyPage = new PageImpl<>(Collections.emptyList());

    Mockito.when(userService.getUserByUsername("admin")).thenReturn(Optional.of(admin));
    Mockito.when(
            activityLogService.getLogsForParent(
                Mockito.eq(EntityType.ASSIGNMENT),
                Mockito.eq(entityId),
                Mockito.any(Pageable.class)))
        .thenReturn((Page) emptyPage);

    mockMvc
        .perform(
            get("/api/activity-logs/entity/{entityType}/{entityId}", "ASSIGNMENT", entityId)
                .param("page", "0")
                .param("size", "10"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(
      username = "teacher",
      roles = {"TEACHER"})
  void getEntityActivityLogs_TeacherAccess_Forbidden() throws Exception {
    UUID teacherId = UUID.randomUUID();
    UUID entityId = UUID.randomUUID();

    User teacher = new User();
    teacher.setId(teacherId);
    teacher.setUsername("teacher");
    teacher.setRole(new Role("ROLE_TEACHER"));

    Mockito.when(userService.getUserByUsername("teacher")).thenReturn(Optional.of(teacher));

    mockMvc
        .perform(
            get("/api/activity-logs/entity/{entityType}/{entityId}", "ASSIGNMENT", entityId)
                .param("page", "0")
                .param("size", "10"))
        .andExpect(status().isForbidden());
  }
}
