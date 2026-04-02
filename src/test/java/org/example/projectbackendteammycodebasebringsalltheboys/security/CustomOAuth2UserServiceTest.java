package org.example.projectbackendteammycodebasebringsalltheboys.security;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private RoleRepository roleRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate;

  @Mock private OAuth2UserRequest userRequest;

  @Mock private OAuth2User oauthUser;

  private CustomOAuth2UserService service;

  @BeforeEach
  void setUp() {
    service = new CustomOAuth2UserService(userRepository, roleRepository, passwordEncoder);
    service.setDelegate(delegate);

    ClientRegistration.ProviderDetails.UserInfoEndpoint userInfoEndpoint =
        mock(ClientRegistration.ProviderDetails.UserInfoEndpoint.class);
    lenient().when(userInfoEndpoint.getUserNameAttributeName()).thenReturn("email");

    ClientRegistration.ProviderDetails providerDetails =
        mock(ClientRegistration.ProviderDetails.class);
    lenient().when(providerDetails.getUserInfoEndpoint()).thenReturn(userInfoEndpoint);

    ClientRegistration clientRegistration = mock(ClientRegistration.class);
    lenient().when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);

    lenient().when(userRequest.getClientRegistration()).thenReturn(clientRegistration);
  }

  // --- loadUser: email null ---

  @Test
  @DisplayName(
      "loadUser throws OAuth2AuthenticationException when OAuth2 provider returns no email")
  void loadUser_nullEmail_throwsIllegalStateException() {
    OAuth2AccessToken accessToken = mock(OAuth2AccessToken.class);
    when(userRequest.getAccessToken()).thenReturn(accessToken);
    when(accessToken.getTokenValue()).thenReturn("dummy-token");

    when(delegate.loadUser(userRequest)).thenReturn(oauthUser);
    when(oauthUser.getAttribute("email")).thenReturn(null);

    assertThatThrownBy(() -> service.loadUser(userRequest))
        .isInstanceOf(OAuth2AuthenticationException.class)
        .hasMessage("Email not provided by OAuth2 provider and could not be fetched");

    verifyNoInteractions(userRepository, roleRepository);
  }

  // --- loadUser: existing user ---

  @Test
  @DisplayName("loadUser returns existing OAuth2User when user already exists in DB")
  void loadUser_existingUser_returnsOAuthUserWithoutCreating() {
    String email = "existing@example.com";

    Role role = new Role();
    role.setName("ROLE_STUDENT");

    User existingUser = new User();
    existingUser.setUsername(email);
    existingUser.setRole(role);

    when(delegate.loadUser(userRequest)).thenReturn(oauthUser);
    when(oauthUser.getAttribute("email")).thenReturn(email);
    when(oauthUser.getAttribute("login")).thenReturn("testuser");
    when(oauthUser.getAttributes()).thenReturn(Map.of("email", email, "login", "testuser"));
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

    OAuth2User result = service.loadUser(userRequest);

    assertThat(result).isInstanceOf(DefaultOAuth2User.class);
    assertThat(result.getAuthorities())
        .extracting(GrantedAuthority::getAuthority)
        .containsExactly("ROLE_STUDENT");
    verify(userRepository).findByEmail(email);
    verify(userRepository, never()).save(any());
    verifyNoInteractions(roleRepository);
  }

  // --- loadUser: new user ---

  @Test
  @DisplayName("loadUser creates and saves new user when email is not found in DB")
  void loadUser_newUser_createsAndSavesUser() {
    String email = "new@example.com";
    Role role = new Role();
    role.setName("ROLE_STUDENT");

    User savedUser = new User();
    savedUser.setUsername(email);
    savedUser.setRole(role);

    when(delegate.loadUser(userRequest)).thenReturn(oauthUser);
    when(oauthUser.getAttribute("email")).thenReturn(email);
    when(oauthUser.getAttribute("login")).thenReturn("testuser");
    when(oauthUser.getAttributes()).thenReturn(Map.of("email", email, "login", "testuser"));
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
    when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.of(role));
    when(passwordEncoder.encode(anyString())).thenReturn("encoded-oauth-password");
    when(userRepository.save(any())).thenReturn(savedUser);

    OAuth2User result = service.loadUser(userRequest);

    assertThat(result).isInstanceOf(DefaultOAuth2User.class);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());

    User captured = userCaptor.getValue();
    assertThat(captured.getUsername()).isEqualTo("testuser");
    assertThat(captured.getRole()).isEqualTo(role);
    assertThat(captured.getPassword()).isNotNull().isNotEmpty();
    verify(passwordEncoder).encode(anyString());
  }

  @Test
  @DisplayName("loadUser returns the OAuth2User after creating new user")
  void loadUser_newUser_returnsOAuthUser() {
    String email = "new@example.com";
    Role role = new Role();
    role.setName("ROLE_STUDENT");

    User savedUser = new User();
    savedUser.setUsername(email);
    savedUser.setRole(role);

    when(delegate.loadUser(userRequest)).thenReturn(oauthUser);
    when(oauthUser.getAttribute("email")).thenReturn(email);
    when(oauthUser.getAttribute("login")).thenReturn("testuser");
    when(oauthUser.getAttributes()).thenReturn(Map.of("email", email, "login", "testuser"));
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
    when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.of(role));
    when(passwordEncoder.encode(anyString())).thenReturn("encoded-oauth-password");
    when(userRepository.save(any())).thenReturn(savedUser);

    OAuth2User result = service.loadUser(userRequest);

    assertThat(result).isInstanceOf(DefaultOAuth2User.class);
    assertThat(result.getAuthorities())
        .extracting(GrantedAuthority::getAuthority)
        .containsExactly("ROLE_STUDENT");

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded-oauth-password");
    verify(passwordEncoder).encode(anyString());
  }

  @Test
  @DisplayName("loadUser throws OAuth2AuthenticationException when ROLE_STUDENT is missing from DB")
  void loadUser_newUser_missingDefaultRole_throwsOAuth2AuthenticationException() {
    String email = "new@example.com";

    when(delegate.loadUser(userRequest)).thenReturn(oauthUser);
    when(oauthUser.getAttribute("login")).thenReturn("testuser");
    when(oauthUser.getAttribute("email")).thenReturn(email);
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
    when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.loadUser(userRequest))
        .isInstanceOf(OAuth2AuthenticationException.class)
        .hasMessage("Default role not found");

    verify(userRepository, never()).save(any());
  }

  // --- delegate interaction ---

  @Test
  @DisplayName("loadUser always delegates to the upstream OAuth2UserService first")
  void loadUser_alwaysCallsDelegate() {
    Role role = new Role();
    role.setName("ROLE_STUDENT");

    User existingUser = new User();
    existingUser.setRole(role);

    when(delegate.loadUser(userRequest)).thenReturn(oauthUser);
    when(oauthUser.getAttribute("email")).thenReturn("any@example.com");
    when(oauthUser.getAttribute("login")).thenReturn("testuser");
    when(oauthUser.getAttributes())
        .thenReturn(Map.of("email", "any@example.com", "login", "testuser"));
    when(userRepository.findByEmail(any())).thenReturn(Optional.of(existingUser));

    service.loadUser(userRequest);

    verify(delegate).loadUser(userRequest);
  }
}
