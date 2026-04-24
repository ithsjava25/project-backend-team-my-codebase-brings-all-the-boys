package org.example.projectbackendteammycodebasebringsalltheboys.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.example.projectbackendteammycodebasebringsalltheboys.controller.AuthController;
import org.example.projectbackendteammycodebasebringsalltheboys.controller.CourseController;
import org.example.projectbackendteammycodebasebringsalltheboys.controller.PageController;
import org.example.projectbackendteammycodebasebringsalltheboys.controller.UserController;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.security.config.OAuth2LoginSuccessHandler;
import org.example.projectbackendteammycodebasebringsalltheboys.security.config.SecurityConfig;
import org.example.projectbackendteammycodebasebringsalltheboys.security.oauth.CustomOAuth2UserService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.ActivityLogService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.CourseService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.SchoolClassService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.example.projectbackendteammycodebasebringsalltheboys.testConfig.TestViewConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    excludeAutoConfiguration = OAuth2ClientAutoConfiguration.class,
    controllers = {
      PageController.class,
      AuthController.class,
      UserController.class,
      CourseController.class
    })
@Import({SecurityConfig.class, TestViewConfig.class})
@ActiveProfiles("test")
class CsrfTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private CustomOAuth2UserService customOAuth2UserService;

  @MockitoBean private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

  @MockitoBean private ClientRegistrationRepository clientRegistrationRepository;

  @MockitoBean private UserService userService;

  @MockitoBean private CourseService courseService;

  @MockitoBean private SchoolClassService schoolClassService;

  @MockitoBean private ActivityLogService activityLogService;

  @MockitoBean private DtoMapper dtoMapper;

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("POST /api/admin/users without CSRF token returns 403")
  void postWithoutCsrfToken_returnsForbidden() throws Exception {
    mockMvc
        .perform(
            post("/api/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                                        {
                                            "username": "testuser",
                                            "email": "test@example.com",
                                            "password": "password123",
                                            "roleName": "ROLE_STUDENT"
                                        }
                                        """))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName(
      "POST /api/admin/users with CSRF token is not blocked by CSRF (returns 2xx or 4xx, not 403)")
  void postWithCsrfToken_isNotBlockedByCsrf() throws Exception {
    mockMvc
        .perform(
            post("/api/admin/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                                        {
                                            "username": "testuser",
                                            "email": "test@example.com",
                                            "password": "password123",
                                            "roleName": "ROLE_STUDENT"
                                        }
                                        """))
        .andExpect(result -> assertThat(result.getResponse().getStatus()).isNotEqualTo(403));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("PUT /api/admin/users/{id} without CSRF token returns 403")
  void putWithoutCsrfToken_returnsForbidden() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/users/00000000-0000-0000-0000-000000000001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                                        {
                                            "username": "updateduser",
                                            "email": "updated@example.com",
                                            "roleName": "ROLE_STUDENT"
                                        }
                                        """))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("DELETE /api/admin/users/{id} without CSRF token returns 403")
  void deleteWithoutCsrfToken_returnsForbidden() throws Exception {
    mockMvc
        .perform(delete("/api/admin/users/00000000-0000-0000-0000-000000000001"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser
  @DisplayName("GET /api/courses without CSRF token is not blocked (CSRF does not apply to GET)")
  void getWithoutCsrfToken_isNotForbidden() throws Exception {
    mockMvc
        .perform(get("/api/courses"))
        .andExpect(result -> assertThat(result.getResponse().getStatus()).isNotEqualTo(403));
  }

  @Test
  @DisplayName("POST /api/auth/register without CSRF token is not blocked by CSRF")
  void publicEndpoint_register_isNotBlockedByCsrf() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                                        {
                                            "username": "newuser",
                                            "email": "new@example.com",
                                            "password": "newpassword"
                                        }
                                        """))
        .andExpect(result -> assertThat(result.getResponse().getStatus()).isNotEqualTo(403));
  }

  @Test
  @DisplayName("POST /api/auth/login without CSRF token is not blocked by CSRF")
  void publicEndpoint_login_isNotBlockedByCsrf() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                                        {
                                            "username": "someuser",
                                            "password": "somepassword"
                                        }
                                        """))
        .andExpect(result -> assertThat(result.getResponse().getStatus()).isNotEqualTo(403));
  }
}
