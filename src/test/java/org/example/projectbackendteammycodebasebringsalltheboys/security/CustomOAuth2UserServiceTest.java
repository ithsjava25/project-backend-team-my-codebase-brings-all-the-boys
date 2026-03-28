package org.example.projectbackendteammycodebasebringsalltheboys.security;

import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.RoleRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.security.oauth.CustomOAuth2UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate;

    @Mock
    private OAuth2UserRequest userRequest;

    @Mock
    private OAuth2User oauthUser;

    private CustomOAuth2UserService service;

    @BeforeEach
    void setUp() {
        service = new CustomOAuth2UserService(userRepository, roleRepository);
        service.setDelegate(delegate);
    }

    // --- loadUser: email null ---

    @Test
    @DisplayName("loadUser throws IllegalStateException when OAuth2 provider returns no email")
    void loadUser_nullEmail_throwsIllegalStateException() {
        when(delegate.loadUser(userRequest)).thenReturn(oauthUser);
        when(oauthUser.getAttribute("email")).thenReturn(null);

        assertThatThrownBy(() -> service.loadUser(userRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Email not provided by OAuth2 provider");

        verifyNoInteractions(userRepository, roleRepository);
    }

    // --- loadUser: existing user ---

    @Test
    @DisplayName("loadUser returns existing OAuth2User when user already exists in DB")
    void loadUser_existingUser_returnsOAuthUserWithoutCreating() {
        String email = "existing@example.com";
        when(delegate.loadUser(userRequest)).thenReturn(oauthUser);
        when(oauthUser.getAttribute("email")).thenReturn(email);
        when(userRepository.findByUsername(email)).thenReturn(Optional.of(new User()));

        OAuth2User result = service.loadUser(userRequest);

        assertThat(result).isSameAs(oauthUser);
        verify(userRepository).findByUsername(email);
        verify(userRepository, never()).save(any());
        verifyNoInteractions(roleRepository);
    }

    // --- loadUser: new user ---

    @Test
    @DisplayName("loadUser creates and saves new user when email is not found in DB")
    void loadUser_newUser_createsAndSavesUser() {
        String email = "new@example.com";
        Role role = new Role();
        role.setName("ROLE_USER");

        when(delegate.loadUser(userRequest)).thenReturn(oauthUser);
        when(oauthUser.getAttribute("email")).thenReturn(email);
        when(userRepository.findByUsername(email)).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));

        OAuth2User result = service.loadUser(userRequest);

        assertThat(result).isSameAs(oauthUser);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo(email);
        assertThat(savedUser.getRole()).isEqualTo(role);
    }

    @Test
    @DisplayName("loadUser returns the OAuth2User after creating new user")
    void loadUser_newUser_returnsOAuthUser() {
        String email = "new@example.com";
        Role role = new Role();
        role.setName("ROLE_USER");

        when(delegate.loadUser(userRequest)).thenReturn(oauthUser);
        when(oauthUser.getAttribute("email")).thenReturn(email);
        when(userRepository.findByUsername(email)).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));

        OAuth2User result = service.loadUser(userRequest);

        assertThat(result).isSameAs(oauthUser);
    }

    @Test
    @DisplayName("loadUser throws IllegalStateException when ROLE_USER is missing from DB")
    void loadUser_newUser_missingDefaultRole_throwsIllegalStateException() {
        String email = "new@example.com";

        when(delegate.loadUser(userRequest)).thenReturn(oauthUser);
        when(oauthUser.getAttribute("email")).thenReturn(email);
        when(userRepository.findByUsername(email)).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUser(userRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Default role not found");

        verify(userRepository, never()).save(any());
    }

    // --- delegate interaction ---

    @Test
    @DisplayName("loadUser always delegates to the upstream OAuth2UserService first")
    void loadUser_alwaysCallsDelegate() {
        when(delegate.loadUser(userRequest)).thenReturn(oauthUser);
        when(oauthUser.getAttribute("email")).thenReturn("any@example.com");
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User()));

        service.loadUser(userRequest);

        verify(delegate).loadUser(userRequest);
    }
}