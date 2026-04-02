package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.RegistrationRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.RoleResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.UserResponse;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.security.config.SecurityConfig;
import org.example.projectbackendteammycodebasebringsalltheboys.security.oauth.CustomOAuth2UserService;
import org.example.projectbackendteammycodebasebringsalltheboys.security.config.OAuth2LoginSuccessHandler;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@TestPropertySource(
    properties = {
      "spring.security.oauth2.client.registration.github.client-id=test-id",
      "spring.security.oauth2.client.registration.github.client-secret=test-secret",
      "frontend.url=http://localhost:3000"
    })
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private UserService userService;
  @MockitoBean private CustomOAuth2UserService customOAuth2UserService;
  @MockitoBean private ClientRegistrationRepository clientRegistrationRepository;
  @MockitoBean private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

  private final ObjectMapper objectMapper = new ObjectMapper();

  // --- POST /api/auth/register ---

  @Test
  @DisplayName("POST /api/auth/register with valid data returns 200 OK")
  void registerUser_validRequest_returnsOk() throws Exception {
    RegistrationRequest request = new RegistrationRequest();
    request.setUsername("test@example.com");
    request.setEmail("test@example.com");
    request.setPassword("password123");
    request.setConfirmPassword("password123");

    User user = new User();
    user.setUsername("user");

    Role role = new Role();
    role.setName("ROLE_STUDENT");
    user.setRole(role);

    UserResponse userResponse = new UserResponse();
    userResponse.setUsername("user");

    RoleResponse roleResponse = new RoleResponse();
    roleResponse.setName("ROLE_STUDENT");
    userResponse.setRole(roleResponse);

    when(userService.registerUser(any(RegistrationRequest.class))).thenReturn(user);
    when(userService.toUserResponse(any(User.class))).thenReturn(userResponse);

    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("user"))
        .andExpect(jsonPath("$.role.name").value("ROLE_STUDENT"));
  }

  @Test
  @DisplayName("POST /api/auth/register with duplicate username returns 400 Bad Request")
  void registerUser_duplicateUsername_returnsBadRequest() throws Exception {
    when(userService.registerUser(any(RegistrationRequest.class)))
        .thenThrow(new IllegalStateException("Username already taken"));

    RegistrationRequest request = new RegistrationRequest();
    request.setUsername("test@example.com");
    request.setEmail("test@example.com");
    request.setPassword("password123");
    request.setConfirmPassword("password123");

    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Username already taken"));

    verify(userService).registerUser(any(RegistrationRequest.class));
  }

  // --- GET /api/auth/me ---

  @Test
  @DisplayName("GET /api/auth/me authenticated returns user info")
  void getCurrentUser_authenticated_returnsUserInfo() throws Exception {
    User user = new User();
    user.setUsername("test@example.com");

    Role role = new Role();
    role.setName("ROLE_STUDENT");
    user.setRole(role);

    UserResponse userResponse = new UserResponse();
    userResponse.setUsername("test@example.com");
    userResponse.setEmail("test@example.com");

    RoleResponse roleResponse = new RoleResponse();
    roleResponse.setName("ROLE_STUDENT");
    userResponse.setRole(roleResponse);

    when(userService.toUserResponse(any(User.class))).thenReturn(userResponse);
    when(userService.getUserByUsername("test@example.com")).thenReturn(Optional.of(user));

    mockMvc
        .perform(
            get("/api/auth/me").with(SecurityMockMvcRequestPostProcessors.user("test@example.com")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("test@example.com"))
        .andExpect(jsonPath("$.role.name").value("ROLE_STUDENT"));
  }

  @Test
  @DisplayName("GET /api/auth/me unauthenticated returns 401")
  void getCurrentUser_unauthenticated_returnsUnauthorized() throws Exception {
    mockMvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
  }
}
