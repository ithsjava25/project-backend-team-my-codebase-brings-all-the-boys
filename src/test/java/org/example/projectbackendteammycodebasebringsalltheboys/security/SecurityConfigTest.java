package org.example.projectbackendteammycodebasebringsalltheboys.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.example.projectbackendteammycodebasebringsalltheboys.controller.AuthController;
import org.example.projectbackendteammycodebasebringsalltheboys.controller.PageController;
import org.example.projectbackendteammycodebasebringsalltheboys.security.config.SecurityConfig;
import org.example.projectbackendteammycodebasebringsalltheboys.security.oauth.CustomOAuth2UserService;
import org.example.projectbackendteammycodebasebringsalltheboys.security.oauth.OAuth2LoginSuccessHandler;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.example.projectbackendteammycodebasebringsalltheboys.testConfig.TestViewConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    excludeAutoConfiguration = OAuth2ClientAutoConfiguration.class,
    controllers = {PageController.class, AuthController.class})
@Import({SecurityConfig.class, TestViewConfig.class})
class SecurityConfigTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private PasswordEncoder passwordEncoder;
  @MockitoBean private CustomOAuth2UserService customOAuth2UserService;
  @MockitoBean private UserService userService;
  @MockitoBean private ClientRegistrationRepository clientRegistrationRepository;
  @MockitoBean private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("GET /admin with ADMIN role returns admin view")
  void adminPage_withAdminRole_returnsAdminView() throws Exception {
    mockMvc.perform(get("/admin")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = "USER")
  @DisplayName("GET /admin with USER role returns 403")
  void adminPage_withUserRole_returnsForbidden() throws Exception {
    mockMvc.perform(get("/admin")).andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("GET /admin unauthenticated redirects to login")
  void adminPage_unauthenticated_redirectsToLogin() throws Exception {
    mockMvc.perform(get("/admin")).andExpect(status().isUnauthorized());
  }

  // --- Public endpoints ---
  @Test
  @DisplayName("GET /api/auth/me is public (returns 401 when not authenticated)")
  void apiAuthMe_unauthenticated_returnsUnauthorized() throws Exception {
    mockMvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("POST /api/auth/register is public")
  void apiAuthRegister_isPublic() throws Exception {
    mockMvc
        .perform(post("/api/auth/register"))
        .andExpect(status().isBadRequest()); // 400 missing body
  }

  @Test
  @DisplayName("GET /oauth2/** is not blocked by security")
  void oauth2Paths_arePublic() throws Exception {
    mockMvc.perform(get("/oauth2/callback")).andExpect(status().isNotFound());
  }

  // --- Protected endpoints: unauthenticated ---

  @Test
  @DisplayName("GET /dashboard unauthenticated returns 401")
  void dashboard_unauthenticated_returnsUnauthorized() throws Exception {
    mockMvc.perform(get("/dashboard")).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("GET /admin unauthenticated returns 401")
  void admin_unauthenticated_returnsUnauthorized() throws Exception {
    mockMvc.perform(get("/admin")).andExpect(status().isUnauthorized());
  }

  // --- Protected endpoints: authenticated as USER ---

  @Test
  @WithMockUser(roles = "USER")
  @DisplayName("GET /dashboard is accessible to authenticated USER")
  void dashboard_authenticatedUser_isAccessible() throws Exception {
    mockMvc.perform(get("/dashboard")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = "USER")
  @DisplayName("GET /admin/** returns 403 for authenticated USER")
  void admin_authenticatedUser_isForbidden() throws Exception {
    mockMvc.perform(get("/admin/dashboard")).andExpect(status().isForbidden());
  }

  // --- Admin endpoints ---

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("GET /admin/** is accessible to ADMIN")
  void admin_authenticatedAdmin_isAccessible() throws Exception {
    mockMvc.perform(get("/admin")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = "USER")
  @DisplayName("GET /admin/** returns 403 for non-admin roles")
  void admin_nonAdminRole_isForbidden() throws Exception {
    mockMvc.perform(get("/admin/anything")).andExpect(status().isForbidden());
  }

  // --- PasswordEncoder bean ---

  @Test
  @DisplayName("PasswordEncoder bean is BCryptPasswordEncoder")
  void passwordEncoder_isBCrypt() {
    assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
  }

  @Test
  @DisplayName("PasswordEncoder correctly encodes and matches a password")
  void passwordEncoder_encodesAndMatchesPassword() {
    String raw = "mySecret123";
    String encoded = passwordEncoder.encode(raw);

    assertThat(encoded).isNotEqualTo(raw);
    assertThat(passwordEncoder.matches(raw, encoded)).isTrue();
  }

  @Test
  @DisplayName("PasswordEncoder produces different hashes for the same input (salted)")
  void passwordEncoder_producesDifferentHashesForSameInput() {
    String raw = "mySecret123";
    String first = passwordEncoder.encode(raw);
    String second = passwordEncoder.encode(raw);

    assertThat(first).isNotEqualTo(second);
  }

  @Test
  @DisplayName("PasswordEncoder does not match wrong password")
  void passwordEncoder_doesNotMatchWrongPassword() {
    String encoded = passwordEncoder.encode("correct");

    assertThat(passwordEncoder.matches("wrong", encoded)).isFalse();
  }
}
