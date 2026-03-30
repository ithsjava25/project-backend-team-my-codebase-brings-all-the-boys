package org.example.projectbackendteammycodebasebringsalltheboys.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.example.projectbackendteammycodebasebringsalltheboys.controller.AuthController;
import org.example.projectbackendteammycodebasebringsalltheboys.controller.PageController;
import org.example.projectbackendteammycodebasebringsalltheboys.security.config.SecurityConfig;
import org.example.projectbackendteammycodebasebringsalltheboys.security.oauth.CustomOAuth2UserService;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.example.projectbackendteammycodebasebringsalltheboys.testConfig.TestViewConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {PageController.class, AuthController.class})
@Import({SecurityConfig.class, TestViewConfig.class})
class SecurityConfigTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private PasswordEncoder passwordEncoder;

  @MockitoBean private CustomOAuth2UserService customOAuth2UserService;

  @MockitoBean private UserService userService;

  @MockitoBean private ClientRegistrationRepository clientRegistrationRepository;

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
    mockMvc.perform(get("/admin")).andExpect(status().is3xxRedirection());
  }

  // --- Public endpoints ---

  @Test
  @DisplayName("GET /auth/login is accessible without authentication")
  void authLogin_isPublic() throws Exception {
    mockMvc.perform(get("/auth/login")).andExpect(status().isOk());
  }

  @Test
  @DisplayName("GET /auth/register is accessible without authentication")
  void authRegister_isPublic() throws Exception {
    mockMvc.perform(get("/auth/register")).andExpect(status().isOk());
  }

  @Test
  @DisplayName("GET /auth/logout-success is accessible without authentication")
  void authLogoutSuccess_isPublic() throws Exception {
    mockMvc.perform(get("/auth/logout-success")).andExpect(status().isOk());
  }

  @Test
  @DisplayName("GET /oauth2/** is not blocked by security")
  void oauth2Paths_arePublic() throws Exception {
    mockMvc.perform(get("/oauth2/callback")).andExpect(status().isNotFound());
  }

  // --- Protected endpoints: unauthenticated ---

  @Test
  @DisplayName("GET /dashboard unauthenticated redirects to login")
  void dashboard_unauthenticated_redirectsToLogin() throws Exception {
    mockMvc
        .perform(get("/dashboard"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/auth/login"));
  }

  @Test
  @DisplayName("GET /admin unauthenticated redirects to login")
  void admin_unauthenticated_redirectsToLogin() throws Exception {
    mockMvc
        .perform(get("/admin"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/auth/login"));
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
