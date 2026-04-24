package org.example.projectbackendteammycodebasebringsalltheboys.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CsrfTest {

  @Autowired private MockMvc mockMvc;

  // -------------------------------------------------------------------------
  // POST
  // -------------------------------------------------------------------------

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

  // -------------------------------------------------------------------------
  // PUT
  // -------------------------------------------------------------------------

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

  // -------------------------------------------------------------------------
  // DELETE
  // -------------------------------------------------------------------------

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("DELETE /api/admin/users/{id} without CSRF token returns 403")
  void deleteWithoutCsrfToken_returnsForbidden() throws Exception {
    mockMvc
        .perform(delete("/api/admin/users/00000000-0000-0000-0000-000000000001"))
        .andExpect(status().isForbidden());
  }

  // -------------------------------------------------------------------------
  // GET – ska aldrig kräva CSRF
  // -------------------------------------------------------------------------

  @Test
  @WithMockUser
  @DisplayName("GET /api/courses without CSRF token is not blocked (CSRF does not apply to GET)")
  void getWithoutCsrfToken_isNotForbidden() throws Exception {
    mockMvc
        .perform(get("/api/courses"))
        .andExpect(result -> assertThat(result.getResponse().getStatus()).isNotEqualTo(403));
  }

  // -------------------------------------------------------------------------
  // Public endpoints – ignoringRequestMatchers ska gälla
  // -------------------------------------------------------------------------

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
                            "password": "password123"
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
