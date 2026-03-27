package org.example.projectbackendteammycodebasebringsalltheboys.security;

import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.RoleRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.UserRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.security.oauth.CustomOAuth2UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private CustomOAuth2UserService service;

    private OAuth2UserRequest buildRequest() {
        ClientRegistration registration = ClientRegistration
                .withRegistrationId("google")
                .clientId("client-id")
                .clientSecret("client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost/login/oauth2/code/google")
                .scope("email", "profile")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://openidconnect.googleapis.com/v1/userinfo")
                .userNameAttributeName("email")
                .build();

        OAuth2AccessToken token = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "token",
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );

        return new OAuth2UserRequest(registration, token);
    }

    @Test
    void createsNewUserWhenNotExists() {
        OAuth2UserRequest request = buildRequest();

        OAuth2User oauthUser = mock(OAuth2User.class);
        when(oauthUser.getAttribute("email")).thenReturn("new@example.com");

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = mock(OAuth2UserService.class);
        when(delegate.loadUser(request)).thenReturn(oauthUser);

        service.setDelegate(delegate);

        when(userRepository.findByUsername("new@example.com"))
                .thenReturn(Optional.empty());

        Role role = new Role();
        role.setName("ROLE_USER");

        when(roleRepository.findByName("ROLE_USER"))
                .thenReturn(Optional.of(role));

        when(userRepository.save(any(User.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        OAuth2User result = service.loadUser(request);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void returnsExistingUserWhenExists() {
        OAuth2UserRequest request = buildRequest();

        OAuth2User oauthUser = mock(OAuth2User.class);
        when(oauthUser.getAttribute("email")).thenReturn("existing@example.com");

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = mock(OAuth2UserService.class);
        when(delegate.loadUser(request)).thenReturn(oauthUser);

        service.setDelegate(delegate);

        User existing = new User();
        existing.setUsername("existing@example.com");

        when(userRepository.findByUsername("existing@example.com"))
                .thenReturn(Optional.of(existing));

        OAuth2User result = service.loadUser(request);

        assertNotNull(result);
        verify(userRepository, never()).save(any());
    }
}
