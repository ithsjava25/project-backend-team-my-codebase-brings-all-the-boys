package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import org.example.projectbackendteammycodebasebringsalltheboys.testConfig.SharedWebMvcTest;
import org.example.projectbackendteammycodebasebringsalltheboys.testConfig.TestViewConfig;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.user.RegistrationRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.instanceOf;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SharedWebMvcTest(AuthController.class)
@Import(TestViewConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    // --- GET /auth/register ---

    @Test
    @DisplayName("GET /auth/register returns register view with empty RegistrationRequest")
    void showRegistrationForm_returnsRegisterView() throws Exception {
        mockMvc.perform(get("/auth/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registrationRequest"))
                .andExpect(model().attribute("registrationRequest",
                        instanceOf(RegistrationRequest.class)));
    }

    // --- POST /auth/register ---

    @Test
    @DisplayName("POST /auth/register with valid data redirects to login with registered param")
    void registerUser_validRequest_redirectsToLogin() throws Exception {
        when(userService.registerUser(any(RegistrationRequest.class))).thenReturn(new User());

        mockMvc.perform(post("/auth/register")
                        .param("username", "john@example.com")
                        .param("password", "secret123")
                        .param("confirmPassword", "secret123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?registered"));

        verify(userService, times(1)).registerUser(any(RegistrationRequest.class));
    }

    @Test
    @DisplayName("POST /auth/register with validation errors returns register view without calling service")
    void registerUser_validationErrors_returnsRegisterView() throws Exception {
        mockMvc.perform(post("/auth/register")
                        // sending no params to trigger @Valid errors
                )
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("POST /auth/register when service throws IllegalStateException returns register view with error message")
    void registerUser_serviceThrowsIllegalStateException_returnsRegisterViewWithError() throws Exception {
        String errorMessage = "Username already taken";
        when(userService.registerUser(any(RegistrationRequest.class)))
                .thenThrow(new IllegalStateException(errorMessage));

        mockMvc.perform(post("/auth/register")
                        .param("username", "john@example.com")   // must be valid email
                        .param("password", "secret123")
                        .param("confirmPassword", "secret123"))  // was missing entirely
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("errorMessage", errorMessage));

        verify(userService).registerUser(any(RegistrationRequest.class));
    }

    // --- GET /auth/login ---

    @Test
    @DisplayName("GET /auth/login returns login view")
    void loginPage_returnsLoginView() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @DisplayName("GET /auth/login?registered returns login view (Spring Security param passthrough)")
    void loginPage_withRegisteredParam_returnsLoginView() throws Exception {
        mockMvc.perform(get("/auth/login").param("registered", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    // --- GET /auth/logout-success ---

    @Test
    @DisplayName("GET /auth/logout-success returns logout-success view")
    void logoutSuccess_returnsLogoutSuccessView() throws Exception {
        mockMvc.perform(get("/auth/logout-success"))
                .andExpect(status().isOk())
                .andExpect(view().name("logout-success"));
    }
}